package net.firemuffin303.muffinsquestlib.mixin.questEntity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.firemuffin303.muffinsquestlib.client.config.ModConfig;
import net.firemuffin303.muffinsquestlib.common.quest.PlayerQuestData;
import net.firemuffin303.muffinsquestlib.common.quest.QuestInstance;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow @Nullable public ClientPlayerEntity player;

    @ModifyReturnValue(method = "hasOutline",at = @At("RETURN"))
    public boolean questLib$hasOutline(boolean original, @Local(argsOnly = true)Entity entity){
        if(this.player instanceof PlayerQuestData.PlayerQuestDataAccessor accessor && ModConfig.QUEST_MOB_STYLE.getCurrentValue().isOutline()){
            QuestInstance questInstance = accessor.questLib$getData().getQuestInstance();
            if(questInstance != null && !questInstance.getQuestEntitiesUUID().isEmpty()){
                if(questInstance.getQuestEntitiesUUID().contains(entity.getUuid())){
                    return true;
                }
            }
        }

        return original;
    }
}
