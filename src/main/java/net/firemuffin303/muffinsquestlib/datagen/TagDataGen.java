package net.firemuffin303.muffinsquestlib.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.firemuffin303.muffinsquestlib.common.quest.Quest;
import net.firemuffin303.muffinsquestlib.common.registry.ModQuests;
import net.firemuffin303.muffinsquestlib.common.registry.ModRegistries;
import net.firemuffin303.muffinsquestlib.common.registry.ModTags;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class TagDataGen extends FabricTagProvider<Quest> {
    public TagDataGen(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, ModRegistries.QUEST_KEY, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
       this.getOrCreateTagBuilder(ModTags.WANDERING_TRADER_QUESTS)
               .add(ModQuests.KILL_PHANTOM_5)
               .add(ModQuests.KILL_ZOMBIE_10)
               .add(ModQuests.KILL_SPIDER_10);
    }
}
