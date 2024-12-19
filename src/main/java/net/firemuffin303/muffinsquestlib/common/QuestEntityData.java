package net.firemuffin303.muffinsquestlib.common;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.firemuffin303.muffinsquestlib.common.network.QuestEntityPacket;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class QuestEntityData {
    private UUID playerUUID;
    private boolean questMarked = false;
    private final MobEntity mob;

    public QuestEntityData(MobEntity mob){
        this.mob = mob;
    }

    public void setQuestMarked(boolean questMarked) {
        this.questMarked = questMarked;
        if(!this.mob.getWorld().isClient){

        }
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public boolean isQuestMarked(){
        return this.questMarked;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public MobEntity getMob() {
        return mob;
    }

    public void updatePacket(){
        if(this.playerUUID != null && !this.mob.getWorld().isClient){
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) this.mob.getWorld().getPlayerByUuid(this.playerUUID);
            if(serverPlayerEntity == null){
                return;
            }
            ServerPlayNetworking.send(serverPlayerEntity,new QuestEntityPacket(this.mob,this.playerUUID,this.questMarked));
        }

    }

    //NBT
    public void writeNbt(NbtCompound nbtCompound){
        NbtCompound data = new NbtCompound();

        if(this.playerUUID != null){
            data.putUuid("PlayerUUID",this.playerUUID);
        }
        data.putBoolean("QuestMarked",this.questMarked);
        nbtCompound.put("QuestEntity",data);
    }

    public void readNbt(NbtCompound nbtCompound){
        NbtCompound questEntityCompound = nbtCompound.getCompound("QuestEntity");

        if(questEntityCompound.contains("PlayerUUID")){
            this.playerUUID = questEntityCompound.getUuid("PlayerUUID");
        }
        this.questMarked = questEntityCompound.getBoolean("QuestMarked");

    }


    public interface QuestEntityDataAccessor{
        QuestEntityData getQuestEntityData();
    }
}
