package net.firemuffin303.muffinsquestlib.api;

import net.firemuffin303.muffinsquestlib.common.item.QuestPaperItem;
import net.firemuffin303.muffinsquestlib.common.quest.Quest;
import net.firemuffin303.muffinsquestlib.common.registry.QuestRegistries;
import net.firemuffin303.muffinsquestlib.common.registry.QuestTags;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class QuestTradeOffers {

    public static class QuestTradeOffer implements TradeOffers.Factory{
        protected final Item buy;
        protected final int price;
        protected final int questDuration;
        protected final int maxUses;
        protected final int experience;
        protected final float priceMultiplier;

        public QuestTradeOffer(Item buy, int price, int experience){
            this(buy,price,18000,1,experience,0.05f);
        }

        public QuestTradeOffer(Item buy, int price, int questDuration, int maxUses, int experience, float priceMultiplier){
            this.buy = buy;
            this.price = price;
            this.questDuration = questDuration;
            this.maxUses = maxUses;
            this.experience = experience;
            this.priceMultiplier = priceMultiplier;
        }

        @Override
        public @Nullable TradeOffer create(Entity entity, Random random) {
            List<RegistryEntry.Reference<Quest>> quests = entity.getWorld().getRegistryManager().get(QuestRegistries.QUEST_KEY).streamEntries().toList();
            RegistryEntry.Reference<Quest> questReference = quests.get(random.nextInt(quests.size()-1));
            return new TradeOffer(new ItemStack(this.buy,this.price), QuestPaperItem.getQuestPaper(questReference.registryKey().getValue(),this.questDuration),this.maxUses,this.experience,this.priceMultiplier);
        }
    }

    public static class WanderingTraderQuestOffer extends QuestTradeOffers.QuestTradeOffer{

        public WanderingTraderQuestOffer(Item buy, int price, int experience) {
            super(buy, price, experience);
        }

        @Override
        public @Nullable TradeOffer create(Entity entity, Random random) {
            List<RegistryEntry.Reference<Quest>> quests = entity.getWorld().getRegistryManager().get(QuestRegistries.QUEST_KEY).streamEntries().filter(questReference -> questReference.isIn(QuestTags.WANDERING_TRADER_QUESTS)).toList();
            RegistryEntry.Reference<Quest> questReference = quests.get(random.nextInt(quests.size()-1));
            return new TradeOffer(new ItemStack(this.buy,this.price), QuestPaperItem.getQuestPaper(questReference.registryKey().getValue(),this.questDuration),this.maxUses,this.experience,this.priceMultiplier);
        }
    }

}
