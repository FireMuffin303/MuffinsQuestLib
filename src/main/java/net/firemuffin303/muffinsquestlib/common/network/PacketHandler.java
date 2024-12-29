package net.firemuffin303.muffinsquestlib.common.network;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.firemuffin303.muffinsquestlib.MuffinsQuestLib;
import net.firemuffin303.muffinsquestlib.common.PlayerQuestData;
import net.firemuffin303.muffinsquestlib.common.QuestEntityData;
import net.firemuffin303.muffinsquestlib.common.quest.QuestInstance;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

import java.util.Objects;
import java.util.UUID;

public class PacketHandler {

    public static void clientInit(){
        ClientPlayNetworking.registerGlobalReceiver(UpdateQuestInstancePacket.TYPE,PacketHandler::onUpdateQuest);
        ClientPlayNetworking.registerGlobalReceiver(ClearQuestInstancePacket.TYPE,PacketHandler::onClearQuest);
        ClientPlayNetworking.registerGlobalReceiver(QuestEntityPacket.TYPE,PacketHandler::onQuestEntityDataUpdate);
        ClientPlayNetworking.registerGlobalReceiver(QuestManagerUpdatePacket.TYPE,PacketHandler::onQuestManagerUpdate);
    }

    public static void onUpdateQuest(UpdateQuestInstancePacket updateQuestInstancePacket,ClientPlayerEntity clientPlayerEntity,PacketSender sender){
        QuestInstance questInstance = updateQuestInstancePacket.questInstance;
        ((PlayerQuestData.PlayerQuestDataAccessor) clientPlayerEntity).questLib$getData().setQuestInstance(questInstance);
    }

    public static void onClearQuest(ClearQuestInstancePacket clearQuestInstancePacket, ClientPlayerEntity clientPlayerEntity, PacketSender sender){
        ((PlayerQuestData.PlayerQuestDataAccessor)clientPlayerEntity).questLib$getData().setQuestInstance(null);
    }

    public static void onQuestEntityDataUpdate(QuestEntityPacket questEntityPacket,ClientPlayerEntity clientPlayerEntity,PacketSender sender){
        ClientWorld clientWorld = MinecraftClient.getInstance().world;
        Objects.requireNonNull(clientWorld);
        Entity entity = clientWorld.getEntityById(questEntityPacket.mobId);
        if(entity instanceof QuestEntityData.QuestEntityDataAccessor accessor){
            accessor.getQuestEntityData().setPlayerUUID(questEntityPacket.player.equals(new UUID(0,0)) ? null : questEntityPacket.player);
            accessor.getQuestEntityData().setQuestMarked(questEntityPacket.questMarked);
        }
    }

    public static void onQuestManagerUpdate(QuestManagerUpdatePacket questManagerUpdatePacket,ClientPlayerEntity clientPlayerEntity,PacketSender packetSender){
        MuffinsQuestLib.QUEST_MANAGER.setQuestById(questManagerUpdatePacket.getQuestMap());
    }


}
