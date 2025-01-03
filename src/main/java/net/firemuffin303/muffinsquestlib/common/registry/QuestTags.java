package net.firemuffin303.muffinsquestlib.common.registry;

import net.firemuffin303.muffinsquestlib.MuffinsQuestLib;
import net.firemuffin303.muffinsquestlib.common.quest.Quest;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class QuestTags {

    public static final TagKey<Quest> WANDERING_TRADER_QUESTS = TagKey.of(QuestRegistries.QUEST_KEY, MuffinsQuestLib.modId("wandering_trader_quests"));
    public static final TagKey<EntityType<?>> QUEST_SPAWN_BLACKLIST = TagKey.of(RegistryKeys.ENTITY_TYPE, MuffinsQuestLib.modId("quest_spawn_blacklist"));
    public static final TagKey<EntityType<?>> QUEST_NO_CONDITION_SPAWN = TagKey.of(RegistryKeys.ENTITY_TYPE,MuffinsQuestLib.modId("quest_no_condition_spawn"));
}
