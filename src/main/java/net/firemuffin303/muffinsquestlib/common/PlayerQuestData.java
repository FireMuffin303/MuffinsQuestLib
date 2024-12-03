package net.firemuffin303.muffinsquestlib.common;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.firemuffin303.muffinsquestlib.common.network.ClearQuestInstancePacket;
import net.firemuffin303.muffinsquestlib.common.network.UpdateQuestInstancePacket;
import net.firemuffin303.muffinsquestlib.common.quest.QuestInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import org.jetbrains.annotations.Nullable;

public class PlayerQuestData {
    @Nullable QuestInstance questInstance;
    private final PlayerEntity player;

    public PlayerQuestData(PlayerEntity playerEntity) {
        this.player = playerEntity;
    }

    public void tick() {
        if(this.questInstance != null){
            if(this.questInstance.getState() == QuestInstance.State.FAIL){
                if(this.player instanceof ServerPlayerEntity serverPlayerEntity){
                    this.clearQuest(serverPlayerEntity);
                }
            }else if(this.questInstance.getState() == QuestInstance.State.PROGRESSING){
                if(this.questInstance.getTime() <= 0 && !this.player.getWorld().isClient){
                    this.questInstance.setState(QuestInstance.State.FAIL);
                }else{
                    this.questInstance.time--;
                }
            } else if (this.questInstance.getState() == QuestInstance.State.SUCCESS && this.player instanceof ServerPlayerEntity serverPlayerEntity) {
                this.questInstance.getQuest().questTypes.forEach((questType, questData) -> questData.forEach(questData1 -> questData1.onQuestDone(this.player)));
                this.questInstance.getRewards().forEach(itemStack -> {
                    if(!serverPlayerEntity.giveItemStack(itemStack.copy())){
                        serverPlayerEntity.dropStack(itemStack.copy());
                    }

                    serverPlayerEntity.addExperience(this.getQuestInstance().getQuest().definition.experience());
                });
                serverPlayerEntity.playSound(SoundEvents.ENTITY_ARROW_HIT_PLAYER,1.0f,1.0f);
                this.clearQuest(serverPlayerEntity);
            }
        }
    }

    public boolean hasQuest(){
        return this.getQuestInstance() != null;
    }

    public void setQuestInstance(@Nullable QuestInstance questInstance){
        this.questInstance = questInstance;
        if(this.player instanceof ServerPlayerEntity serverPlayerEntity){
            ServerPlayNetworking.send(serverPlayerEntity,new UpdateQuestInstancePacket(this.questInstance));
        }
    }

    public void writeCustomDataToNbt(NbtCompound nbtCompound) {
        if(this.questInstance != null){
            this.questInstance.writeNbt(this.player,nbtCompound);
        }
    }

    public void readCustomDataToNbt(NbtCompound nbtCompound){
        this.questInstance = QuestInstance.readNbt(this.player,nbtCompound);
    }

    public @Nullable QuestInstance getQuestInstance() {
        return questInstance;
    }

    public void clearQuest(ServerPlayerEntity serverPlayerEntity) {
        ServerPlayNetworking.send(serverPlayerEntity,new ClearQuestInstancePacket());
        this.questInstance = null;
    }

    public interface PlayerQuestDataAccessor{
        PlayerQuestData questLib$getData();
    }
}
