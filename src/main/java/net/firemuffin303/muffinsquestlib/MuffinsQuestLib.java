package net.firemuffin303.muffinsquestlib;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
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
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.lang.ref.Reference;
import java.util.Objects;
import java.util.Optional;

public class MuffinsQuestLib implements ModInitializer {

    public static final String MOD_ID = "muffins_questlib";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final ItemGroup MOD_ITEM_GROUP = FabricItemGroup.builder()
            .displayName(Text.literal("Quest Library"))
            .icon(() -> new ItemStack(ModItems.QUEST_PAPER_ITEM))
            .entries((displayContext, entries) -> {

                displayContext.lookup().getOptionalWrapper(ModRegistries.QUEST_KEY).ifPresent(questImpl -> {
                    questImpl.streamEntries().map(questReference -> QuestPaperItem.getQuestPaper(questReference.registryKey().getValue())).forEach(entries::add);
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

    static class QuestTradeOffer implements TradeOffers.Factory{
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
            Optional<RegistryEntry.Reference<Quest>> quest = entity.getWorld().getRegistryManager().get(ModRegistries.QUEST_KEY).getRandom(random);
            return quest.map(questReference ->
                    new TradeOffer(new ItemStack(Items.EMERALD, this.price), QuestPaperItem.getQuestPaper(questReference.registryKey().getValue()), this.maxUses, this.experience, this.multiplier))
                    .orElseGet(() ->
                            new TradeOffer(new ItemStack(Items.EMERALD, this.price), new ItemStack(Items.PAPER, 8), this.maxUses, this.experience, this.multiplier));

        }
    }
}
