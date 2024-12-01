package net.firemuffin303.muffinsquestlib;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.firemuffin303.muffinsquestlib.common.PlayerQuestData;
import net.firemuffin303.muffinsquestlib.common.command.ModCommands;
import net.firemuffin303.muffinsquestlib.common.item.QuestPaperItem;
import net.firemuffin303.muffinsquestlib.common.network.UpdateQuestInstancePacket;
import net.firemuffin303.muffinsquestlib.common.quest.Quest;
import net.firemuffin303.muffinsquestlib.common.quest.QuestInstance;
import net.firemuffin303.muffinsquestlib.common.registry.*;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;

public class MuffinsQuestLib implements ModInitializer {
    public static final GameRules.Key<GameRules.BooleanRule> DO_QUEST_TARGET_ENTITY_SPAWN  = GameRuleRegistry.register("muffins_questlib:doQuestTargetEntitySpawn", GameRules.Category.SPAWNING,GameRuleFactory.createBooleanRule(true));

    public static final String MOD_ID = "muffins_questlib";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final ItemGroup MOD_ITEM_GROUP = FabricItemGroup.builder()
            .displayName(Text.literal("Quest Library"))
            .icon(() -> new ItemStack(ModItems.QUEST_PAPER_ITEM))
            .entries((displayContext, entries) -> {

                displayContext.lookup().getOptionalWrapper(ModRegistries.QUEST_KEY).ifPresent(questImpl -> {
                    questImpl.streamEntries().map(questReference -> QuestPaperItem.getQuestPaper(questReference.registryKey().getValue(),18000)).forEach(entries::add);
                });
            }).build();


    @Override
    public void onInitialize() {
        Registry.register(Registries.ITEM_GROUP, MuffinsQuestLib.modId("main"),MOD_ITEM_GROUP);

        ModRegistries.init();
        ModCommands.init();
        ModSoundEvents.init();
        ModQuestTypes.init();
        ModQuests.init();
        ModItems.init();

        ServerPlayNetworking.registerGlobalReceiver(MuffinsQuestLib.modId("clear_quest_c2s"),(minecraftServer, serverPlayerEntity, serverPlayNetworkHandler, packetByteBuf, packetSender) -> {
            if(((PlayerQuestData.PlayerQuestDataAccessor)serverPlayerEntity).questLib$getData().getQuestInstance() != null){
                minecraftServer.execute(() -> ((PlayerQuestData.PlayerQuestDataAccessor)serverPlayerEntity).questLib$getData().getQuestInstance().setState(QuestInstance.State.FAIL));

            }
        });

        ServerPlayConnectionEvents.JOIN.register((serverPlayNetworkHandler, packetSender, minecraftServer) -> {
            if(((PlayerQuestData.PlayerQuestDataAccessor)serverPlayNetworkHandler.player).questLib$getData().hasQuest()){
                minecraftServer.execute(() ->{
                    ServerPlayNetworking.send(serverPlayNetworkHandler.player,
                            new UpdateQuestInstancePacket(((PlayerQuestData.PlayerQuestDataAccessor)serverPlayNetworkHandler.player).questLib$getData().getQuestInstance()));
                });
            }

        });

        TradeOfferHelper.registerWanderingTraderOffers(1,factories -> {

            factories.add(new QuestTradeOffer(16,1,1,1));
        });

    }

    public static Identifier modId(String id){
        return new Identifier(MOD_ID,id);
    }

    public static class QuestTradeOffer implements TradeOffers.Factory{
        private final int price;
        private final int maxUses;
        private final int experience;
        private final float multiplier;

        public QuestTradeOffer(int price,int maxUses,int experience,int multiplier){
            this.price = price;
            this.maxUses = maxUses;
            this.experience = experience;
            this.multiplier = multiplier;
        }

        @Override
        public @Nullable TradeOffer create(Entity entity, Random random) {
            List<RegistryEntry.Reference<Quest>> quests = entity.getWorld().getRegistryManager().get(ModRegistries.QUEST_KEY).streamEntries().filter(questReference -> questReference.isIn(ModTags.WANDERING_TRADER_QUESTS)).toList();
            RegistryEntry.Reference<Quest> questReference = quests.get(random.nextInt(quests.size()-1));
            return new TradeOffer(new ItemStack(Items.GOLD_INGOT,this.price), QuestPaperItem.getQuestPaper(questReference.registryKey().getValue(),18000),1,this.experience,0.05f);
        }
    }
}
