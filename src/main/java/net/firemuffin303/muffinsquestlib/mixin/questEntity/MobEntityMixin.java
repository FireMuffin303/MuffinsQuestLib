package net.firemuffin303.muffinsquestlib.mixin.questEntity;

import net.firemuffin303.muffinsquestlib.common.QuestEntityData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity implements QuestEntityData.QuestEntityDataAccessor {
    @Shadow protected abstract boolean isDisallowedInPeaceful();

    @Unique
    private final QuestEntityData questEntityData = new QuestEntityData((MobEntity) (Object) this);


    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }


    @Inject(method = "checkDespawn",at = @At(value = "HEAD"), cancellable = true)
    public void questLib$checkDespawn(CallbackInfo ci){
        if(this.questEntityData.isQuestMarked()){
            if (this.getWorld().getDifficulty() == Difficulty.PEACEFUL && this.isDisallowedInPeaceful()) {
                this.discard();
            }else{
                Entity entity = this.getWorld().getPlayerByUuid(this.questEntityData.getPlayerUUID());
                if(entity != null){
                    double d = entity.squaredDistanceTo(this);
                    double j = 48 * 48;
                    if (d > j) {
                        this.discard();
                    }
                }else{
                    this.discard();
                }
            }
            ci.cancel();
        }
    }

    @Override
    public boolean canUsePortals() {
        if(this.questEntityData.isQuestMarked()){
            return false;
        }
        return super.canUsePortals();
    }

    @Inject(method = "tick",at = @At("TAIL"))
    public void questLib$tick(CallbackInfo ci){
        this.questEntityData.tick();
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    public void questLib$writeQuestEntity(NbtCompound nbt, CallbackInfo ci){
        this.questEntityData.writeNbt(nbt);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void questLib$readQuestEntity(NbtCompound nbt, CallbackInfo ci){
        this.questEntityData.readNbt(nbt);
    }

    @Override
    public QuestEntityData getQuestEntityData() {
        return this.questEntityData;
    }
}
