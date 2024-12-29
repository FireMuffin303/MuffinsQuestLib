package net.firemuffin303.muffinsquestlib.common.quest;

import com.mojang.serialization.Codec;
import net.firemuffin303.muffinsquestlib.common.quest.data.KillEntityQuestData;
import net.firemuffin303.muffinsquestlib.common.quest.data.QuestData;
import net.minecraft.network.PacketByteBuf;

import java.util.function.Function;

/*
Contains category of quests. Require Codec for data management and network packet
 */

public  class QuestType<T extends QuestData> {
    public Codec<T> codec;
    public  Function<PacketByteBuf,T> function;
    public QuestType(Codec<T> codec,Function<PacketByteBuf,T> fromPacket){
        this.codec = codec;
        this.function = fromPacket;
    }

    public Codec<T> getCodec() {
        return this.codec;
    }


    public static class Builder<T extends QuestData>{
        Codec<T> codec;
        Function<PacketByteBuf,T> function;

        public Builder<T> codec(Codec<T> codec){
            this.codec = codec;
            return this;
        }

        public Builder<T> fromPacket(Function<PacketByteBuf,T> function){
            this.function = function;
            return this;
        }

        public QuestType<T> build(){
            return new QuestType<>(this.codec,this.function);
        }
    }
}
