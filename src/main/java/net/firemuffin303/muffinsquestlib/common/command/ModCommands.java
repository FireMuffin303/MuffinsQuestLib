package net.firemuffin303.muffinsquestlib.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.firemuffin303.muffinsquestlib.common.quest.PlayerQuestData;
import net.firemuffin303.muffinsquestlib.common.item.QuestPaperItem;
import net.firemuffin303.muffinsquestlib.common.quest.Quest;
import net.firemuffin303.muffinsquestlib.common.quest.QuestInstance;
import net.firemuffin303.muffinsquestlib.common.registry.QuestRegistries;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

public class ModCommands {
    private static final SimpleCommandExceptionType CLEAR_EVERYTHING_FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.quest.clear.everything.failed"));

    /*private static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> {
        Collection<Identifier> collection = MuffinsQuestLib.QUEST_MANAGER.getQuestById().keySet();
        return CommandSource.suggestIdentifiers(collection.stream(),suggestionsBuilder);
    };*/


    public static void init(){
        CommandRegistrationCallback.EVENT.register(ModCommands::questCommand);
    }


    public static void questCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment registrationEnvironment){
        dispatcher.register(CommandManager.literal("quest").requires(serverCommandSource -> {
                    return serverCommandSource.hasPermissionLevel(2);
                })
                .then(CommandManager.literal("clear")
                        .then(CommandManager.argument("targets",EntityArgumentType.players())
                                .executes(ModCommands::removeQuest)))
                .then(CommandManager.literal("get")
                        .then(CommandManager.argument("target",EntityArgumentType.player())
                                .executes(ModCommands::getQuest)
                        ))
                .then(CommandManager.literal("give")
                        .then(CommandManager.argument("targets", EntityArgumentType.players())
                                .then(CommandManager.argument("quest", RegistryEntryArgumentType.registryEntry(registryAccess, QuestRegistries.QUEST_KEY))
                                        .then(CommandManager.argument("seconds", IntegerArgumentType.integer(1,1000000)).executes(ModCommands::giveQuest))
                        )
                ))
                .then(CommandManager.literal("givepaper")
                        .then(CommandManager.argument("targets",EntityArgumentType.players())
                                .then( CommandManager.argument("quest",RegistryEntryArgumentType.registryEntry(registryAccess, QuestRegistries.QUEST_KEY))
                                        .then(CommandManager.argument("seconds",IntegerArgumentType.integer(1,1000000)).executes(ModCommands::givePaper))
                                )
                        )
                )
        );
    }

    private static int givePaper(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {

        Collection<ServerPlayerEntity> playerCollection = EntityArgumentType.getPlayers(commandContext,"targets");
        Iterator<ServerPlayerEntity> players = playerCollection.iterator();

        int i = 0;
        while (players.hasNext()){
            ServerPlayerEntity serverPlayerEntity = players.next();
            Optional<RegistryKey<Quest>> registryKey = RegistryEntryArgumentType.getRegistryEntry(commandContext, "quest", QuestRegistries.QUEST_KEY).getKey();
            if (registryKey.isPresent()) {
                Identifier identifier = registryKey.get().getValue();

                int integer = IntegerArgumentType.getInteger(commandContext, "seconds");

                if(serverPlayerEntity.giveItemStack(QuestPaperItem.getQuestPaper(identifier, integer * 20))){
                    i++;
                }
            }
        }

        if(i == 0){
            throw CLEAR_EVERYTHING_FAILED_EXCEPTION.create();
        } else{
            if(i == 1){
                commandContext.getSource().sendFeedback(() -> Text.translatable("command.questlib.givepaper.success.single",playerCollection.iterator().next().getDisplayName()),true);
            }else {
                commandContext.getSource().sendFeedback(() -> Text.translatable("command.questlib.givepaper.success.multiples",playerCollection.size()),true);

            }
            return i;
        }
    }

    private static int getQuest(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
        ServerPlayerEntity serverPlayerEntity = EntityArgumentType.getPlayer(commandContext,"target");

        ServerWorld serverWorld = commandContext.getSource().getWorld();

        if(serverPlayerEntity == null){
            throw CLEAR_EVERYTHING_FAILED_EXCEPTION.create();
        }

        QuestInstance playerQuestInstance = ((PlayerQuestData.PlayerQuestDataAccessor)serverPlayerEntity).questLib$getData().getQuestInstance();
        if(playerQuestInstance != null){
            Identifier identifier = serverWorld.getRegistryManager().get(QuestRegistries.QUEST_KEY).getId(playerQuestInstance.getQuest());
            commandContext.getSource().sendFeedback(() -> Text.translatable("command.questlib.getquest.success", serverPlayerEntity.getDisplayName(),identifier),false);
            return 1;
        }

        commandContext.getSource().sendFeedback(() -> Text.translatable("command.questlib.getquest.no_quest",serverPlayerEntity.getDisplayName()),false);
        return 1;


    }


    private static int removeQuest(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> collection = EntityArgumentType.getPlayers(commandContext,"targets");
        Iterator<ServerPlayerEntity> iterator = collection.iterator();

        int i = 0;
        while (iterator.hasNext()){
            ServerPlayerEntity serverPlayerEntity = iterator.next();
            ((PlayerQuestData.PlayerQuestDataAccessor)serverPlayerEntity).questLib$getData().clearQuest(serverPlayerEntity);
            i++;
        }

        if(i == 0){
            throw CLEAR_EVERYTHING_FAILED_EXCEPTION.create();
        } else{
            if(i == 1){
                commandContext.getSource().sendFeedback(() -> Text.translatable("command.questlib.clear.single",collection.iterator().next().getDisplayName()),true);
            }else {
                commandContext.getSource().sendFeedback(() -> Text.translatable("command.questlib.clear.multiples",collection.size()),true);

            }
            return i;
        }

    }

    private static int giveQuest(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> playerCollection = EntityArgumentType.getPlayers(commandContext,"targets");
        ServerWorld serverWorld = commandContext.getSource().getWorld();
        Iterator<ServerPlayerEntity> players = playerCollection.iterator();
        int i = 0;
        while(players.hasNext()){
            ServerPlayerEntity serverPlayerEntity = players.next();
            //Identifier identifier = IdentifierArgumentType.getIdentifier(commandContext,"quest");
            //Quest quest = serverWorld.getRegistryManager().get(ModRegistries.QUEST_KEY).get(identifier);

            Quest quest = RegistryEntryArgumentType.getRegistryEntry(commandContext,"quest", QuestRegistries.QUEST_KEY).value();

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
    }
}
