package net.firemuffin303.muffinsquestlib.common;

import com.mojang.logging.LogUtils;
import net.fabricmc.loader.impl.util.log.Log;
import net.firemuffin303.muffinsquestlib.MuffinsQuestLib;
import net.firemuffin303.muffinsquestlib.common.quest.QuestInstance;
import net.firemuffin303.muffinsquestlib.common.quest.data.KillEntityQuestData;
import net.firemuffin303.muffinsquestlib.common.quest.data.QuestData;
import net.firemuffin303.muffinsquestlib.common.registry.ModQuestTypes;
import net.firemuffin303.muffinsquestlib.common.registry.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.spawner.Spawner;

import java.util.List;
import java.util.Objects;

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
                this.cooldown += 600 + random.nextInt(600);
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
                    LogUtils.getLogger().info(serverPlayerEntity.getDisplayName().getString());

                    //We don't want mob to spawn near villages or workstation. so we cancel if there's any POI block nearby.
                    if(!world.isNearOccupiedPointOfInterest(serverPlayerEntity.getBlockPos(),2)){

                        PlayerQuestData playerQuestData = ((PlayerQuestData.PlayerQuestDataAccessor)serverPlayerEntity).questLib$getData();
                        List<QuestData> data = playerQuestData.getQuestInstance().getQuestData(ModQuestTypes.KILL_ENTITY_DATA);

                        for (QuestData questData : data){
                            if(questData instanceof KillEntityQuestData killEntityQuestData){
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

                }

                return mobSpawned;
            }
        }
    }

    public boolean spawnEntity(ServerWorld world,BlockPos blockPos,ServerPlayerEntity serverPlayerEntity,KillEntityQuestData killEntityQuestData){
        QuestInstance questInstance = ((PlayerQuestData.PlayerQuestDataAccessor)serverPlayerEntity).questLib$getData().getQuestInstance();
        Objects.requireNonNull(questInstance);
        EntityType<?> entityType = killEntityQuestData.getEntityRequirements().entityType();
        int questAmount = killEntityQuestData.getRequirementAmount();
        BlockState blockState = world.getBlockState(blockPos);
        int yCheck = 0;
        blockPos = blockPos.up(10);
        boolean bl = false;

        if(entityType.isIn(ModTags.QUEST_SPAWN_BLACKLIST)){
            return false;
        }

        do{
            bl = SpawnHelper.canSpawn(SpawnRestriction.Location.ON_GROUND,world,blockPos,entityType);
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




