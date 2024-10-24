package net.firemuffin303.muffinsquestlib.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.firemuffin303.muffinsquestlib.common.PlayerQuestData;
import net.firemuffin303.muffinsquestlib.common.quest.QuestInstance;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

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
                })));
    }
}
