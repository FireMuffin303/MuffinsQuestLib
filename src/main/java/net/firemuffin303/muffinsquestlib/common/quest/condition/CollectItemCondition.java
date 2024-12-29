package net.firemuffin303.muffinsquestlib.common.quest.condition;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.firemuffin303.muffinsquestlib.common.PlayerQuestData;
import net.firemuffin303.muffinsquestlib.common.network.UpdateQuestInstancePacket;
import net.firemuffin303.muffinsquestlib.common.quest.data.CollectItemQuestData;
import net.firemuffin303.muffinsquestlib.common.quest.data.QuestData;
import net.firemuffin303.muffinsquestlib.common.registry.ModQuestTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class CollectItemCondition {

    public void trigger(ServerPlayerEntity serverPlayerEntity, ItemStack item){
        PlayerQuestData playerQuestData = ((PlayerQuestData.PlayerQuestDataAccessor)serverPlayerEntity).questLib$getData();
        if(playerQuestData.getQuestInstance() != null && playerQuestData.getQuestInstance().getQuest().hasQuestType(ModQuestTypes.COLLECT_ITEM_DATA)){
            List<QuestData> quests = playerQuestData.getQuestInstance().getQuest().getQuestType(ModQuestTypes.COLLECT_ITEM_DATA);

            for(int i = 0; i < quests.size();i++){
                if(quests.get(i).checkItem(serverPlayerEntity,item) && playerQuestData.getQuestInstance().getProgressType(ModQuestTypes.COLLECT_ITEM_DATA).get(i) < item.getCount()){
                    playerQuestData.getQuestInstance().setProgression(ModQuestTypes.COLLECT_ITEM_DATA,i, item.getCount());
                }
            }

            ServerPlayNetworking.send(serverPlayerEntity,new UpdateQuestInstancePacket(playerQuestData.getQuestInstance()));
        }
    }
}
