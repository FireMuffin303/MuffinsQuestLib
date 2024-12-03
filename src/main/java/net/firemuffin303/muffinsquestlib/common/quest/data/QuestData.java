package net.firemuffin303.muffinsquestlib.common.quest.data;

import com.mojang.serialization.Codec;
import net.firemuffin303.muffinsquestlib.common.quest.QuestType;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public interface QuestData {

    int getRequirementAmount();

    void toPacket(PacketByteBuf packetByteBuf);

    boolean checkKillOther(ServerPlayerEntity serverPlayerEntity, ServerWorld world, LivingEntity target);

    boolean checkItem(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack);

    String toString();

    void tooltipRender(TextRenderer textRenderer, int x, int y, DrawContext context);

    int getTextWidth(TextRenderer textRenderer);

    <T extends QuestData> Codec<T> getCodec();

    QuestType<?> getType();

    void onQuestDone(PlayerEntity player);
}
