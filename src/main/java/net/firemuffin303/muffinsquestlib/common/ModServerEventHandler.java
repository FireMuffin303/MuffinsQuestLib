package net.firemuffin303.muffinsquestlib.common;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.S2CPlayChannelEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.firemuffin303.muffinsquestlib.common.network.UpdateQuestInstancePacket;
import net.firemuffin303.muffinsquestlib.common.quest.QuestInstance;
import net.firemuffin303.muffinsquestlib.common.quest.condition.QuestConitions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.UUID;

public class ModServerEventHandler {

    public static void init(){
        ServerEntityEvents.ENTITY_LOAD.register(ModServerEventHandler::onEntityLoaded);
        ServerEntityEvents.ENTITY_UNLOAD.register(ModServerEventHandler::onEntityUnload);



        //Sync Player's Quest Data
        ServerPlayerEvents.AFTER_RESPAWN.register(ModServerEventHandler::onPlayerRespawn);
        ServerPlayerEvents.COPY_FROM.register(ModServerEventHandler::onPlayerCopyFrom);
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(ModServerEventHandler::onPlayerChangeDimension);
        ServerPlayConnectionEvents.JOIN.register(ModServerEventHandler::onPlayerJoined);

        //TODO : Use this event instead of mixin for quest progressing.
        //Why am I not check fabric api event first. goddamit.
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(ModServerEventHandler::onLivingEntityAfterDeath);
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
}
