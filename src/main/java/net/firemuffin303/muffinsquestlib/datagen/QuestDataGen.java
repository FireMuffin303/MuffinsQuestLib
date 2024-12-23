package net.firemuffin303.muffinsquestlib.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.firemuffin303.muffinsquestlib.MuffinsQuestLib;
import net.firemuffin303.muffinsquestlib.common.registry.ModQuests;
import net.firemuffin303.muffinsquestlib.common.registry.ModRegistries;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class QuestDataGen extends FabricDynamicRegistryProvider {
    public QuestDataGen(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup, Entries entries) {
        entries.add(wrapperLookup.getWrapperOrThrow(ModRegistries.QUEST_KEY),ModQuests.KILL_ZOMBIE_10);
        entries.add(wrapperLookup.getWrapperOrThrow(ModRegistries.QUEST_KEY),ModQuests.KILL_SPIDER_10);
        entries.add(wrapperLookup.getWrapperOrThrow(ModRegistries.QUEST_KEY),ModQuests.KILL_MULTIPLE_10);
        entries.add(wrapperLookup.getWrapperOrThrow(ModRegistries.QUEST_KEY),ModQuests.KILL_PHANTOM_5);
        entries.add(wrapperLookup.getWrapperOrThrow(ModRegistries.QUEST_KEY),ModQuests.COLLECT_PLANKS_32);
        entries.add(wrapperLookup.getWrapperOrThrow(ModRegistries.QUEST_KEY),ModQuests.BLAZE_KILLER_1);
        entries.add(wrapperLookup.getWrapperOrThrow(ModRegistries.QUEST_KEY),ModQuests.PILLAGER_10);
        //entries.add(wrapperLookup.getWrapperOrThrow(ModRegistries.QUEST_KEY),ModQuests.WARDEN_SPAWN_TEST);
        //entries.add(wrapperLookup.getWrapperOrThrow(ModRegistries.QUEST_KEY),ModQuests.AXOLOTL_BUCKET_TEST);
    }

    @Override
    public String getName() {
        return MuffinsQuestLib.MOD_ID;
    }
}
