package net.firemuffin303.muffinsquestlib.common.registry;

import net.firemuffin303.muffinsquestlib.MuffinsQuestLib;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class QuestSoundEvents {
    public static final SoundEvent QUEST_PAPER_USE = register("item.quest_paper.use");

    public static void init() {}

    public static SoundEvent register(String id){
        Identifier identifier = MuffinsQuestLib.modId(id);
        return Registry.register(Registries.SOUND_EVENT,identifier,SoundEvent.of(identifier));
    }
}
