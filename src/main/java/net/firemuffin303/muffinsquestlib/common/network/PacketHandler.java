package net.firemuffin303.muffinsquestlib.common.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.firemuffin303.muffinsquestlib.common.PlayerQuestData;
import net.firemuffin303.muffinsquestlib.common.quest.QuestInstance;
import net.minecraft.client.network.ClientPlayerEntity;

public class PacketHandler {

    public static void clientInit(){
        ClientPlayNetworking.registerGlobalReceiver(UpdateQuestInstancePacket.TYPE,PacketHandler::onUpdateQuest);
        ClientPlayNetworking.registerGlobalReceiver(ClearQuestInstancePacket.TYPE,PacketHandler::onClearQuest);
    }

    public static void onUpdateQuest(UpdateQuestInstancePacket updateQuestInstancePacket,ClientPlayerEntity clientPlayerEntity,PacketSender sender){
        QuestInstance questInstance = updateQuestInstancePacket.questInstance;
        ((PlayerQuestData.PlayerQuestDataAccessor) clientPlayerEntity).questLib$getData().setQuestInstance(questInstance);
    }

    public static void onClearQuest(ClearQuestInstancePacket clearQuestInstancePacket, ClientPlayerEntity clientPlayerEntity, PacketSender sender){
        ((PlayerQuestData.PlayerQuestDataAccessor)clientPlayerEntity).questLib$getData().setQuestInstance(null);
    }
}
