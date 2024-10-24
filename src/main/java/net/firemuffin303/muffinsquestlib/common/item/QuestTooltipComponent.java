package net.firemuffin303.muffinsquestlib.common.item;

import net.firemuffin303.muffinsquestlib.MuffinsQuestLib;
import net.firemuffin303.muffinsquestlib.common.quest.QuestInstance;
import net.firemuffin303.muffinsquestlib.common.quest.QuestType;
import net.firemuffin303.muffinsquestlib.common.quest.data.QuestData;
import net.firemuffin303.muffinsquestlib.common.registry.ModQuestTypes;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.joml.Quaternionf;

import java.util.List;

public class QuestTooltipComponent implements TooltipComponent {
    private final QuestInstance questInstance;
    Quaternionf MOB_ROTATION = new Quaternionf().rotationXYZ(0.43633232F, 180, 3.1415927F);

    public QuestTooltipComponent(QuestTooltipData questTooltipData){
        this.questInstance = questTooltipData.getQuestInstance();
    }

    @Override
    public int getHeight() {
        int height = 32;
        for(QuestType<?> questType: questInstance.getQuest().questTypes.keySet().stream().toList()) {
            height += this.questInstance.getQuestData(questType).size() * 12;
        }
        return height;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return 100;
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        if(this.questInstance != null){
            int maxColumn = 0;
            int neoX = x;
            int neoY = y;

            for(QuestType<?> questType: questInstance.getQuest().questTypes.keySet().stream().toList()){
                List<QuestData> data = this.questInstance.getQuestData(questType);
                for (QuestData datum : data) {
                    datum.tooltipRender(textRenderer,x,neoY,context);
                    neoY+=8;
                }
            }

            neoY += 2;
            context.drawText(textRenderer, Text.of("Rewards :"),x+2,neoY,0xffffff,false);
            int index = 0;
            neoY += 8;

            for(ItemStack itemStack: this.questInstance.getRewards()) {
                context.drawItem(itemStack,neoX+1,neoY+1,index);
                context.drawItemInSlot(textRenderer,itemStack,neoX+1,neoY+1);
                neoX += 18;
                maxColumn ++;

                if(maxColumn >= 5){
                    neoX = x;
                    neoY += 18;
                    maxColumn = 0;
                }
                index++;
            }

        }
    }

}
