package net.firemuffin303.muffinsquestlib.common;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.impl.util.log.Log;
import net.firemuffin303.muffinsquestlib.common.network.QuestEntityPacket;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

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

    //I'll find a way to optimise data sync later
    public void tick(){
        if(this.mob.age % 5 == 0 && !this.mob.getWorld().isClient){
            ServerWorld serverWorld = (ServerWorld) this.mob.getWorld();
            for(ServerPlayerEntity serverPlayerEntity : serverWorld.getPlayers()){
                if(serverPlayerEntity.isInRange(this.mob,64)){
                    ServerPlayNetworking.send(serverPlayerEntity,new QuestEntityPacket(this.mob,this.playerUUID,this.questMarked));
                }
            }
        }
    }

    public void updatePacket(ServerPlayerEntity serverPlayerEntity){
        if(this.playerUUID != null && !this.mob.getWorld().isClient){
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
