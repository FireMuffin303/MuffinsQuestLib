package net.firemuffin303.muffinsquestlib.common.network;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.firemuffin303.muffinsquestlib.MuffinsQuestLib;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class QuestEntityPacket implements FabricPacket {
    public static final PacketType<QuestEntityPacket> TYPE = PacketType.create(MuffinsQuestLib.modId("quest_entity_packet"), QuestEntityPacket::new);

    public int mobId;
    public UUID player;
    public boolean questMarked;

    public QuestEntityPacket(PacketByteBuf packetByteBuf){
        this.mobId = packetByteBuf.readInt();
        this.player = packetByteBuf.readUuid();
        this.questMarked = packetByteBuf.readBoolean();
    }

    public QuestEntityPacket(MobEntity mob, @Nullable UUID player, boolean questMarked){
        this.mobId = mob.getId();
        this.player = player == null ? new UUID(0,0) : player;
        this.questMarked = questMarked;
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) {
        packetByteBuf.writeInt(this.mobId);
        packetByteBuf.writeUuid(this.player);
        packetByteBuf.writeBoolean(this.questMarked);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
