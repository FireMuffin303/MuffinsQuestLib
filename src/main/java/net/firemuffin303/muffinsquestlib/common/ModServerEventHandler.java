package net.firemuffin303.muffinsquestlib.common;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.firemuffin303.muffinsquestlib.MuffinsQuestLib;
import net.firemuffin303.muffinsquestlib.api.QuestTradeOffers;
import net.firemuffin303.muffinsquestlib.common.network.UpdateQuestInstancePacket;
import net.firemuffin303.muffinsquestlib.common.quest.PlayerQuestData;
import net.firemuffin303.muffinsquestlib.common.quest.QuestInstance;
import net.firemuffin303.muffinsquestlib.common.quest.condition.QuestConitions;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.TradeOffers;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class ModServerEventHandler {

    public static void init(){
        //Quest Progression Event
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(ModServerEventHandler::onLivingEntityAfterDeath);
        PlayerBlockBreakEvents.AFTER.register(ModServerEventHandler::onPlayerBreakBlock);

        ServerEntityEvents.ENTITY_LOAD.register(ModServerEventHandler::onEntityLoaded);
        ServerEntityEvents.ENTITY_UNLOAD.register(ModServerEventHandler::onEntityUnload);

        //Sync Player's Quest Data
        ServerPlayerEvents.AFTER_RESPAWN.register(ModServerEventHandler::onPlayerRespawn);
        ServerPlayerEvents.COPY_FROM.register(ModServerEventHandler::onPlayerCopyFrom);
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(ModServerEventHandler::onPlayerChangeDimension);
        ServerPlayConnectionEvents.JOIN.register(ModServerEventHandler::onPlayerJoined);

        TradeOfferHelper.registerWanderingTraderOffers(1,ModServerEventHandler::onWanderingTraderTradeModify);

        ServerPlayNetworking.registerGlobalReceiver(MuffinsQuestLib.modId("clear_quest_c2s"),(minecraftServer, serverPlayerEntity, serverPlayNetworkHandler, packetByteBuf, packetSender) -> {
            if(((PlayerQuestData.PlayerQuestDataAccessor)serverPlayerEntity).questLib$getData().getQuestInstance() != null){
                minecraftServer.execute(() -> ((PlayerQuestData.PlayerQuestDataAccessor)serverPlayerEntity).questLib$getData().getQuestInstance().setState(QuestInstance.State.FAIL));
            }
        });

    }

    private static void onPlayerBreakBlock(World world, PlayerEntity player, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity){
        if(player instanceof ServerPlayerEntity serverPlayerEntity){
            QuestConitions.BREAK_BLOCK_CONDITION.trigger(serverPlayerEntity,blockState);
        }
    }

    private static void onEntityLoaded(Entity entity,ServerWorld serverWorld){
        // Send Entity Quest Marked Data after loaded
        if(entity instanceof QuestEntityData.QuestEntityDataAccessor accessor){
            if(accessor.getQuestEntityData().isQuestMarked()){
                for(ServerPlayerEntity serverPlayerEntity : serverWorld.getPlayers()){
                    accessor.getQuestEntityData().updatePacket(serverPlayerEntity);
                }
            }
        }
        //-------------------------------------------
    }

    private static void onEntityUnload(Entity entity, ServerWorld serverWorld){
        //Remove Entity UUID list from Player's Quest Instance
        if(entity instanceof QuestEntityData.QuestEntityDataAccessor questEntityDataAccessor){
            QuestEntityData questEntityData = questEntityDataAccessor.getQuestEntityData();
            if(questEntityData.isQuestMarked() && questEntityData.getPlayerUUID() != null){
                ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) entity.getWorld().getPlayerByUuid(questEntityData.getPlayerUUID());
                if(serverPlayerEntity != null){
                    QuestInstance questInstance = ((PlayerQuestData.PlayerQuestDataAccessor)serverPlayerEntity).questLib$getData().getQuestInstance();
                    if(questInstance != null && questInstance.getQuestEntitiesUUID().contains(entity.getUuid())){
                        questInstance.removeQuestEntity(entity.getUuid(),serverPlayerEntity);
                        ServerPlayNetworking.send(serverPlayerEntity,new UpdateQuestInstancePacket(questInstance));
                    }
                }
            }

        }
        //----------------------------------------------------

    }

    private static void onPlayerRespawn(ServerPlayerEntity oldPlayer,ServerPlayerEntity newPlayer,boolean alive){
        QuestInstance questInstance = ((PlayerQuestData.PlayerQuestDataAccessor)oldPlayer).questLib$getData().getQuestInstance();
        if(questInstance != null){
            ServerPlayNetworking.send(newPlayer,new UpdateQuestInstancePacket(questInstance));
        }

    }

    private static void onPlayerCopyFrom(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer,boolean alive){
        QuestInstance questInstance = ((PlayerQuestData.PlayerQuestDataAccessor)oldPlayer).questLib$getData().getQuestInstance();
        if(questInstance != null){
            ((PlayerQuestData.PlayerQuestDataAccessor)newPlayer).questLib$getData().setQuestInstance(questInstance);
        }

    }

    private static void onPlayerChangeDimension(ServerPlayerEntity serverPlayerEntity,ServerWorld fromWorld, ServerWorld toWorld){
        QuestInstance questInstance = ((PlayerQuestData.PlayerQuestDataAccessor)serverPlayerEntity).questLib$getData().getQuestInstance();
        if(questInstance != null){
            for(UUID uuid :  questInstance.getQuestEntitiesUUID()){
                Entity entity = fromWorld.getEntity(uuid);
                if(entity != null){
                    entity.discard();
                }
            }

            ServerPlayNetworking.send(serverPlayerEntity,new UpdateQuestInstancePacket(questInstance));
        }
    }

    private static void onPlayerJoined(ServerPlayNetworkHandler serverPlayNetworkHandler, PacketSender packetSender, MinecraftServer minecraftServer){
        //Sync Player's Quest Data
        if(((PlayerQuestData.PlayerQuestDataAccessor)serverPlayNetworkHandler.player).questLib$getData().hasQuest()){
            minecraftServer.execute(() -> ServerPlayNetworking.send(serverPlayNetworkHandler.player,
                    new UpdateQuestInstancePacket(((PlayerQuestData.PlayerQuestDataAccessor)serverPlayNetworkHandler.player).questLib$getData().getQuestInstance())));
        }
    }

    private static void onLivingEntityAfterDeath(ServerWorld serverWorld,Entity attacker,LivingEntity killedEntity){
        if(attacker instanceof ServerPlayerEntity serverPlayerEntity){
            QuestConitions.KILL_MOB_CONDITION.trigger(serverPlayerEntity, serverWorld,killedEntity);
        }
    }

    private static void onWanderingTraderTradeModify(List<TradeOffers.Factory> factories){
        factories.add(new QuestTradeOffers.WanderingTraderQuestOffer(Items.EMERALD,16,1));
    }
}
