package net.firemuffin303.muffinsquestlib.mixin;

import net.firemuffin303.muffinsquestlib.common.PlayerQuestData;
import net.firemuffin303.muffinsquestlib.common.quest.condition.QuestConitions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements PlayerQuestData.PlayerQuestDataAccessor {
    @Unique PlayerQuestData questData = new PlayerQuestData((PlayerEntity)(Object)this);


    @Inject(method = "tick", at = @At("TAIL"))
    public void questLib$tick(CallbackInfo ci){
        this.questData.tick();
    }

    @Inject(method = "onKilledOther", at = @At("TAIL"))
    public void questLib$onKilledOther(ServerWorld world, LivingEntity other, CallbackInfoReturnable<Boolean> cir){
        if(((PlayerEntity)(Object)this) instanceof ServerPlayerEntity serverPlayerEntity){
            QuestConitions.KILL_MOB_CONDITION.trigger(serverPlayerEntity,world,other);
        }
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
