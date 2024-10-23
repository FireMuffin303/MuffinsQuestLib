package net.firemuffin303.muffinsquestlib.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.firemuffin303.muffinsquestlib.common.quest.condition.QuestConitions;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryChangedCriterion.class)
public abstract class InventoryChangedCriterionMixin {

    @Inject(method = "trigger(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/item/ItemStack;)V" ,
            at = @At("TAIL"))
    public void muffinsQuestLib$trigger(ServerPlayerEntity player, PlayerInventory inventory, ItemStack stack, CallbackInfo ci,
                                        @Local(ordinal = 0) LocalIntRef full,@Local(ordinal = 1) LocalIntRef empty,@Local(ordinal = 2) LocalIntRef occupied){
        QuestConitions.COLLECT_ITEM_CONDITION.trigger(player,stack);
    }
}
