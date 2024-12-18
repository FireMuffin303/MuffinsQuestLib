package net.firemuffin303.muffinsquestlib.client;

import net.firemuffin303.muffinsquestlib.MuffinsQuestLib;
import net.firemuffin303.muffinsquestlib.client.screen.QuestInfoScreen;
import net.firemuffin303.muffinsquestlib.common.PlayerQuestData;
import net.firemuffin303.muffinsquestlib.common.quest.Quest;
import net.firemuffin303.muffinsquestlib.common.quest.QuestInstance;
import net.firemuffin303.muffinsquestlib.common.quest.QuestType;
import net.firemuffin303.muffinsquestlib.common.quest.data.QuestData;
import net.firemuffin303.muffinsquestlib.common.registry.ModQuestTypes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class ModHudRender {
    private static final Identifier QUEST_HUD = MuffinsQuestLib.modId("textures/gui/quest_hud.png");

    public static void init(DrawContext drawContext,float delta){
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        questHUD(drawContext,delta,minecraftClient);
    }

    public static void questHUD(DrawContext drawContext, float delta,MinecraftClient client){
        Objects.requireNonNull(client.player);
        QuestInstance questInstance = ((PlayerQuestData.PlayerQuestDataAccessor)client.player).questLib$getData().getQuestInstance();
        if(client.player != null && questInstance != null){
            int i = questInstance.getQuest().questRarity == Quest.QuestRarity.COMMON ? 176 : 200;

            float f = 1.0f;
            if (questInstance.time < 200) {
                int m = questInstance.time;
                int n = 10 - m / 20;
                f = MathHelper.clamp((float)m / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) + MathHelper.cos((float)m * 3.1415927F / 5.0F) * MathHelper.clamp((float)n / 10.0F * 0.25F, 0.0F, 0.25F);
            }

            drawContext.setShaderColor(1.0f,1.0f,1.0f,f);
            drawContext.drawTexture(QuestInfoScreen.QUEST_SCREEN_TEXTURE,0,0,i,0,24,24);
            drawContext.drawText(client.textRenderer,Text.of((questInstance.time/20)/60+":"+ (questInstance.time/20)%60 ),26,8,0xffffff,false);
            drawContext.setShaderColor(1.0f,1.0f,1.0f,1.0f);
        }
    }
}
