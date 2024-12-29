package net.firemuffin303.muffinsquestlib.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.firemuffin303.muffinsquestlib.MuffinsQuestLib;
import net.firemuffin303.muffinsquestlib.common.registry.QuestTypes;
import net.firemuffin303.muffinsquestlib.common.registry.Quests;
import net.firemuffin303.muffinsquestlib.common.registry.QuestRegistries;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class QuestDataGen extends FabricDynamicRegistryProvider {
    public QuestDataGen(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup, Entries entries) {
        entries.add(wrapperLookup.getWrapperOrThrow(QuestRegistries.QUEST_KEY), Quests.KILL_ZOMBIE_10);
        entries.add(wrapperLookup.getWrapperOrThrow(QuestRegistries.QUEST_KEY), Quests.KILL_SPIDER_10);
        entries.add(wrapperLookup.getWrapperOrThrow(QuestRegistries.QUEST_KEY), Quests.KILL_MULTIPLE_10);
        entries.add(wrapperLookup.getWrapperOrThrow(QuestRegistries.QUEST_KEY), Quests.KILL_PHANTOM_5);
        entries.add(wrapperLookup.getWrapperOrThrow(QuestRegistries.QUEST_KEY), Quests.COLLECT_PLANKS_32);
        entries.add(wrapperLookup.getWrapperOrThrow(QuestRegistries.QUEST_KEY), Quests.BLAZE_KILLER_1);
        entries.add(wrapperLookup.getWrapperOrThrow(QuestRegistries.QUEST_KEY), Quests.PILLAGER_10);
        entries.add(wrapperLookup.getWrapperOrThrow(QuestRegistries.QUEST_KEY), Quests.MINE_10_IRON_ORE);
    }

    @Override
    public String getName() {
        return MuffinsQuestLib.MOD_ID;
    }
}
