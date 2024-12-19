package net.firemuffin303.muffinsquestlib.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.logging.LogUtils;
import net.firemuffin303.muffinsquestlib.common.PlayerQuestData;
import net.firemuffin303.muffinsquestlib.common.quest.QuestInstance;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.UUID;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow @Nullable public ClientPlayerEntity player;

    @ModifyReturnValue(method = "hasOutline",at = @At("RETURN"))
    public boolean questLib$isQuestEntity(boolean original, @Local(argsOnly = true)Entity entity){
        if(this.player != null && ((PlayerQuestData.PlayerQuestDataAccessor)this.player).questLib$getData().getQuestInstance() != null){
            QuestInstance questInstance = ((PlayerQuestData.PlayerQuestDataAccessor)this.player).questLib$getData().getQuestInstance();
            if(questInstance == null || questInstance.getQuestEntitiesUUID().isEmpty()){
                return original;
            }

            if(questInstance.getQuestEntitiesUUID().contains(entity.getUuid())){
                return true;
            }
        }
        return original;
    }
}
