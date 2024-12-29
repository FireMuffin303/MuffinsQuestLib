package net.firemuffin303.muffinsquestlib.common.registry;

import net.firemuffin303.muffinsquestlib.MuffinsQuestLib;
import net.firemuffin303.muffinsquestlib.common.quest.Quest;
import net.firemuffin303.muffinsquestlib.common.quest.data.BlockRequirementEntry;
import net.firemuffin303.muffinsquestlib.common.quest.data.BreakBlockQuestData;
import net.firemuffin303.muffinsquestlib.common.quest.data.CollectItemQuestData;
import net.firemuffin303.muffinsquestlib.common.quest.data.KillEntityQuestData;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;

import java.util.List;

public class Quests {
    public static final RegistryKey<Quest> KILL_ZOMBIE_10 = register("kill_zombie_10");
    public static final RegistryKey<Quest> KILL_SPIDER_10 = register("kill_spider_10");
    public static final RegistryKey<Quest> KILL_MULTIPLE_10 = register("kill_multiple_10");
    public static final RegistryKey<Quest> KILL_PHANTOM_5 = register("kill_phantom_5");
    public static final RegistryKey<Quest> COLLECT_PLANKS_32 = register("collect_planks_32");
    public static final RegistryKey<Quest> BLAZE_KILLER_1 = register("blaze_killer_1");
    public static final RegistryKey<Quest> PILLAGER_10 = register("pillager_10");
    public static final RegistryKey<Quest> MINE_10_IRON_ORE = register("mine_10_iron_ore");

    public static void init(){}

    public static RegistryKey<Quest> register(String id){
        return RegistryKey.of(QuestRegistries.QUEST_KEY, MuffinsQuestLib.modId(id));
    }

    public static void dynamicRegister(Registerable<Quest> questRegisterable){

        questRegisterable.getRegistryLookup(QuestRegistries.QUEST_KEY);
        questRegisterable.register(KILL_ZOMBIE_10,
                new Quest(new Quest.Definition(List.of(new ItemStack(Items.LEATHER,10),new ItemStack(Items.EMERALD,1)),20),
                        "Kill 10 Zombies")
                        .addQuest(QuestTypes.KILL_ENTITY_DATA,new KillEntityQuestData(new KillEntityQuestData.EntityRequirementEntry(EntityType.ZOMBIE,10))));

        questRegisterable.register(KILL_SPIDER_10,
                new Quest(new Quest.Definition(List.of(new ItemStack(Items.SPIDER_EYE,10),new ItemStack(Items.STRING,5)),20),
                "Kill 10 Spiders")
                .addQuest(QuestTypes.KILL_ENTITY_DATA,new KillEntityQuestData(new KillEntityQuestData.EntityRequirementEntry(EntityType.SPIDER,10))));

        questRegisterable.register(KILL_MULTIPLE_10,
                new Quest(new Quest.Definition(List.of(
                        new ItemStack(Items.IRON_INGOT,32),
                        new ItemStack(Items.GOLD_INGOT,8),
                        new ItemStack(Items.ENDER_PEARL,2)
                ),20),
                        "Kill 1 Spider, 1 Creeper, 1 Zombie, 1 Skeleton and 1 Enderman")
                        .addQuest(QuestTypes.KILL_ENTITY_DATA,new KillEntityQuestData(new KillEntityQuestData.EntityRequirementEntry(EntityType.SPIDER,1)))
                        .addQuest(QuestTypes.KILL_ENTITY_DATA,new KillEntityQuestData(new KillEntityQuestData.EntityRequirementEntry(EntityType.CREEPER,1)))
                        .addQuest(QuestTypes.KILL_ENTITY_DATA,new KillEntityQuestData(new KillEntityQuestData.EntityRequirementEntry(EntityType.ZOMBIE,1)))
                        .addQuest(QuestTypes.KILL_ENTITY_DATA,new KillEntityQuestData(new KillEntityQuestData.EntityRequirementEntry(EntityType.SKELETON,1)))
                        .addQuest(QuestTypes.KILL_ENTITY_DATA,new KillEntityQuestData(new KillEntityQuestData.EntityRequirementEntry(EntityType.ENDERMAN,1))));

        questRegisterable.register(KILL_PHANTOM_5,
                new Quest(new Quest.Definition(List.of(new ItemStack(Items.PHANTOM_MEMBRANE,12)),10),"Kill 5 Phantom")
                        .addQuest(QuestTypes.KILL_ENTITY_DATA,new KillEntityQuestData(new KillEntityQuestData.EntityRequirementEntry(EntityType.PHANTOM,5))));

        questRegisterable.register(COLLECT_PLANKS_32,new Quest(
                new Quest.Definition(
                        List.of(new ItemStack(Items.OAK_SAPLING,20)),
                        5),
                "Collect 32 Oak Planks"
        )
                .addQuest(QuestTypes.COLLECT_ITEM_DATA,new CollectItemQuestData(new ItemStack(Items.OAK_PLANKS,32))));

        questRegisterable.register(BLAZE_KILLER_1,new Quest(
                new Quest.Definition(
                        List.of(new ItemStack(Items.BLAZE_POWDER,5)),
                        15),
                "Kill 5 Blazes and Collect 3 Blaze Rods"
        )
                .addQuest(QuestTypes.KILL_ENTITY_DATA,new KillEntityQuestData(new KillEntityQuestData.EntityRequirementEntry(EntityType.BLAZE,5)))
                .addQuest(QuestTypes.COLLECT_ITEM_DATA,new CollectItemQuestData(new ItemStack(Items.BLAZE_ROD,3))));

        questRegisterable.register(PILLAGER_10,new Quest(
                new Quest.Definition(List.of(new ItemStack(Items.ARROW,16)),20),"Kill 10 Pillagers"
        ).addQuest(QuestTypes.KILL_ENTITY_DATA,new KillEntityQuestData(new KillEntityQuestData.EntityRequirementEntry(EntityType.PILLAGER,5))));

        questRegisterable.register(MINE_10_IRON_ORE,new Quest(
                new Quest.Definition(List.of(new ItemStack(Items.IRON_INGOT,10)),10),"Mine 10 Iron Ore"
        ).addQuest(QuestTypes.BREAK_BLOCK_DATA,new BreakBlockQuestData(new BlockRequirementEntry(Blocks.IRON_ORE,10)))
                        .addQuest(QuestTypes.COLLECT_ITEM_DATA,new CollectItemQuestData(new ItemStack(Items.RAW_IRON,10)))
                        .setRarity(Quest.QuestRarity.COMMON)
        );
    }
}
