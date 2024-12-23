package net.firemuffin303.muffinsquestlib.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.firemuffin303.muffinsquestlib.common.quest.Quest;
import net.firemuffin303.muffinsquestlib.common.registry.ModQuests;
import net.firemuffin303.muffinsquestlib.common.registry.ModRegistries;
import net.firemuffin303.muffinsquestlib.common.registry.ModTags;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.EntityTypeTags;

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

    public static class MobTagDataGen extends FabricTagProvider.EntityTypeTagProvider{

        public MobTagDataGen(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
            super(output, completableFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
            this.getOrCreateTagBuilder(ModTags.QUEST_SPAWN_BLACKLIST)
                    .add(EntityType.WARDEN)
                    .add(EntityType.WITHER)
                    .add(EntityType.ENDER_DRAGON)
                    .add(EntityType.ELDER_GUARDIAN);

            this.getOrCreateTagBuilder(ModTags.QUEST_NO_CONDITION_SPAWN)
                    .add(EntityType.PHANTOM)
                    .add(EntityType.VILLAGER)
                    .add(EntityType.IRON_GOLEM)
                    .forceAddTag(EntityTypeTags.RAIDERS);
        }
    }
}
