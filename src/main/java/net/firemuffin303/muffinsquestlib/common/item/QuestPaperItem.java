package net.firemuffin303.muffinsquestlib.common.item;

import net.firemuffin303.muffinsquestlib.MuffinsQuestLib;
import net.firemuffin303.muffinsquestlib.common.PlayerQuestData;
import net.firemuffin303.muffinsquestlib.common.quest.Quest;
import net.firemuffin303.muffinsquestlib.common.quest.QuestInstance;
import net.firemuffin303.muffinsquestlib.common.registry.ModItems;
import net.firemuffin303.muffinsquestlib.common.registry.ModQuests;
import net.firemuffin303.muffinsquestlib.common.registry.ModRegistries;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class QuestPaperItem extends Item {
    public static final String STORED_QUEST_KEY = "StoredQuests";

    public QuestPaperItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if(!world.isClient && itemStack.getNbt() != null && itemStack.getNbt().getCompound(STORED_QUEST_KEY) != null && !((PlayerQuestData.PlayerQuestDataAccessor)user).questLib$getData().hasQuest()){
            NbtCompound nbtCompound = itemStack.getNbt().getCompound(STORED_QUEST_KEY);
            Quest playerQuest = world.getRegistryManager().get(ModRegistries.QUEST_KEY).get(Identifier.tryParse(nbtCompound.getString("id")));
            int duration = nbtCompound.getInt("duration");

            world.playSound(null,user.getBlockPos(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS,1.0f,1.0f);
            ((PlayerQuestData.PlayerQuestDataAccessor)user).questLib$getData().setQuestInstance(
                    new QuestInstance(playerQuest,duration)
            );
        }

        return super.use(world, user, hand);
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        /*
        if(stack.getNbt() != null && stack.getNbt().getCompound(STORED_QUEST_KEY) != null){
            NbtCompound nbtCompound = stack.getNbt().getCompound(STORED_QUEST_KEY);
            Quest playerQuest = ModRegistries.QUEST_REGISTRY.get(Identifier.tryParse(nbtCompound.getString("id")));
            int duration = nbtCompound.getInt("duration");

            QuestInstance questInstance = new QuestInstance(playerQuest,duration);
            return Optional.of(new QuestTooltipData(questInstance));
        }
        return super.getTooltipData(stack);

         */
        return Optional.empty();
    }

    public static ItemStack getQuestPaper(Quest quest){
        ItemStack itemStack = new ItemStack(ModItems.QUEST_PAPER_ITEM);
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putString("id", quest.description);
        nbtCompound.putInt("duration",2400);
        itemStack.getOrCreateNbt().put(STORED_QUEST_KEY,nbtCompound);
        return itemStack;
    }
}
