package net.firemuffin303.muffinsquestlib.common.quest.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;

import java.io.Reader;

public record BlockRequirementEntry(Block block,int amount) {
    public static final Codec<BlockRequirementEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Registries.BLOCK.getCodec().fieldOf("block").forGetter(BlockRequirementEntry::block),
            Codec.INT.fieldOf("amount").forGetter(BlockRequirementEntry::amount)
    ).apply(instance,BlockRequirementEntry::new));
}
