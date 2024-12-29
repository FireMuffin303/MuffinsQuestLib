package net.firemuffin303.muffinsquestlib.common.registry;

import net.firemuffin303.muffinsquestlib.MuffinsQuestLib;
import net.firemuffin303.muffinsquestlib.common.item.QuestPaperItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class QuestItems {
    public static final Item QUEST_PAPER_ITEM = register("quest_paper",new QuestPaperItem(new Item.Settings()));

    public static void init(){}

    public static Item register(String id,Item item){
        return Registry.register(Registries.ITEM, MuffinsQuestLib.modId(id),item);
    }
}
