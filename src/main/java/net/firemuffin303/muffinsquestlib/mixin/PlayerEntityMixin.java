package net.firemuffin303.muffinsquestlib.mixin;

import net.firemuffin303.muffinsquestlib.common.quest.PlayerQuestData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements PlayerQuestData.PlayerQuestDataAccessor {
    @Unique PlayerQuestData questData = new PlayerQuestData((PlayerEntity)(Object)this);


    @Inject(method = "tick", at = @At("TAIL"))
    public void questLib$tick(CallbackInfo ci){
        this.questData.tick();
    }

    @Inject(method = "writeCustomDataToNbt",at = @At("TAIL"))
    public void questLib$writeCustomDatatoNbt(NbtCompound nbt, CallbackInfo ci){
        this.questData.writeCustomDataToNbt(nbt);
    }

    @Inject(method = "readCustomDataFromNbt",at = @At("TAIL"))
    public void questLib$readCustomDataToNbt(NbtCompound nbt, CallbackInfo ci){
        if(nbt.contains("QuestInstance")){
            this.questData.readCustomDataToNbt(nbt);
        }
    }

    @Unique
    public PlayerQuestData questLib$getData(){
        return questData;
    }
}
