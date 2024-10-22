package net.firemuffin303.muffinsquestlib.common.item;

import net.firemuffin303.muffinsquestlib.common.quest.QuestInstance;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.world.ClientWorld;

public class QuestTooltipData implements TooltipData {
    private final QuestInstance questInstance;

    public QuestTooltipData(QuestInstance questInstance){
        this.questInstance = questInstance;
    }

    public QuestInstance getQuestInstance() {
        return questInstance;
    }

}
