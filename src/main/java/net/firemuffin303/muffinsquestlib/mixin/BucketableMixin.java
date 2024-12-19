package net.firemuffin303.muffinsquestlib.mixin;

import com.mojang.logging.LogUtils;
import net.firemuffin303.muffinsquestlib.common.PlayerQuestData;
import net.firemuffin303.muffinsquestlib.common.QuestEntityData;
import net.firemuffin303.muffinsquestlib.common.quest.QuestInstance;
import net.minecraft.entity.Bucketable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Debug(export = true)
@Mixin(Bucketable.class)
public interface BucketableMixin {
    @Inject(method = "tryBucket",at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;playSound(Lnet/minecraft/sound/SoundEvent;FF)V"), cancellable = true)
    private static <T extends LivingEntity & Bucketable> void questLib$tryBucket(PlayerEntity player, Hand hand, T entity, CallbackInfoReturnable<Optional<ActionResult>> cir){
        if(entity instanceof QuestEntityData.QuestEntityDataAccessor questEntityDataAccessor ){
            LogUtils.getLogger().info(questEntityDataAccessor.getQuestEntityData().isQuestMarked() + "");
            if(questEntityDataAccessor.getQuestEntityData().isQuestMarked()){
                cir.setReturnValue(Optional.empty());
            }
        }
    }
}
