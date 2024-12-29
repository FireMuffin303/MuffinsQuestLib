package net.firemuffin303.muffinsquestlib.common.quest.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.firemuffin303.muffinsquestlib.api.data.QuestData;
import net.firemuffin303.muffinsquestlib.common.quest.PlayerQuestData;
import net.firemuffin303.muffinsquestlib.common.quest.QuestInstance;
import net.firemuffin303.muffinsquestlib.common.quest.QuestType;
import net.firemuffin303.muffinsquestlib.common.registry.QuestTypes;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record KillEntityQuestData(EntityRequirementEntry entityRequirementEntry) implements QuestData {
    public static final Codec<KillEntityQuestData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EntityRequirementEntry.CODEC.fieldOf("entity_requirements").forGetter(KillEntityQuestData::getEntityRequirements)
    ).apply(instance, KillEntityQuestData::new));

    public EntityRequirementEntry getEntityRequirements() {
        return entityRequirementEntry;
    }

    public boolean checkKillOther(ServerPlayerEntity serverPlayerEntity, ServerWorld world, LivingEntity target) {
        return this.entityRequirementEntry.entityType.equals(target.getType());
    }

    @Override
    public void onQuestDone(PlayerEntity player) {
        this.clearQuestEntity(player);
    }

    @Override
    public void onQuestFailed(PlayerEntity player) {
        this.clearQuestEntity(player);
    }

    @Override
    public int getRequirementAmount() {
        return this.entityRequirementEntry.amount;
    }

    @Override
    public void tooltipRender(TextRenderer textRenderer, int x, int y, DrawContext context) {
        Text text = Text.translatable("item.quest_paper.tooltip.kill_entity",this.getRequirementAmount(),Text.translatable(getEntityRequirements().entityType.toString()).getString());

        context.drawText(textRenderer, text,x,y, 16755200,false);
    }

    @Override
    public int getTextWidth(TextRenderer textRenderer) {
        Text text = Text.translatable("item.quest_paper.tooltip.kill_entity",this.getRequirementAmount(),Text.translatable(getEntityRequirements().entityType.toString()).getString());
        return textRenderer.getWidth(text);
    }
    @Override
    public void toPacket(PacketByteBuf packetByteBuf) {
        packetByteBuf.writeIdentifier(Registries.ENTITY_TYPE.getId(this.getEntityRequirements().entityType));
        packetByteBuf.writeInt(this.entityRequirementEntry.amount);
    }

    public static KillEntityQuestData fromPacket(PacketByteBuf packetByteBuf) {
        EntityType<?> entityType = Registries.ENTITY_TYPE.get(packetByteBuf.readIdentifier());
        int amount = packetByteBuf.readInt();
        return new KillEntityQuestData(new EntityRequirementEntry(entityType,amount));
    }

    @Override
    public QuestType<?> getType() {
        return QuestTypes.KILL_ENTITY_DATA;
    }

    @Override
    public String toString() {
        return this.entityRequirementEntry.entityType.getTranslationKey();
    }

    private void clearQuestEntity(PlayerEntity player){
        ServerWorld serverWorld = (ServerWorld) player.getWorld();
        QuestInstance questInstance = ((PlayerQuestData.PlayerQuestDataAccessor)player).questLib$getData().getQuestInstance();
        if(questInstance != null && !questInstance.getQuestEntitiesUUID().isEmpty()){
            //We make lists here as a cache to prevent ConcurrentModification Error.
            List<UUID> uuids = new ArrayList<>(questInstance.getQuestEntitiesUUID());

            for(UUID uuid: uuids){
                Entity entity = serverWorld.getEntity(uuid);
                if(entity != null && entity.getType() == this.getEntityRequirements().entityType()){
                    entity.discard();
                }
            }
        }
    }

    public record EntityRequirementEntry(EntityType<?> entityType, int amount) {
        public static final Codec<EntityRequirementEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Registries.ENTITY_TYPE.getCodec().fieldOf("type").forGetter(EntityRequirementEntry::entityType),
                Codec.INT.fieldOf("amount").forGetter(EntityRequirementEntry::amount)
        ).apply(instance, EntityRequirementEntry::new));
    }
}
