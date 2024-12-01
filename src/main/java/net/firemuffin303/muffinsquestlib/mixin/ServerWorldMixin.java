package net.firemuffin303.muffinsquestlib.mixin;

import net.firemuffin303.muffinsquestlib.common.KillQuestSpawner;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
    @Unique
    KillQuestSpawner killQuestSpawner = new KillQuestSpawner();

    @Inject(method = "tickSpawners",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/spawner/Spawner;spawn(Lnet/minecraft/server/world/ServerWorld;ZZ)I"))
    public void muffinsQuestLib$questSpawner(boolean spawnMonsters, boolean spawnAnimals, CallbackInfo ci){
        killQuestSpawner.spawn((ServerWorld) (Object)this,spawnMonsters,spawnAnimals);
    }
}
