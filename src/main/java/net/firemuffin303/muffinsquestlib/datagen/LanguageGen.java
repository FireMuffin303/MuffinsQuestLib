package net.firemuffin303.muffinsquestlib.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.firemuffin303.muffinsquestlib.common.registry.ModItems;

public class LanguageGen extends FabricLanguageProvider {
    protected LanguageGen(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        translationBuilder.add(ModItems.QUEST_PAPER_ITEM,"Quest Paper");
        translationBuilder.add("quest_info_screen.title","Quest");
        translationBuilder.add("quest_info_screen.no_quest","You don't have any quest. :(");
        translationBuilder.add("quest_info_screen.quest_name","Quest : %s");
        translationBuilder.add("quest_info_screen.quest_reward","Rewards");
        translationBuilder.add("quest_info_screen.progress","%s (%d/%d)");
        translationBuilder.add("quest_info_screen.cancel_quest","Cancel Quest");
        translationBuilder.add("muffins_questlib.questdata.kill_entity.tooltip", "Kill %d %s");
    }

    public static class ThaiLanguage extends FabricLanguageProvider {
        protected ThaiLanguage(FabricDataOutput dataOutput) {
            super(dataOutput,"th_th");
        }

        @Override
        public void generateTranslations(TranslationBuilder translationBuilder) {
            translationBuilder.add(ModItems.QUEST_PAPER_ITEM,"กระดาษภารกิจ");
            translationBuilder.add("quest_info_screen.title","ภารกิจ");
            translationBuilder.add("quest_info_screen.no_quest","คุณไม่มีภารกิจอะไรเลย :(");
            translationBuilder.add("quest_info_screen.quest_name","ภารกิจ : %s");
            translationBuilder.add("quest_info_screen.quest_reward","รางวัล");
            translationBuilder.add("quest_info_screen.progress","%s (%d/%d)");
            translationBuilder.add("quest_info_screen.cancel_quest","ยกเลิกภารกิจ");
            translationBuilder.add("muffins_questlib.questdata.kill_entity.tooltip", "ฆ่า %d %s");


        }
    }
}
