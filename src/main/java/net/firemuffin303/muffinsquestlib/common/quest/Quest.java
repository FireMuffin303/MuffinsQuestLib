package net.firemuffin303.muffinsquestlib.common.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.firemuffin303.muffinsquestlib.MuffinsQuestLib;
import net.firemuffin303.muffinsquestlib.common.quest.data.QuestData;
import net.firemuffin303.muffinsquestlib.common.registry.ModRegistries;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Quest {
    public Map<QuestType<?>, List<QuestData>> questTypes = new HashMap<>();
    public QuestRarity questRarity = QuestRarity.COMMON;
    public Definition definition;
    public String description;

    /*

    public static final Codec<Quest> CODEC = RecordCodecBuilder.create(instance -> instance.group(

            QuestData.
            Codec.STRING.fieldOf("rarity").forGetter(quest -> quest.questRarity.name()),
            Definition.CODEC.fieldOf("definition").forGetter(quest -> quest.definition),
            Codec.STRING.fieldOf("description").forGetter(quest -> quest.description)
    ).apply(instance,Quest::new));

     */


    public Quest(Definition definition,String description){
        this.definition = definition;
        this.description = description;
    }

    public Quest addQuest(QuestType<?> questType, QuestData quest){
        this.getQuests(questType).add(quest);
        return this;
    }

    public Quest setRarity(QuestRarity questRarity){
        this.questRarity = questRarity;
        return this;
    }

    public List<QuestData> getQuests(QuestType<?> questType) {
        this.questTypes.computeIfAbsent(questType, k -> new ArrayList<>());
        return this.questTypes.get(questType);
    }

    public void setQuestTypes(Map<QuestType<?>, List<QuestData>> questTypes) {
        this.questTypes = questTypes;
    }

    //Network
    public void toPacket(PacketByteBuf packetByteBuf) {
        packetByteBuf.writeMap(this.questTypes,
                (keyBuf, questType) -> keyBuf.writeRegistryValue(ModRegistries.QUEST_TYPE_REGISTRY,questType),
                (valueBuf, questData) -> valueBuf.writeCollection(questData,(packetByteBuf1, questData1) -> questData1.toPacket(packetByteBuf1)));
        this.definition.toPacket(packetByteBuf);
        packetByteBuf.writeString(this.description);
        packetByteBuf.writeEnumConstant(this.questRarity);
    }

    public static Quest fromPacket(PacketByteBuf packetByteBuf) {
        AtomicReference<QuestType<?>> questTypeAtomic = new AtomicReference<>();
        Map<QuestType<?>, List<QuestData>> map = packetByteBuf.readMap(
                keyBuf -> {
                    questTypeAtomic.set(keyBuf.readRegistryValue(ModRegistries.QUEST_TYPE_REGISTRY));
                    return questTypeAtomic.get();
                },
                valueBuf -> valueBuf.readCollection(ArrayList::new,(buf) ->{
                    return questTypeAtomic.get().function.apply(buf);
                }
        ));
        Definition definition = Definition.fromPacket(packetByteBuf);
        String text = packetByteBuf.readString();
        QuestRarity questRarity = packetByteBuf.readEnumConstant(QuestRarity.class);

        Quest quest = new Quest(definition,text);
        quest.setRarity(questRarity);
        quest.setQuestTypes(map);
        return quest;
    }

    public record Definition(List<ItemStack> rewards, int experience){
        public static final Codec<Definition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.CODEC.listOf().fieldOf("reward_item").forGetter(Definition::rewards),
                Codec.INT.fieldOf("experience").forGetter(Definition::experience)
        ).apply(instance,Definition::new));

        public void toPacket(PacketByteBuf packetByteBuf) {
            packetByteBuf.writeCollection(this.rewards, PacketByteBuf::writeItemStack);
            packetByteBuf.writeInt(this.experience);
        }

        public static Definition fromPacket(PacketByteBuf packetByteBuf){
            List<ItemStack> itemStacks = packetByteBuf.readCollection(ArrayList::new, PacketByteBuf::readItemStack);
            int experience = packetByteBuf.readInt();
            return new Definition(itemStacks,experience);
        }
    }

    public enum QuestRarity{
        COMMON("common"),
        RARE("rare");

        QuestRarity(String value) {
        }

    }
}
