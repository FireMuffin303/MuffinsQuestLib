package net.firemuffin303.muffinsquestlib.client;

import net.firemuffin303.muffinsquestlib.MuffinsQuestLib;
import net.firemuffin303.muffinsquestlib.common.PlayerQuestData;
import net.firemuffin303.muffinsquestlib.common.quest.QuestInstance;
import net.firemuffin303.muffinsquestlib.common.quest.QuestType;
import net.firemuffin303.muffinsquestlib.common.quest.data.QuestData;
import net.firemuffin303.muffinsquestlib.common.registry.ModQuestTypes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class ModHudRender {

    public static void init(DrawContext drawContext,float delta){
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        questHUD(drawContext,delta,minecraftClient);
    }

    public static void questHUD(DrawContext drawContext, float delta,MinecraftClient client){
        Objects.requireNonNull(client.player);
        QuestInstance questInstance = ((PlayerQuestData.PlayerQuestDataAccessor)client.player).questLib$getData().getQuestInstance();
        if(client.player != null && questInstance != null){
            Text text = Text.of(questInstance.getQuest().description);
            drawContext.drawText(client.textRenderer, Text.literal("Quest :"+ text.getString()), 2,6,0x0b0b0b,false);
            List<QuestType<?>> questTypes = questInstance.getQuest().questTypes.keySet().stream().toList();

            int progressY = 14;

            for(QuestType<?> questType : questTypes){
                for(int i = 0; i < questInstance.getQuest().getQuests(questType).size() ; i++){
                    int cureent = questInstance.getProgressType(questType).get(i);
                    int requireAmount = questInstance.getQuest().getQuests(questType).get(i).getRequirementAmount();
                    drawContext.drawText(client.textRenderer,Text.literal("Progress : "+ cureent +"/"+requireAmount),2,progressY,0x0b0b0b,false);
                    progressY += 8;
                }
            }
            drawContext.drawText(client.textRenderer, Text.literal("Time Left :"+ questInstance.time), 2,progressY,0x0b0b0b,false);
        }
    }
}
