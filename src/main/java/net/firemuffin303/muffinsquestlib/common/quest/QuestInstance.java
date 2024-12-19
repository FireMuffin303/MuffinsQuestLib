package net.firemuffin303.muffinsquestlib.common.quest;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.firemuffin303.muffinsquestlib.MuffinsQuestLib;
import net.firemuffin303.muffinsquestlib.common.network.UpdateQuestInstancePacket;
import net.firemuffin303.muffinsquestlib.common.quest.data.QuestData;
import net.firemuffin303.muffinsquestlib.common.registry.ModRegistries;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class QuestInstance {
    private Quest quest;
    private Map<QuestType<?>,List<Integer>> progress = Maps.newHashMap();
    private List<UUID> questEntitiesUUID = new ArrayList<>();
    private State state = State.PROGRESSING;
    public int time;

    public static final Codec<Map<QuestType<?>,List<Integer>>> PROGRESS_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(ModRegistries.QUEST_TYPE_REGISTRY.getCodec(),Codec.INT.listOf()).fieldOf("progress").forGetter(questTypeListMap -> questTypeListMap)
    ).apply(instance,questTypeListMap -> {
        //Codec always return ImmutableMap and ImmutableList. So we need to remake Map and List to be mutable.
        Map<QuestType<?>,List<Integer>> progress = Maps.newHashMap();
        questTypeListMap.forEach((questType, list) -> {
            List<Integer> newList = new ArrayList<>();
            newList.addAll(list);
            progress.put(questType,newList);
        });
        return progress;
    }));

    public QuestInstance(Quest quest,int time){
        this.quest = quest;
        this.time = time;
    }

    public Quest getQuest() {
        return quest;
    }

    public List<QuestData> getQuestData(QuestType<?> questType){
        return this.getQuest().getQuests(questType);
    }

    public int getTime() {
        return time;
    }

    //--- Progresses ---

    public void setProgress(Map<QuestType<?>, List<Integer>> progress) {
        this.progress = progress;
    }

    public List<Integer> getProgressType(QuestType<?> questType) {
        if(this.progress.get(questType) == null){
            List<Integer> list = new ArrayList<>();
            for(QuestData questData : this.getQuest().getQuestType(questType)) {
                list.add(0);
            }
            this.progress.put(questType,list);
        }
        return this.progress.get(questType);
    }

    public void addProgression(QuestType<?> questType, int index, int value) {
        List<Integer> list = this.getProgressType(questType);
        list.set(index,list.get(index) + value);
        this.checkProgression();
    }

    public void setProgression(QuestType<?> questType, int index, int value) {
        List<Integer> list = this.getProgressType(questType);
        list.set(index,value);
        this.checkProgression();
    }

    public void checkProgression(){
        AtomicInteger k = new AtomicInteger(0);
        this.quest.questTypes.forEach((questType, list) -> {
            int j = 0;
            for (int i = 0; i < list.size() ; ++i){
                if(this.getProgressType(questType).get(i) >= list.get(i).getRequirementAmount()){
                    j++;
                }
            }
            if(j == list.size()){
                k.getAndIncrement();
            }
        });

        this.setState(this.quest.questTypes.size() == k.get() ? State.SUCCESS : State.PROGRESSING);
    }

    //--- Quest State ---

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    //--- Rewards ---

    public List<ItemStack> getRewards() {
        return quest.definition.rewards();
    }

    //--- Quest Entities ---
    public void addQuestEntity(UUID uuid, ServerPlayerEntity serverPlayerEntity){
        this.questEntitiesUUID.add(uuid);
        ServerPlayNetworking.send(serverPlayerEntity,new UpdateQuestInstancePacket(this));
    }

    public void removeQuestEntity(UUID uuid,ServerPlayerEntity serverPlayerEntity){
        this.questEntitiesUUID.remove(uuid);
        ServerPlayNetworking.send(serverPlayerEntity,new UpdateQuestInstancePacket(this));
    }

    public List<UUID> getQuestEntitiesUUID(){
        return this.questEntitiesUUID;
    }

    //NBT
    public void writeNbt(PlayerEntity player, NbtCompound nbtCompound){
        World world = player.getWorld();
        NbtCompound questInstance = new NbtCompound();
        Identifier identifier = world.getRegistryManager().get(ModRegistries.QUEST_KEY).getId(this.quest);
        Objects.requireNonNull(identifier);
        questInstance.putString("QuestID", identifier.toString());


        PROGRESS_CODEC.encodeStart(NbtOps.INSTANCE,this.progress)
                .resultOrPartial(MuffinsQuestLib.LOGGER::error).ifPresent(nbtElement -> questInstance.put("Progress",nbtElement));
        questInstance.putString("State",this.state.name());
        questInstance.putInt("Time",this.time);

        NbtList questEntities = new NbtList();
        for(int i = 0 ; i < this.questEntitiesUUID.size(); i++){
            questEntities.add(i, NbtHelper.fromUuid(this.questEntitiesUUID.get(i)));
        }

        questInstance.put("QuestEntities",questEntities);

        nbtCompound.put("QuestInstance",questInstance);





    }

    public static QuestInstance readNbt(PlayerEntity player,NbtCompound nbtCompound){

        NbtCompound questInstanceNBT = nbtCompound.getCompound("QuestInstance");
        Quest quest = player.getWorld().getRegistryManager().get(ModRegistries.QUEST_KEY).get(Identifier.tryParse(questInstanceNBT.getString("QuestID")));
        Objects.requireNonNull(quest);
        int time = questInstanceNBT.getInt("Time");

        QuestInstance questInstance = new QuestInstance(quest,time);
        State state = State.valueOf(questInstanceNBT.getString("State"));
        questInstance.setState(state);
        PROGRESS_CODEC.parse(new Dynamic<>(NbtOps.INSTANCE,questInstanceNBT.get("Progress")))
                .resultOrPartial(MuffinsQuestLib.LOGGER::error).ifPresent(questInstance::setProgress);

        NbtList questEntitiesNBT = questInstanceNBT.getList("QuestEntities",11);
        List<UUID> questEntities = new ArrayList<>();
        for(int i = 0;i < questEntitiesNBT.size();i++){
            questEntities.add(NbtHelper.toUuid(questEntitiesNBT.get(i)));
        }

        questInstance.questEntitiesUUID = questEntities;

        return questInstance;
    }



    //Network
    public void toPacket(PacketByteBuf packetByteBuf) {
        this.quest.toPacket(packetByteBuf);
        packetByteBuf.writeMap(this.progress,
                (keyBuf,questType) ->keyBuf.writeRegistryValue(ModRegistries.QUEST_TYPE_REGISTRY,questType),
                (valueBuf,list) -> valueBuf.writeCollection(list,PacketByteBuf::writeInt));
        packetByteBuf.writeEnumConstant(this.state);
        packetByteBuf.writeInt(this.time);
        packetByteBuf.writeCollection(this.questEntitiesUUID, PacketByteBuf::writeUuid);
    }

    public static QuestInstance fromPacket(PacketByteBuf packetByteBuf) {
        Quest quest = Quest.fromPacket(packetByteBuf);
        Map<QuestType<?>,List<Integer>> map = packetByteBuf.readMap(
                keyBuf -> keyBuf.readRegistryValue(ModRegistries.QUEST_TYPE_REGISTRY),
                valueBuf -> valueBuf.readCollection(ArrayList<Integer>::new,PacketByteBuf::readInt));
        State state = packetByteBuf.readEnumConstant(State.class);
        int time = packetByteBuf.readInt();
        List<UUID> questEntities = packetByteBuf.readCollection(ArrayList::new,PacketByteBuf::readUuid);

        QuestInstance questInstance = new QuestInstance(quest,time);

        questInstance.setState(state);
        questInstance.setProgress(map);
        questInstance.questEntitiesUUID = questEntities;

        return questInstance;
    }


    public enum State{
        PROGRESSING("progressing"),
        FAIL("fail"),
        SUCCESS("success");

        State(String progressing) {

        }
    }
}
