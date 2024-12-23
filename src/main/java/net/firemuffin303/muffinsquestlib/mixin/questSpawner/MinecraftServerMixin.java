package net.firemuffin303.muffinsquestlib.mixin.questSpawner;

import com.llamalad7.mixinextras.sugar.Local;
import net.firemuffin303.muffinsquestlib.common.KillQuestSpawner;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.spawner.Spawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Inject(method = "createWorlds",at = @At(value = "INVOKE",target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",ordinal = 0))
    public void questLib$addSpawnerOverworld(WorldGenerationProgressListener worldGenerationProgressListener, CallbackInfo ci, @Local(ordinal = 0) ServerWorld serverWorld){
        List<Spawner> spawners = new ArrayList<>(((ServerWorldAccessor)serverWorld).getSpawner());
        spawners.add(new KillQuestSpawner());
        ((ServerWorldAccessor)serverWorld).setSpawners(spawners);
    }

    @Inject(method = "createWorlds",at = @At(value = "INVOKE",target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",ordinal = 1))
    public void questLib$addSpawnerNonOverworld(WorldGenerationProgressListener worldGenerationProgressListener, CallbackInfo ci, @Local(ordinal = 1) ServerWorld serverWorld){
        List<Spawner> spawners = new ArrayList<>(((ServerWorldAccessor)serverWorld).getSpawner());
        spawners.add(new KillQuestSpawner());
        ((ServerWorldAccessor)serverWorld).setSpawners(spawners);
    }
}
