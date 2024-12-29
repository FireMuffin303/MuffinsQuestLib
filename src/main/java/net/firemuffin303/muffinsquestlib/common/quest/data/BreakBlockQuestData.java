package net.firemuffin303.muffinsquestlib.common.quest.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.firemuffin303.muffinsquestlib.api.data.QuestData;
import net.firemuffin303.muffinsquestlib.common.quest.QuestType;
import net.firemuffin303.muffinsquestlib.common.registry.QuestTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public record BreakBlockQuestData(BlockRequirementEntry blockRequirementEntry) implements QuestData {
    public static final Codec<BreakBlockQuestData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockRequirementEntry.CODEC.fieldOf("block_requirement").forGetter(BreakBlockQuestData::blockRequirementEntry)
    ).apply(instance, BreakBlockQuestData::new));

    @Override
    public void onQuestDone(PlayerEntity player) {

    }

    @Override
    public void onQuestFailed(PlayerEntity player) {

    }

    @Override
    public int getRequirementAmount() {
        return this.blockRequirementEntry.amount();
    }

    @Override
    public void tooltipRender(TextRenderer textRenderer, int x, int y, DrawContext context) {
        Text text = Text.translatable("item.quest_paper.tooltip.break_block",this.getRequirementAmount(),Text.translatable(this.blockRequirementEntry.block().getTranslationKey()).getString());
        context.drawText(textRenderer,text,x,y,16755200,false);
    }

    @Override
    public int getTextWidth(TextRenderer textRenderer) {
        Text text = Text.translatable("item.quest_paper.tooltip.break_block",this.getRequirementAmount(),Text.translatable(this.blockRequirementEntry.block().getTranslationKey()).getString());
        return textRenderer.getWidth(text);
    }

    @Override
    public Sprite getIcon() {
        BakedModel itemModels = MinecraftClient.getInstance().getItemRenderer().getModels().getModel(Items.IRON_PICKAXE);
        return itemModels.getParticleSprite();
    }

    @Override
    public void toPacket(PacketByteBuf packetByteBuf) {
        packetByteBuf.writeIdentifier(Registries.BLOCK.getId(this.blockRequirementEntry.block()));
        packetByteBuf.writeInt(this.blockRequirementEntry.amount());
    }

    public static BreakBlockQuestData fromPacket(PacketByteBuf packetByteBuf){
        Block block = Registries.BLOCK.get(packetByteBuf.readIdentifier());
        int amount = packetByteBuf.readInt();
        return new BreakBlockQuestData(new BlockRequirementEntry(block,amount));
    }

    @Override
    public QuestType<?> getType() {
        return QuestTypes.BREAK_BLOCK_DATA;
    }

    public boolean checkBlock(ServerPlayerEntity serverPlayerEntity, BlockState blockState) {
        return blockState.isOf(this.blockRequirementEntry.block());
    }

    @Override
    public String toString() {
        return this.blockRequirementEntry.block().getTranslationKey();
    }
}
