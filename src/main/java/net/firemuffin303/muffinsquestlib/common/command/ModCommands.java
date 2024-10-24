package net.firemuffin303.muffinsquestlib.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.firemuffin303.muffinsquestlib.MuffinsQuestLib;
import net.firemuffin303.muffinsquestlib.common.PlayerQuestData;
import net.firemuffin303.muffinsquestlib.common.quest.Quest;
import net.firemuffin303.muffinsquestlib.common.quest.QuestInstance;
import net.firemuffin303.muffinsquestlib.common.registry.ModRegistries;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.Iterator;

public class ModCommands {
    private static final SimpleCommandExceptionType CLEAR_EVERYTHING_FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.quest.clear.everything.failed"));

    public static void init(){
        CommandRegistrationCallback.EVENT.register(ModCommands::questCommand);
    }


    public static void questCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment registrationEnvironment){
        dispatcher.register(CommandManager.literal("quest").requires(serverCommandSource -> {
                    return serverCommandSource.hasPermissionLevel(2);
                })
                .then(CommandManager.literal("clear").executes(commandContext -> {
                    Entity entity = commandContext.getSource().getEntity();
                    if(entity instanceof ServerPlayerEntity serverPlayerEntity){
                        ((PlayerQuestData.PlayerQuestDataAccessor)serverPlayerEntity).questLib$getData().clearQuest(serverPlayerEntity);
                        commandContext.getSource().sendFeedback(() -> Text.translatable("command.questlib.clear.success",serverPlayerEntity.getDisplayName()),true);
                        return 1;
                    }
                    throw CLEAR_EVERYTHING_FAILED_EXCEPTION.create();
                }))
                .then(CommandManager.literal("get").executes(commandContext -> {
                    Entity entity = commandContext.getSource().getEntity();
                    if(entity instanceof ServerPlayerEntity serverPlayerEntity){
                        QuestInstance playerQuestInstance = ((PlayerQuestData.PlayerQuestDataAccessor)serverPlayerEntity).questLib$getData().getQuestInstance();
                        if(playerQuestInstance != null){
                            commandContext.getSource().sendFeedback(() -> Text.literal("ffasfasf"),false);
                            return 1;
                        }

                        commandContext.getSource().sendFeedback(() -> Text.literal("Target has no quests"),false);
                        return 1;
                    }
                    throw CLEAR_EVERYTHING_FAILED_EXCEPTION.create();
                }))
                .then(CommandManager.literal("give")
                        .then(CommandManager.argument("targets", EntityArgumentType.players())
                                .then(CommandManager.argument("quest", RegistryEntryArgumentType.registryEntry(registryAccess, ModRegistries.QUEST_KEY))
                                        .then(CommandManager.argument("seconds", IntegerArgumentType.integer(1,1000000)).executes(commandContext -> {

                                            Collection<ServerPlayerEntity> playerCollection = EntityArgumentType.getPlayers(commandContext,"targets");
                                            Iterator<ServerPlayerEntity> players = playerCollection.iterator();
                                            int i = 0;
                                            while(players.hasNext()){
                                                ServerPlayerEntity serverPlayerEntity = players.next();
                                                Quest quest = RegistryEntryArgumentType.getRegistryEntry(commandContext,"quest",ModRegistries.QUEST_KEY).value();
                                                int integer = IntegerArgumentType.getInteger(commandContext,"seconds");
                                                ((PlayerQuestData.PlayerQuestDataAccessor)serverPlayerEntity).questLib$getData().setQuestInstance(new QuestInstance(quest,integer*20));
                                                i++;
                                            }

                                            if(i == 0){
                                                throw CLEAR_EVERYTHING_FAILED_EXCEPTION.create();
                                            } else{
                                                if(i == 1){
                                                    commandContext.getSource().sendFeedback(() -> Text.translatable("command.questlib.give.success.single",playerCollection.iterator().next().getDisplayName()),true);
                                                }else {
                                                    commandContext.getSource().sendFeedback(() -> Text.translatable("command.questlib.give.success.multiples",playerCollection.size()),true);

                                                }
                                                return i;
                                            }
                                }))
                        )
                ))
        );
    }
}
