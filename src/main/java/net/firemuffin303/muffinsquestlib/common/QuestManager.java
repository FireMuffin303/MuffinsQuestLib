package net.firemuffin303.muffinsquestlib.common;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.firemuffin303.muffinsquestlib.MuffinsQuestLib;
import net.firemuffin303.muffinsquestlib.common.quest.Quest;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.IOException;
import java.io.Reader;
import java.util.*;

public class QuestManager implements SimpleSynchronousResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private Map<Identifier,Quest> questById = ImmutableMap.of();

    @Override
    public Identifier getFabricId() {
        return new Identifier(MuffinsQuestLib.MOD_ID,"quest_loader");
    }

    @Override
    public void reload(ResourceManager manager) {
        ResourceFinder questFinder = ResourceFinder.json("muffins_questlib/quest");
        Iterator<Map.Entry<Identifier, Resource>> iterator = questFinder.findResources(manager).entrySet().iterator();

        ImmutableMap.Builder<Identifier,Quest> builder = ImmutableMap.builder();
        while(iterator.hasNext()){
            Map.Entry<Identifier, Resource> entry = iterator.next();
            Identifier identifier = entry.getKey();
            Identifier identifier1 = questFinder.toResourceId(identifier);

            try {
                Reader reader = entry.getValue().getReader();

                JsonElement jsonElement = JsonHelper.deserialize(GSON, reader, JsonElement.class);

                DataResult<Quest> dataResult = Quest.CODEC.parse(new Dynamic<>(JsonOps.INSTANCE,jsonElement));
                Optional<Quest> quest = dataResult.resultOrPartial(MuffinsQuestLib.LOGGER::error);
                quest.ifPresent(value -> {
                    builder.put(identifier1, value);
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        this.questById = builder.build();

        MuffinsQuestLib.LOGGER.info(String.format("Loaded %d quests",this.questById.size()));
    }

    public Map<Identifier, Quest> getQuestById() {
        return questById;
    }


    public void setQuestById(Map<Identifier,Quest> map){
        this.questById = map;
    }
}
