package net.firemuffin303.muffinsquestlib.api.data;

import net.firemuffin303.muffinsquestlib.common.quest.QuestType;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

public interface QuestData {

    void onQuestDone(PlayerEntity player);

    void onQuestFailed(PlayerEntity player);

    int getRequirementAmount();

    void tooltipRender(TextRenderer textRenderer, int x, int y, DrawContext context);

    int getTextWidth(TextRenderer textRenderer);

    void toPacket(PacketByteBuf packetByteBuf);

    <T extends QuestData> QuestType<T> getType();

    String toString();
}
