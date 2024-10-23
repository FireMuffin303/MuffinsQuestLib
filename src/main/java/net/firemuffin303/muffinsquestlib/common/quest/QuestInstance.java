package net.firemuffin303.muffinsquestlib.common.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.firemuffin303.muffinsquestlib.MuffinsQuestLib;
import net.firemuffin303.muffinsquestlib.common.quest.data.QuestData;
import net.firemuffin303.muffinsquestlib.common.registry.ModRegistries;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class QuestInstance {
    private Quest quest;
    private Map<QuestType<?>,List<Integer>> progress = new HashMap<>();
    private State state = State.PROGRESSING;
    public int time;

    public static final Codec<Map<QuestType<?>,List<Integer>>> PROGRESS_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(ModRegistries.QUEST_TYPE_REGISTRY.getCodec(),Codec.INT.listOf()).fieldOf("progress").forGetter(questTypeListMap -> questTypeListMap)
    ).apply(instance,Map::copyOf));

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

    public Map<QuestType<?>,List<Integer>> setProgressCodec(Map<QuestType<?>, List<Integer>> progress) {
        this.progress = progress;
        return this.progress;
    }

    public void setProgress(Map<QuestType<?>, List<Integer>> progress) {
        this.progress = progress;
    }

    public List<Integer> getProgressType(QuestType<?> questType) {
        this.progress.computeIfAbsent(questType,questType1 -> {
            List<Integer> list = new ArrayList<>();
            this.getQuest().questTypes.get(questType).forEach(quest -> list.add(0));
            return list;
        });
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

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public List<ItemStack> getRewards() {
        return quest.definition.rewards();
    }

    //NBT
    public void writeNbt(NbtCompound nbtCompound){
        /*
        DataResult<NbtElement> dataResult = Quest.CODEC.encodeStart(NbtOps.INSTANCE,this.quest);
        Objects.requireNonNull(MuffinsQuestLib.LOGGER);
        dataResult.resultOrPartial(MuffinsQuestLib.LOGGER::error).ifPresent(nbtElement -> {
            nbtCompound.put("Quest",nbtElement);
        });

         */

        NbtCompound questInstance = new NbtCompound();
        questInstance.putString("QuestID", Objects.requireNonNull(ModRegistries.QUEST_REGISTRY.getId(this.quest)).toString());


        PROGRESS_CODEC.encodeStart(NbtOps.INSTANCE,this.progress)
                .resultOrPartial(MuffinsQuestLib.LOGGER::error).ifPresent(nbtElement -> questInstance.put("Progress",nbtElement));
        questInstance.putString("State",this.state.name());
        questInstance.putInt("Time",this.time);
        nbtCompound.put("QuestInstance",questInstance);
    }

    public static QuestInstance readNbt(NbtCompound nbtCompound){

        NbtCompound questInstanceNBT = nbtCompound.getCompound("QuestInstance");
        Quest quest = ModRegistries.QUEST_REGISTRY.get(Identifier.tryParse(questInstanceNBT.getString("QuestID")));
        int time = questInstanceNBT.getInt("Time");

        QuestInstance questInstance = new QuestInstance(quest,time);
        State state = State.valueOf(questInstanceNBT.getString("State"));
        questInstance.setState(state);
        PROGRESS_CODEC.parse(new Dynamic<>(NbtOps.INSTANCE,questInstanceNBT.get("Progress")))
                .resultOrPartial(MuffinsQuestLib.LOGGER::error).ifPresent(questInstance::setProgress);


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
    }

    public static QuestInstance fromPacket(PacketByteBuf packetByteBuf) {
        Quest quest = Quest.fromPacket(packetByteBuf);
        Map<QuestType<?>,List<Integer>> map = packetByteBuf.readMap(
                keyBuf -> keyBuf.readRegistryValue(ModRegistries.QUEST_TYPE_REGISTRY),
                valueBuf -> valueBuf.readCollection(ArrayList<Integer>::new,PacketByteBuf::readInt));
        State state = packetByteBuf.readEnumConstant(State.class);
        int time = packetByteBuf.readInt();

        QuestInstance questInstance = new QuestInstance(quest,time);

        questInstance.setState(state);
        questInstance.setProgress(map);
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
