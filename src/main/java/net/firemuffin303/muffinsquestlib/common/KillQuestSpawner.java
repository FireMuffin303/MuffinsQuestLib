package net.firemuffin303.muffinsquestlib.common;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.firemuffin303.muffinsquestlib.MuffinsQuestLib;
import net.firemuffin303.muffinsquestlib.common.quest.QuestInstance;
import net.firemuffin303.muffinsquestlib.common.quest.data.KillEntityQuestData;
import net.firemuffin303.muffinsquestlib.common.quest.data.QuestData;
import net.firemuffin303.muffinsquestlib.common.registry.ModQuestTypes;
import net.firemuffin303.muffinsquestlib.common.registry.ModTags;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.spawner.Spawner;

import java.util.*;

public class KillQuestSpawner implements Spawner {
    private int cooldown;

    public KillQuestSpawner(){

    }

    @Override
    public int spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals) {
        if(!spawnMonsters) {
            return 0;
        } else if (!world.getGameRules().getBoolean(MuffinsQuestLib.DO_QUEST_TARGET_ENTITY_SPAWN)) {
            return 0;
        } else{
            Random random = world.random;
            --this.cooldown;
            if(this.cooldown > 0){
                return 0;
            }else{
                this.cooldown += 500 + random.nextInt(200);
                int i = world.getPlayers().size();
                //If there's no player on the server, cancel spawn
                if (i < 1) {
                    return 0;
                }

                List<ServerPlayerEntity> playerEntities = world.getPlayers(serverPlayerEntity -> {
                    PlayerQuestData playerQuestData = ((PlayerQuestData.PlayerQuestDataAccessor)serverPlayerEntity).questLib$getData();
                    return  playerQuestData.getQuestInstance() != null && playerQuestData.getQuestInstance().getQuest().hasQuestType(ModQuestTypes.KILL_ENTITY_DATA) && !serverPlayerEntity.isSpectator();
                });

                if(playerEntities.isEmpty()){
                    return 0;
                }

                int mobSpawned = 0;

                for (ServerPlayerEntity serverPlayerEntity : playerEntities){

                    //We don't want mob to spawn near villages or workstation. so we cancel if there's any POI block nearby.
                    if(!world.isNearOccupiedPointOfInterest(serverPlayerEntity.getBlockPos(),2)){

                        PlayerQuestData playerQuestData = ((PlayerQuestData.PlayerQuestDataAccessor)serverPlayerEntity).questLib$getData();

                        if(playerQuestData.getQuestInstance() == null){
                            return 0;
                        }

                        QuestInstance questInstance = playerQuestData.getQuestInstance();

                        List<QuestData> questDataList = new ArrayList<>(questInstance.getQuestData(ModQuestTypes.KILL_ENTITY_DATA));
                        List<Integer> progressList = new ArrayList<>(questInstance.getProgressType(ModQuestTypes.KILL_ENTITY_DATA));

                        for(int j = questDataList.size() -1 ; j >= 0; j--){
                            if(questDataList.get(j).getRequirementAmount() == progressList.get(j)){
                                questDataList.remove(j);
                                progressList.remove(j);
                            }
                        }

                        int index = world.getRandom().nextInt(questDataList.size());

                        QuestData questData = questDataList.get(index);
                        int progress = progressList.get(index);

                        int spawnedEntity = 0;

                        if(questData instanceof KillEntityQuestData killEntityQuestData){
                            for(UUID uuid: questInstance.getQuestEntitiesUUID()){
                                Entity entity = world.getEntity(uuid);
                                if(entity != null && entity.getType() == killEntityQuestData.getEntityRequirements().entityType()){
                                    spawnedEntity++;
                                }
                            }

                            if(spawnedEntity >= killEntityQuestData.getRequirementAmount() - progress){
                                return 0;
                            }

                            int randomX = (12 + random.nextInt(8)) * (random.nextBoolean() ? -1 : 1);
                            int randomZ = (12 + random.nextInt(8)) * (random.nextBoolean() ? -1 : 1);
                            BlockPos.Mutable mutable = serverPlayerEntity.getBlockPos().mutableCopy().move(randomX, 0, randomZ);

                            //Check if chunk loaded
                            if (world.isRegionLoaded(mutable.getX() - 10, mutable.getZ() - 10, mutable.getX() + 10, mutable.getZ() + 10)) {
                                if(this.spawnEntity(world,mutable,serverPlayerEntity,killEntityQuestData)){
                                    mobSpawned++;

                                }
                            }
                        }

                    }

                }

                return mobSpawned;
            }
        }
    }

    public boolean spawnEntity(ServerWorld world,BlockPos blockPos,ServerPlayerEntity serverPlayerEntity,KillEntityQuestData killEntityQuestData){
        QuestInstance questInstance = ((PlayerQuestData.PlayerQuestDataAccessor)serverPlayerEntity).questLib$getData().getQuestInstance();
        if(questInstance == null){
            return false;
        }

        EntityType<?> entityType = killEntityQuestData.getEntityRequirements().entityType();
        int questAmount = killEntityQuestData.getRequirementAmount();


        if(entityType.isIn(ModTags.QUEST_SPAWN_BLACKLIST)){
            return false;
        }

        //We check structure to gatekeep mobs to not spawn in the place they should not be.
        Map<Structure, LongSet> structureReferences = world.getStructureAccessor().getStructureReferences(serverPlayerEntity.getBlockPos());
        if(!structureReferences.isEmpty()){
            for(Structure structure : structureReferences.keySet()){
                StructureSpawns structureSpawns = structure.getStructureSpawns().get(entityType.getSpawnGroup());
                if (structureSpawns != null){
                    for(SpawnSettings.SpawnEntry spawnEntry:structureSpawns.spawns().getEntries()){
                        if(spawnEntry.type == entityType){
                            if(trySpawn(serverPlayerEntity,questInstance,world,blockPos,entityType)){
                                return true;
                            }
                        }
                    }
                }
            }
        }


        Biome biome = world.getBiome(serverPlayerEntity.getBlockPos()).value();
        if(biome != null){
            Pool<SpawnSettings.SpawnEntry> spawnEntryPool = biome.getSpawnSettings().getSpawnEntries(entityType.getSpawnGroup());
            if(!spawnEntryPool.isEmpty()){
                for(SpawnSettings.SpawnEntry spawnEntry:spawnEntryPool.getEntries()){
                    if(spawnEntry.type == entityType){
                        if(trySpawn(serverPlayerEntity,questInstance,world,blockPos,entityType)){
                            return true;
                        }
                    }
                }
            }
        }




        return false;
    }


    private boolean trySpawn(ServerPlayerEntity serverPlayerEntity,QuestInstance questInstance,ServerWorld world,BlockPos blockPos,EntityType<?> entityType){
        int yCheck = 0;
        blockPos = blockPos.up(10);

        do{
            boolean bl = SpawnHelper.canSpawn(SpawnRestriction.Location.ON_GROUND,world,blockPos,entityType);
            if(bl){
                Entity entity = entityType.create(world);
                if(entity != null){
                    entity.setPosition(blockPos.toCenterPos());
                    if(entity instanceof MobEntity mobEntity){
                        world.playSound(null,blockPos, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.NEUTRAL,5.0f,1.0f);
                        Random random = world.random;
                        float ak = world.random.nextFloat() * 4.0F;
                        float ao = random.nextFloat() * 6.2831855F;
                        double f = (double)(MathHelper.cos(ao) * ak);
                        double y = 0.01 + random.nextDouble() * 0.5;
                        double z = (double)(MathHelper.sin(ao) * ak);
                        world.spawnParticles(ParticleTypes.CLOUD, blockPos.getX() + f * 0.1, blockPos.getY() + 0.3, blockPos.getZ() + z * 0.1,20,f,y,z,2);

                        mobEntity.setTarget(serverPlayerEntity);
                        mobEntity.initialize(world,world.getLocalDifficulty(blockPos), SpawnReason.REINFORCEMENT,null,null);

                        if(mobEntity instanceof QuestEntityData.QuestEntityDataAccessor questEntityDataAccessor){
                            QuestEntityData questEntityData = questEntityDataAccessor.getQuestEntityData();
                            questEntityData.setQuestMarked(true);
                            questEntityData.setPlayerUUID(serverPlayerEntity.getUuid());
                        }

                        questInstance.addQuestEntity(mobEntity.getUuid(),serverPlayerEntity);
                    }

                    world.spawnEntityAndPassengers(entity);



                    return true;
                }
            }

            blockPos = blockPos.down();
            yCheck += 1;
        }while(yCheck <= 20);

        return false;
    }
}




