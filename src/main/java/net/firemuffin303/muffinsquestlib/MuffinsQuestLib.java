package net.firemuffin303.muffinsquestlib;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.firemuffin303.muffinsquestlib.common.PlayerQuestData;
import net.firemuffin303.muffinsquestlib.common.item.QuestPaperItem;
import net.firemuffin303.muffinsquestlib.common.quest.QuestInstance;
import net.firemuffin303.muffinsquestlib.common.registry.ModItems;
import net.firemuffin303.muffinsquestlib.common.registry.ModQuestTypes;
import net.firemuffin303.muffinsquestlib.common.registry.ModQuests;
import net.firemuffin303.muffinsquestlib.common.registry.ModRegistries;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class MuffinsQuestLib implements ModInitializer {

    public static final String MOD_ID = "muffins_questlib";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final ItemGroup MOD_ITEM_GROUP = FabricItemGroup.builder()
            .displayName(Text.literal("Quest Library"))
            .icon(() -> new ItemStack(ModItems.QUEST_PAPER_ITEM))
            .entries((displayContext, entries) -> {
                LOGGER.info(displayContext.lookup().getOptionalWrapper(ModRegistries.QUEST_KEY).toString());

                displayContext.lookup().getOptionalWrapper(ModRegistries.QUEST_KEY).ifPresent(questImpl -> {
                    questImpl.streamEntries().map(entry -> entry.)
                            .map(QuestPaperItem::getQuestPaper)
                            .forEach(entries::add);
                });
            }).build();


    @Override
    public void onInitialize() {
        Registry.register(Registries.ITEM_GROUP, MuffinsQuestLib.modId("main"),MOD_ITEM_GROUP);



        ModRegistries.init();
        ModQuestTypes.init();
        ModQuests.init();
        ModItems.init();

        ServerPlayNetworking.registerGlobalReceiver(MuffinsQuestLib.modId("clear_quest_c2s"),(minecraftServer, serverPlayerEntity, serverPlayNetworkHandler, packetByteBuf, packetSender) -> {
            if(((PlayerQuestData.PlayerQuestDataAccessor)serverPlayerEntity).questLib$getData().getQuestInstance() != null){
                minecraftServer.execute(() -> ((PlayerQuestData.PlayerQuestDataAccessor)serverPlayerEntity).questLib$getData().getQuestInstance().setState(QuestInstance.State.FAIL));

            }
        });
    }

    public static Identifier modId(String id){
        return new Identifier(MOD_ID,id);
    }
}
