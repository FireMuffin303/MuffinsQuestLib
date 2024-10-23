package net.firemuffin303.muffinsquestlib.common.quest.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.firemuffin303.muffinsquestlib.common.quest.QuestType;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public interface QuestData {


    int getRequirementAmount();

    void toPacket(PacketByteBuf packetByteBuf);

    boolean checkKillOther(ServerPlayerEntity serverPlayerEntity, ServerWorld world, LivingEntity target);

    String toString();

    void tooltipRender(TextRenderer textRenderer, int x, int y, DrawContext context);

    <T extends QuestData> Codec<T> getCodec();

    QuestType<?> getType();
}
