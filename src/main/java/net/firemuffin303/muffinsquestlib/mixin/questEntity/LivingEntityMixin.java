package net.firemuffin303.muffinsquestlib.mixin.questEntity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.firemuffin303.muffinsquestlib.common.QuestEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Debug(export = true)
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @WrapOperation(method = "drop",at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;shouldDropLoot()Z"))
    public boolean questLib$checkIfQuestOwnerKill(LivingEntity instance, Operation<Boolean> original, @Local(argsOnly = true)DamageSource damageSource){
        if(instance instanceof QuestEntityData.QuestEntityDataAccessor questEntityDataAccessor){
            if(questEntityDataAccessor.getQuestEntityData().isQuestMarked()){
                if(damageSource.getAttacker() != null && damageSource.getAttacker().getUuid() == questEntityDataAccessor.getQuestEntityData().getPlayerUUID()){
                    return true;
                }else{
                    instance.disableExperienceDropping();
                    return false;
                }
            }
        }
        return original.call(instance);
    }
}
