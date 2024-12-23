package net.firemuffin303.muffinsquestlib.mixin.questSpawner;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.spawner.Spawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ServerWorld.class)
public interface ServerWorldAccessor {
    @Accessor("spawners")
    @Mutable
    List<Spawner> getSpawner();

    @Accessor("spawners")
    @Mutable
    void setSpawners(List<Spawner> spawners);
}
