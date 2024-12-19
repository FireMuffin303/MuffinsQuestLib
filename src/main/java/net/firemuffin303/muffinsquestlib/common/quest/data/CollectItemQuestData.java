package net.firemuffin303.muffinsquestlib.common.quest.data;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.firemuffin303.muffinsquestlib.common.quest.QuestType;
import net.firemuffin303.muffinsquestlib.common.registry.ModQuestTypes;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public record CollectItemQuestData(ItemStack itemStack) implements QuestData{
    public static final Codec<CollectItemQuestData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.fieldOf("itemstack_requirements").forGetter(CollectItemQuestData::itemStack)
    ).apply(instance,CollectItemQuestData::new));



    @Override
    public int getRequirementAmount() {
        return this.itemStack.getCount();
    }

    @Override
    public void toPacket(PacketByteBuf packetByteBuf) {
        packetByteBuf.writeItemStack(this.itemStack);
    }

    @Override
    public boolean checkKillOther(ServerPlayerEntity serverPlayerEntity, ServerWorld world, LivingEntity target) {
        return false;
    }

    @Override
    public boolean checkItem(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack) {
        return (itemStack.isOf(this.itemStack.getItem()));
    }

    @Override
    public String toString() {
        return itemStack.getTranslationKey();
    }

    @Override
    public void tooltipRender(TextRenderer textRenderer, int x, int y, DrawContext context) {
        Text text = Text.translatable("item.quest_paper.tooltip.collect_item",this.itemStack.getCount(),Text.translatable(itemStack.getTranslationKey()).getString());
        context.drawText(textRenderer,text,x,y, 16755200,false) ;
    }

    @Override
    public int getTextWidth(TextRenderer textRenderer) {
        Text text = Text.translatable("item.quest_paper.tooltip.collect_item",this.itemStack.getCount(),Text.translatable(itemStack.getTranslationKey()).getString());
        return textRenderer.getWidth(text);
    }

    @Override
    public QuestType<?> getType() {
        return ModQuestTypes.COLLECT_ITEM_DATA;
    }

    //Not Done
    @Override
    public void onQuestDone(PlayerEntity player) {
        PlayerInventory inventory = player.getInventory();
        int required = this.itemStack.getCount();
        for(int i = 0 ; i < inventory.size();i++){
            ItemStack invStack = inventory.getStack(i);
            if(ItemStack.areItemsEqual(invStack,this.itemStack)){
                required = Math.max(required - invStack.getCount(),0);
                player.getInventory().getStack(i).decrement(required);
            }

            if(required <= 0){
                break;
            }
        }
    }

    @Override
    public void onQuestFailed(PlayerEntity player) {

    }

    @Override
    public <T extends QuestData> Codec<T> getCodec() {
        return null;
    }


    public static CollectItemQuestData fromPacket(PacketByteBuf packetByteBuf) {
        return new CollectItemQuestData(packetByteBuf.readItemStack());
    }
}
