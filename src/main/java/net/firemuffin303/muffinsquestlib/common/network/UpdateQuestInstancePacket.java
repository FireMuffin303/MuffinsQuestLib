package net.firemuffin303.muffinsquestlib.common.network;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.firemuffin303.muffinsquestlib.MuffinsQuestLib;
import net.firemuffin303.muffinsquestlib.common.quest.QuestInstance;
import net.minecraft.network.PacketByteBuf;

public class UpdateQuestInstancePacket implements FabricPacket {
    public static final PacketType<UpdateQuestInstancePacket> TYPE = PacketType.create(MuffinsQuestLib.modId("add_quest_instance"), UpdateQuestInstancePacket::new);
    public final QuestInstance questInstance;

    public UpdateQuestInstancePacket(PacketByteBuf packetByteBuf){
        this(QuestInstance.fromPacket(packetByteBuf));
    }

    public UpdateQuestInstancePacket(QuestInstance questInstance) {
        this.questInstance = questInstance;
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) {
        questInstance.toPacket(packetByteBuf);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
