package net.firemuffin303.muffinsquestlib.common.registry;

import net.firemuffin303.muffinsquestlib.MuffinsQuestLib;
import net.firemuffin303.muffinsquestlib.common.quest.QuestType;
import net.firemuffin303.muffinsquestlib.common.quest.data.CollectItemQuestData;
import net.firemuffin303.muffinsquestlib.common.quest.data.KillEntityQuestData;
import net.minecraft.registry.Registry;

public class ModQuestTypes {
    public static final QuestType<KillEntityQuestData> KILL_ENTITY_DATA = Registry.register(ModRegistries.QUEST_TYPE_REGISTRY, MuffinsQuestLib.modId("kill_entity"),new QuestType<>(KillEntityQuestData.CODEC,KillEntityQuestData::fromPacket));
    public static final QuestType<CollectItemQuestData> COLLECT_ITEM_DATA = Registry.register(ModRegistries.QUEST_TYPE_REGISTRY, MuffinsQuestLib.modId("collect_item"),new QuestType<>(CollectItemQuestData.CODEC,CollectItemQuestData::fromPacket));

    public static void init(){}
}
