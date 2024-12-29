package net.firemuffin303.muffinsquestlib.common.network;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.firemuffin303.muffinsquestlib.MuffinsQuestLib;
import net.firemuffin303.muffinsquestlib.common.quest.Quest;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.Map;

public class QuestManagerUpdatePacket implements FabricPacket {
    public static final PacketType<QuestManagerUpdatePacket> TYPE = PacketType.create(MuffinsQuestLib.modId("quest_manager"), QuestManagerUpdatePacket::new);

    private Map<Identifier,Quest> questMap;

    public QuestManagerUpdatePacket(PacketByteBuf packetByteBuf){
        this(packetByteBuf.readMap(PacketByteBuf::readIdentifier, Quest::fromPacket));
    }

    public QuestManagerUpdatePacket(Map<Identifier, Quest> questMap){
        this.questMap = questMap;
    }


    @Override
    public void write(PacketByteBuf packetByteBuf) {
        packetByteBuf.writeMap(this.questMap,PacketByteBuf::writeIdentifier,(packetByteBuf1, quest) -> quest.toPacket(packetByteBuf));
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public Map<Identifier, Quest> getQuestMap() {
        return questMap;
    }
}
