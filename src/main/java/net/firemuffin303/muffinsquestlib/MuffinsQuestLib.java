package net.firemuffin303.muffinsquestlib;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.firemuffin303.muffinsquestlib.common.PlayerQuestData;
import net.firemuffin303.muffinsquestlib.common.item.QuestPaperItem;
import net.firemuffin303.muffinsquestlib.common.quest.Quest;
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
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class MuffinsQuestLib implements ModInitializer {

    public static final String MOD_ID = "muffins_questlib";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final ItemGroup MOD_ITEM_GROUP = FabricItemGroup.builder()
            .displayName(Text.literal("Quest Library"))
            .icon(() -> new ItemStack(ModItems.QUEST_PAPER_ITEM))
            .entries((displayContext, entries) -> {

                displayContext.lookup().getOptionalWrapper(ModRegistries.QUEST_KEY).ifPresent(questImpl -> {
                    questImpl.streamEntries().map(questReference -> QuestPaperItem.getQuestPaper(questReference.registryKey().getValue(), questReference.value())).forEach(entries::add);
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
