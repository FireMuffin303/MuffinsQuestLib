package net.firemuffin303.muffinsquestlib;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.firemuffin303.muffinsquestlib.common.ModServerEventHandler;
import net.firemuffin303.muffinsquestlib.common.quest.PlayerQuestData;
import net.firemuffin303.muffinsquestlib.common.QuestManager;
import net.firemuffin303.muffinsquestlib.common.command.ModCommands;
import net.firemuffin303.muffinsquestlib.common.item.QuestPaperItem;
import net.firemuffin303.muffinsquestlib.common.quest.QuestInstance;
import net.firemuffin303.muffinsquestlib.common.registry.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;

public class MuffinsQuestLib implements ModInitializer {
    public static final GameRules.Key<GameRules.BooleanRule> DO_QUEST_TARGET_ENTITY_SPAWN  = GameRuleRegistry.register("muffins_questlib:doQuestTargetEntitySpawn", GameRules.Category.SPAWNING,GameRuleFactory.createBooleanRule(true));

    //Successfully quest datapack hotswap... but need to be sync to client, which is impossible. for now.
    public static final QuestManager QUEST_MANAGER = new QuestManager();

    public static final String MOD_ID = "muffins_questlib";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final ItemGroup MOD_ITEM_GROUP = FabricItemGroup.builder()
            .displayName(Text.literal("Quest Library"))
            .icon(() -> new ItemStack(QuestItems.QUEST_PAPER_ITEM))
            .entries((displayContext, entries) -> {
                displayContext.lookup().getOptionalWrapper(QuestRegistries.QUEST_KEY).ifPresent(questImpl -> {
                    questImpl.streamEntries().map(questReference -> QuestPaperItem.getQuestPaper(questReference.registryKey().getValue(),18000)).forEach(entries::add);
                });
            }).build();


    @Override
    public void onInitialize() {
        QuestRegistries.init();
        ModCommands.init();
        QuestSoundEvents.init();
        QuestTypes.init();
        Quests.init();
        QuestItems.init();

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(QUEST_MANAGER);

        Registry.register(Registries.ITEM_GROUP, MuffinsQuestLib.modId("main"),MOD_ITEM_GROUP);

        ModServerEventHandler.init();
    }



    public static Identifier modId(String id){
        return new Identifier(MOD_ID,id);
    }

    //Remove UUID if Player kills Quest Marked Entity
    public static void onPlayerKill(ServerWorld world, ServerPlayerEntity serverPlayerEntity, LivingEntity livingEntity){
        QuestInstance questInstance = ((PlayerQuestData.PlayerQuestDataAccessor)serverPlayerEntity).questLib$getData().getQuestInstance();
        if(questInstance != null){
            if(!questInstance.getQuestEntitiesUUID().isEmpty()){
                if(questInstance.getQuestEntitiesUUID().contains(livingEntity.getUuid())){
                    questInstance.removeQuestEntity(livingEntity.getUuid(),serverPlayerEntity);
                }
            }
        }
    }


}
