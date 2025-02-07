package net.firemuffin303.muffinsquestlib.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.firemuffin303.muffinsquestlib.common.registry.Quests;
import net.firemuffin303.muffinsquestlib.common.registry.QuestRegistries;
import net.minecraft.registry.RegistryBuilder;

public class ModDataGen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(ModelGen::new);
        pack.addProvider(LanguageGen::new);
        pack.addProvider(LanguageGen.ThaiLanguage::new);
        pack.addProvider(TagDataGen::new);
        pack.addProvider(TagDataGen.MobTagDataGen::new);
        pack.addProvider(QuestDataGen::new);
    }

    @Override
    public void buildRegistry(RegistryBuilder registryBuilder) {
       registryBuilder.addRegistry(QuestRegistries.QUEST_KEY, Quests::dynamicRegister);

    }
}
