package net.firemuffin303.muffinsquestlib.common.network;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.firemuffin303.muffinsquestlib.MuffinsQuestLib;
import net.minecraft.network.PacketByteBuf;

public class ClearQuestInstancePacket implements FabricPacket {
    public static final PacketType<ClearQuestInstancePacket> TYPE = PacketType.create(MuffinsQuestLib.modId("clear_quest_instance"),ClearQuestInstancePacket::new);

    public ClearQuestInstancePacket(PacketByteBuf packetByteBuf){

    }

    public ClearQuestInstancePacket(){

    }

    @Override
    public void write(PacketByteBuf packetByteBuf) {

    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
