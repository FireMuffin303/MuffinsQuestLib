package net.firemuffin303.muffinsquestlib.common.quest.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.firemuffin303.muffinsquestlib.common.quest.QuestType;
import net.firemuffin303.muffinsquestlib.common.registry.ModQuestTypes;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

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
    public void tooltipRender(TextRenderer textRenderer, int x, int y, DrawContext context) {
        context.drawItem(this.itemStack,x,y);
        context.drawItemInSlot(textRenderer,this.itemStack,x,y);
    }

    @Override
    public <T extends QuestData> Codec<T> getCodec() {
        return null;
    }

    @Override
    public QuestType<?> getType() {
        return ModQuestTypes.COLLECT_ITEM_DATA;
    }

    public static CollectItemQuestData fromPacket(PacketByteBuf packetByteBuf) {
        return new CollectItemQuestData(packetByteBuf.readItemStack());
    }
}
