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
        translationBuilder.add("command.questlib.clear.success","Successfully remove quest from %s");
        translationBuilder.add("item.quest_paper.tooltip.quest_time","Time : (%d)");
        translationBuilder.add("item.quest_paper.tooltip.kill_entity", "✶ Kill %d %s");
        translationBuilder.add("item.quest_paper.tooltip.collect_item","✶ Collect %d %s");
        translationBuilder.add("item.quest_paper.tooltip.rewards","Rewards :");
        translationBuilder.add("key.muffins_questlib.questinfo","Open Quest Info");
        translationBuilder.add("category.muffins_questlib","Muffin's Quest Lib");

        //Config
        translationBuilder.add("screen.quest_config.title","Muffin's Quest Lib Options");
        translationBuilder.add("category.quest_lib.hud_options","HUD Options");
        translationBuilder.add("config.quest_lib.show_hud","Show HUD");
        translationBuilder.add("config.quest_lib.show_hud.true","On");
        translationBuilder.add("config.quest_lib.show_hud.false","Off");
        translationBuilder.add("config.quest_lib.icon_position","Icon Position");
        translationBuilder.add("config.quest_lib.icon_position.x","Icon Position X");
        translationBuilder.add("config.quest_lib.icon_position.y","Icon Position Y");
        translationBuilder.add("category.quest_lib.quest_mob_options","Quest Mob Options");

        translationBuilder.add("config.quest_lib.quest_mob_style","Quest Mob Style");
        translationBuilder.add("config.quest_lib.quest_mob_style.none","None");
        translationBuilder.add("config.quest_lib.quest_mob_style.icon","Icon");
        translationBuilder.add("config.quest_lib.quest_mob_style.outline","Outline");
        translationBuilder.add("config.quest_lib.quest_mob_style.icon_and_outline","Icon & Outline");

        //ModMenu
        translationBuilder.add("modmenu.descriptionTranslation.muffins_questlib","Add Data-Drive Quest System to Minecraft!");
        translationBuilder.add("modmenu.nameTranslation.muffins_questlib","Muffin's Quest Lib");
        translationBuilder.add("modmenu.summaryTranslation.muffins_questlib","Add Data-Drive Quest System to Minecraft!");
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
            translationBuilder.add("item.quest_paper.tooltip.quest_time","เวลา : (%d)");
            translationBuilder.add("item.quest_paper.tooltip.kill_entity", "✶ ฆ่า %d %s");
            translationBuilder.add("item.quest_paper.tooltip.collect_item","✶ เก็บ %d %s");
            translationBuilder.add("item.quest_paper.tooltip.rewards","รางวัล :");
            translationBuilder.add("key.muffins_questlib.questinfo","เปิดรายละเอียดเควส");
            translationBuilder.add("category.muffins_questlib","Muffin's Quest Lib");


            translationBuilder.add("screen.quest_config.title","การตั้งค่า Muffin's Quest Lib");
            translationBuilder.add("category.quest_lib.hud_options","ตั้งค่า HUD");
            translationBuilder.add("config.quest_lib.show_hud","แสดง HUD");
            translationBuilder.add("config.quest_lib.show_hud.true","เปิด");
            translationBuilder.add("config.quest_lib.show_hud.false","ปิด");
            translationBuilder.add("config.quest_lib.icon_position","ตำแหน่งไอคอน");
            translationBuilder.add("config.quest_lib.icon_position.x","ตำแหน่งไอคอนแนวนอน");
            translationBuilder.add("config.quest_lib.icon_position.y","ตำแหน่งไอคอนแนวตั้ง");
            translationBuilder.add("category.quest_lib.quest_mob_options","ตั้งค่า Quest Mob");
            translationBuilder.add("config.quest_lib.quest_mob_style","ลักษณะการแสดง Quest Mob");
            translationBuilder.add("config.quest_lib.quest_mob_style.none","ไม่แสดง");
            translationBuilder.add("config.quest_lib.quest_mob_style.icon","ไอคอน");
            translationBuilder.add("config.quest_lib.quest_mob_style.outline","เส้บขอบ");
            translationBuilder.add("config.quest_lib.quest_mob_style.icon_and_outline","ไอคอนและเส้นขอบ");

            translationBuilder.add("modmenu.descriptionTranslation.muffins_questlib","เพิ่มระบบเควสในไมน์คราฟ แบบปรับแต่งได้!");
            translationBuilder.add("modmenu.nameTranslation.muffins_questlib","Muffin's Quest Lib");
            translationBuilder.add("modmenu.summaryTranslation.muffins_questlib","เพิ่มระบบเควสในไมน์คราฟ แบบปรับแต่งได้!");
        }
    }
}
