package net.firemuffin303.muffinsquestlib.common.quest.condition;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.firemuffin303.muffinsquestlib.common.network.UpdateQuestInstancePacket;
import net.firemuffin303.muffinsquestlib.common.quest.PlayerQuestData;
import net.firemuffin303.muffinsquestlib.common.quest.data.BreakBlockQuestData;
import net.firemuffin303.muffinsquestlib.common.registry.QuestTypes;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class BreakBlockCondition {

    public void trigger(ServerPlayerEntity serverPlayerEntity, BlockState blockState){
        PlayerQuestData playerQuestData = ((PlayerQuestData.PlayerQuestDataAccessor)serverPlayerEntity).questLib$getData();
        if(playerQuestData.getQuestInstance() != null && playerQuestData.getQuestInstance().getQuest().hasQuestType(QuestTypes.BREAK_BLOCK_DATA)){
            List<BreakBlockQuestData> quests = playerQuestData.getQuestInstance().getQuest().getQuestType(QuestTypes.BREAK_BLOCK_DATA);

            for(int i = 0; i < quests.size();i++){
                if(quests.get(i).checkBlock(serverPlayerEntity,blockState)){
                    playerQuestData.getQuestInstance().addProgression(QuestTypes.BREAK_BLOCK_DATA,i, 1);
                }
            }

            ServerPlayNetworking.send(serverPlayerEntity,new UpdateQuestInstancePacket(playerQuestData.getQuestInstance()));
        }
    }
}
