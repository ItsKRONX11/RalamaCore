package net.ralama.command;

import com.google.common.collect.ImmutableList;
import net.ralama.Ralama;
import net.ralama.command.commands.*;
import net.ralama.message.Messages;
import net.ralama.player.RalamaPlayer;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;

public class CommandManager {
    private final HashMap<String, Command> commandMap = new HashMap<>();

    public CommandManager() {
        this.registerCommand(OnlineCommand.class);
        this.registerCommand(CloudCommand.class);
        this.registerCommand(ServerCommand.class);
        this.registerCommand(LoginCommand.class);
        this.registerCommand(BanCommand.class);
        this.registerCommand(UnbanCommand.class);
        this.registerCommand(MuteCommand.class);
        this.registerCommand(UnmuteCommand.class);
        this.registerCommand(KickCommand.class);
        this.registerCommand(RankCommand.class);
        this.registerCommand(WarnCommand.class);
        this.registerCommand(PunishHistoryCommand.class);
        this.registerCommand(JumpToCommand.class);
        this.registerCommand(CoinsCommand.class);
        this.registerCommand(PremiumCommand.class);
        this.registerCommand(NotifyCommand.class);
        this.registerCommand(CloudStatsCommand.class);
        this.registerCommand(StaffChatCommand.class);
        this.registerCommand(PlaytimeCommand.class);
        this.registerCommand(StaffCommand.class);
        this.registerCommand(RegisterCommand.class);
        this.registerCommand(InfoCommand.class);
        this.registerCommand(FilterCommand.class);
        this.registerCommand(SendCommand.class);
        this.registerCommand(ListCommand.class);
        this.registerCommand(RemovePointsCommand.class);
        this.registerCommand(ChatlogCommand.class);
        this.registerCommand(MsgCommand.class);
        this.registerCommand(RCommand.class);
        this.registerCommand(WhereAmICommand.class);
        this.registerCommand(PartyCommand.class);
    }

    public Command getCommand(String name) {
        return this.commandMap.get(name.toLowerCase());
    }

    public void registerCommand(Command command) {
        this.commandMap.put(command.getName().toLowerCase(), command);

        for (String alias : command.getAliases()) {
            this.commandMap.put(alias.toLowerCase(), command);
        }
    }
    public void registerCommand(Class<? extends Command> commandClass) {
        try {
            this.registerCommand(commandClass.getConstructor().newInstance());
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException exc) {
            System.out.println("Failed to register command " + commandClass.getName() + exc.getClass().getName() + ": " + exc.getMessage());
        }
    }

    public void dispatchCommand(RalamaPlayer executor, String toExecute) {
        String[] commandArgs = toExecute.split(" ");

        if (commandArgs.length == 0) {
            Ralama.getLogger().info("Tried to execute command with no command line");
            return;
        }

        String name = commandArgs[0].toLowerCase();

        if (!commandMap.containsKey(name)) {
            Ralama.getLogger().info("Tried to execute command that does not exist: " + name);
            return;
        }

        dispatchCommand(commandMap.get(name), executor, Arrays.copyOfRange(commandArgs, 1, commandArgs.length));
    }

    public void dispatchCommand(Command command, RalamaPlayer sender, String[] args) {
        if (!sender.getRank().equalsIsHigher(command.getRank())) {
            sender.sendMessage(Messages.NO_PERMISSION);
            return;
        }
        try {
            command.execute(sender, args);
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(Ralama.ERROR);
        }
    }

    public ImmutableList<String> getCommands() {
        return ImmutableList.copyOf(this.commandMap.keySet());
    }

}
