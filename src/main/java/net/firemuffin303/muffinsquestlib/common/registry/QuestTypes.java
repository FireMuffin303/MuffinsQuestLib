package net.firemuffin303.muffinsquestlib.common.registry;

import net.firemuffin303.muffinsquestlib.MuffinsQuestLib;
import net.firemuffin303.muffinsquestlib.api.data.QuestData;
import net.firemuffin303.muffinsquestlib.common.quest.QuestType;
import net.firemuffin303.muffinsquestlib.common.quest.data.CollectItemQuestData;
import net.firemuffin303.muffinsquestlib.common.quest.data.KillEntityQuestData;
import net.firemuffin303.muffinsquestlib.common.quest.data.BreakBlockQuestData;
import net.minecraft.registry.Registry;

public class QuestTypes {
    public static final QuestType<KillEntityQuestData> KILL_ENTITY_DATA = register("kill_entity",new QuestType<>(KillEntityQuestData.CODEC,KillEntityQuestData::fromPacket));
    public static final QuestType<CollectItemQuestData> COLLECT_ITEM_DATA = register("collect_item",new QuestType<>(CollectItemQuestData.CODEC,CollectItemQuestData::fromPacket));
    public static final QuestType<BreakBlockQuestData> BREAK_BLOCK_DATA = register("break_block",new QuestType<>(BreakBlockQuestData.CODEC, BreakBlockQuestData::fromPacket));

    public static void init(){}

    public static <T extends QuestData> QuestType<T> register(String id, QuestType<T> questType){
        return Registry.register(QuestRegistries.QUEST_TYPE_REGISTRY,MuffinsQuestLib.modId(id),questType);
    }
}
