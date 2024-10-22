package net.firemuffin303.muffinsquestlib.common.quest;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.firemuffin303.muffinsquestlib.MuffinsQuestLib;
import net.firemuffin303.muffinsquestlib.common.quest.data.QuestData;
import net.firemuffin303.muffinsquestlib.common.registry.ModRegistries;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class QuestInstance {
    private Quest quest;
    private Map<QuestType<?>,List<Integer>> progress = new HashMap<>();
    private State state = State.PROGRESSING;
    public int time;

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
        PROGRESSING,
        FAIL,
        SUCCESS
    }
}
