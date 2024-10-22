package net.firemuffin303.muffinsquestlib.common.quest.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;

public record KillEntityQuestData(EntityRequirementEntry entityRequirementEntry) implements QuestData {
    public static final Codec<KillEntityQuestData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EntityRequirementEntry.CODEC.fieldOf("entity_requirements").forGetter(KillEntityQuestData::getEntityRequirements)
    ).apply(instance, KillEntityQuestData::new));

    public EntityRequirementEntry getEntityRequirements() {
        return entityRequirementEntry;
    }

    @Override
    public String toString() {
        return this.entityRequirementEntry.entityType.getTranslationKey();
    }

    @Override
    public void tooltipRender(TextRenderer textRenderer, int x, int y, DrawContext context) {
        context.drawText(textRenderer, Text.of("* "+ Text.translatable("muffins_questlib.questdata.kill_entity.tooltip",
                this.getRequirementAmount(),Text.translatable(getEntityRequirements().entityType.toString()).append(this.getRequirementAmount() > 0 ? "s":"")).getString()
        ),x,y, 16755200,false);
    }


    //Quest Data
    @Override
    public int getRequirementAmount() {
        return this.entityRequirementEntry.amount;
    }

    @Override
    public void toPacket(PacketByteBuf packetByteBuf) {
        packetByteBuf.writeIdentifier(Registries.ENTITY_TYPE.getId(this.getEntityRequirements().entityType));
        packetByteBuf.writeInt(this.entityRequirementEntry.amount);
    }

    @Override
    public boolean checkKillOther(ServerPlayerEntity serverPlayerEntity, ServerWorld world, LivingEntity target) {
        return this.entityRequirementEntry.entityType.equals(target.getType());
    }

    public static KillEntityQuestData fromPacket(PacketByteBuf packetByteBuf) {
        EntityType<?> entityType = Registries.ENTITY_TYPE.get(packetByteBuf.readIdentifier());
        int amount = packetByteBuf.readInt();
        return new KillEntityQuestData(new EntityRequirementEntry(entityType,amount));
    }

    public record EntityRequirementEntry(EntityType<?> entityType, int amount) {
        public static final Codec<EntityRequirementEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Registries.ENTITY_TYPE.getCodec().fieldOf("type").forGetter(EntityRequirementEntry::entityType),
                Codec.INT.fieldOf("amount").forGetter(EntityRequirementEntry::amount)
        ).apply(instance, EntityRequirementEntry::new));
    }
}
