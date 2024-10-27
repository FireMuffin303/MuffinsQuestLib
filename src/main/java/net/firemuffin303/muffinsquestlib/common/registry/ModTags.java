package net.firemuffin303.muffinsquestlib.common.registry;

import net.firemuffin303.muffinsquestlib.MuffinsQuestLib;
import net.firemuffin303.muffinsquestlib.common.quest.Quest;
import net.minecraft.registry.tag.TagKey;

public class ModTags {

    public static final TagKey<Quest> WANDERING_TRADER_QUESTS = TagKey.of(ModRegistries.QUEST_KEY, MuffinsQuestLib.modId("wandering_trader_quests"));
}
