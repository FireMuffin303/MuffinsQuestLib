package net.firemuffin303.muffinsquestlib.common.item;

import com.mojang.logging.LogUtils;
import net.firemuffin303.muffinsquestlib.common.quest.QuestInstance;
import net.firemuffin303.muffinsquestlib.common.quest.QuestType;
import net.firemuffin303.muffinsquestlib.common.quest.data.QuestData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.apache.commons.logging.Log;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

public class QuestTooltipComponent implements TooltipComponent {
    private final QuestInstance questInstance;
    private final int width;
    private final int height;

    Quaternionf MOB_ROTATION = new Quaternionf().rotationXYZ(0.43633232F, 180, 3.1415927F);

    public QuestTooltipComponent(QuestTooltipData questTooltipData){
        this.questInstance = questTooltipData.getQuestInstance();
        Text time = Text.translatable("item.quest_paper.tooltip.quest_time", String.format("%02d:%02d",(questInstance.time/20)/60,(questInstance.time/20)%60));
        int timeWidth = MinecraftClient.getInstance().textRenderer.getWidth(time);
        int height = 12;

        int questInfoTextWidth = 0;
        for(QuestType<?> questType: questInstance.getQuest().questTypes.keySet().stream().toList()){
            List<QuestData> data = this.questInstance.getQuestData(questType);
            for (QuestData datum : data) {
                questInfoTextWidth = Math.max(questInfoTextWidth,datum.getTextWidth(MinecraftClient.getInstance().textRenderer));
                height += 8;
            }
        }
        height += 12;
        int rewardRows = (this.questInstance.getRewards().size() / 5) + 1;
        height += 16 * rewardRows;

        this.width = Math.max(timeWidth,questInfoTextWidth) + 12;
        this.height = height;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return this.width;
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        if(this.questInstance != null){
            int maxColumn = 0;
            int neoX = x;
            int neoY = y;

            Text timeText = Text.translatable("item.quest_paper.tooltip.quest_time", String.format("%02d:%02d",(questInstance.time/20)/60,(questInstance.time/20)%60));
            context.drawText(textRenderer, timeText ,x,neoY,11184810,false);
            neoY += 12;


            for(QuestType<?> questType: questInstance.getQuest().questTypes.keySet().stream().toList()){
                List<QuestData> data = this.questInstance.getQuestData(questType);
                for (QuestData datum : data) {
                    datum.tooltipRender(textRenderer,x,neoY,context);
                    neoY+=8;
                }
            }

            neoY += 2;
            context.drawText(textRenderer, Text.translatable("item.quest_paper.tooltip.rewards"),x+2,neoY,0xffffff,false);
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
