package net.firemuffin303.muffinsquestlib.common.registry;

import net.firemuffin303.muffinsquestlib.MuffinsQuestLib;
import net.firemuffin303.muffinsquestlib.common.quest.Quest;
import net.firemuffin303.muffinsquestlib.common.quest.data.KillEntityQuestData;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModQuests {
    public static final Map<String,Quest> QUESTS = new HashMap<>();

    public static final Quest KILL_ZOMBIE_10 = register("kill_zombie_10",
            new Quest(new Quest.Definition(List.of(new ItemStack(Items.LEATHER,10),new ItemStack(Items.EMERALD,1)),20),
            "quest.kill_zombie_10.desc")
            .addQuest(ModQuestTypes.KILL_ENTITY_DATA,new KillEntityQuestData(new KillEntityQuestData.EntityRequirementEntry(EntityType.ZOMBIE,10))));

    public static final Quest KILL_SPIDER_10 = register("kill_spider_10",
            new Quest(new Quest.Definition(List.of(new ItemStack(Items.SPIDER_EYE,10)),20),
                    "quest.kill_spider_10.desc")
                    .addQuest(ModQuestTypes.KILL_ENTITY_DATA,new KillEntityQuestData(new KillEntityQuestData.EntityRequirementEntry(EntityType.SPIDER,10))));

    public static final Quest KILL_MULTIPLE_10 = register("kill_multiple_10",
            new Quest(new Quest.Definition(List.of(
                    new ItemStack(Items.DIAMOND,32),
                    new ItemStack(Items.NETHERITE_INGOT,1),
                    new ItemStack(Items.GOLD_INGOT,1),
                    new ItemStack(Items.ECHO_SHARD),
                    new ItemStack(Items.OAK_PLANKS),
                    new ItemStack(Items.GLOW_LICHEN),
                    new ItemStack(Items.LEATHER)
            ),20),
                    "quest.kill_spider_10.desc")
                    .addQuest(ModQuestTypes.KILL_ENTITY_DATA,new KillEntityQuestData(new KillEntityQuestData.EntityRequirementEntry(EntityType.SPIDER,1)))
                    .addQuest(ModQuestTypes.KILL_ENTITY_DATA,new KillEntityQuestData(new KillEntityQuestData.EntityRequirementEntry(EntityType.CREEPER,1)))
                    .addQuest(ModQuestTypes.KILL_ENTITY_DATA,new KillEntityQuestData(new KillEntityQuestData.EntityRequirementEntry(EntityType.ZOMBIE,1)))
                    .addQuest(ModQuestTypes.KILL_ENTITY_DATA,new KillEntityQuestData(new KillEntityQuestData.EntityRequirementEntry(EntityType.SKELETON,1)))
                    .addQuest(ModQuestTypes.KILL_ENTITY_DATA,new KillEntityQuestData(new KillEntityQuestData.EntityRequirementEntry(EntityType.ENDERMAN,1)))
    );

    public static final Quest KILL_PHANTOM_5 = register("kill_phantom_5",
            new Quest(new Quest.Definition(List.of(new ItemStack(Items.ENDER_EYE)),10),"quest.kill_phantom_5.desc")
                    .addQuest(ModQuestTypes.KILL_ENTITY_DATA,new KillEntityQuestData(new KillEntityQuestData.EntityRequirementEntry(EntityType.PHANTOM,5)))
            );


    public static void init(){}


    public static Quest register(String id,Quest quest){
        QUESTS.put(id, quest);

        //Registry.register(ModRegistries.QUEST_KEY,RegistryKey.of(ModRegistries.QUEST_KEY,MuffinsQuestLib.modId(id)),quest);
        return quest;
        //return Registry.register(ModRegistries.QUEST_REGISTRY, MuffinsQuestLib.modId(id),quest);
    }
}
