package net.firemuffin303.muffinsquestlib.common.registry;

import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.firemuffin303.muffinsquestlib.MuffinsQuestLib;
import net.firemuffin303.muffinsquestlib.common.quest.Quest;
import net.firemuffin303.muffinsquestlib.common.quest.QuestType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class ModRegistries {
    public static final RegistryKey<Registry<QuestType<?>>> QUEST_TYPE_KEY = RegistryKey.ofRegistry(MuffinsQuestLib.modId("quest_type"));
    public static final Registry<QuestType<?>> QUEST_TYPE_REGISTRY = FabricRegistryBuilder.createSimple(QUEST_TYPE_KEY).attribute(RegistryAttribute.SYNCED).buildAndRegister();

    public static final RegistryKey<Registry<Quest>> QUEST_KEY = RegistryKey.ofRegistry(MuffinsQuestLib.modId("quest"));
    //public static final Registry<Quest> QUEST_REGISTRY = FabricRegistryBuilder.createSimple(ModRegistries.QUEST_KEY).attribute(RegistryAttribute.SYNCED).buildAndRegister();




    public static void init(){
        DynamicRegistries.registerSynced(QUEST_KEY,Quest.CODEC);
        //Registry.register(QUEST_REGISTRY,TEST,ModQuests.KILL_MULTIPLE_10);
    }
}
