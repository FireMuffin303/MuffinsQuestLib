package net.firemuffin303.muffinsquestlib.common.quest.condition;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.firemuffin303.muffinsquestlib.MuffinsQuestLib;
import net.firemuffin303.muffinsquestlib.common.PlayerQuestData;
import net.firemuffin303.muffinsquestlib.common.network.UpdateQuestInstancePacket;
import net.firemuffin303.muffinsquestlib.common.quest.data.QuestData;
import net.firemuffin303.muffinsquestlib.common.registry.ModQuestTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.List;

public class KillMobCondition {

    public void trigger(ServerPlayerEntity serverPlayerEntity, ServerWorld world, LivingEntity target){
        PlayerQuestData playerQuestData = ((PlayerQuestData.PlayerQuestDataAccessor)serverPlayerEntity).questLib$getData();
        if(playerQuestData.getQuestInstance() != null && playerQuestData.getQuestInstance().getQuest().hasQuestType(ModQuestTypes.KILL_ENTITY_DATA)){
            List<QuestData> quests = playerQuestData.getQuestInstance().getQuest().getQuestType(ModQuestTypes.KILL_ENTITY_DATA);

            for(int i = 0; i < quests.size();i++){
                if(quests.get(i).checkKillOther(serverPlayerEntity,world,target)){
                    playerQuestData.getQuestInstance().addProgression(ModQuestTypes.KILL_ENTITY_DATA,i,1);
                }
            }

            ServerPlayNetworking.send(serverPlayerEntity,new UpdateQuestInstancePacket(playerQuestData.getQuestInstance()));
        }
    }
}
