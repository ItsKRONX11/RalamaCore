package net.ralama.command.commands;

import net.ralama.Ralama;
import net.ralama.command.Command;
import net.ralama.message.Messages;
import net.ralama.packets.api.Rank;
import net.ralama.player.RalamaPlayer;

public class LoginCommand extends Command {
    public LoginCommand() {
        super("login", "l");
    }

    @Override
    public void execute(RalamaPlayer sender, String[] args) {

        if (sender.isLoggedIn() && sender.getRank().equalsIsHigher(Rank.OWNER) && args.length >= 1 && args[0].equalsIgnoreCase("reset")) {
            if (args.length == 1) {
                sender.sendMessage(Ralama.PREFIX + "Usage§8: §b/login reset <player>");
                return;
            }
            RalamaPlayer target = Ralama.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(Messages.NEVER_JOINED);
                return;
            }
            target.setPassword(null);
            sender.sendMessage(Ralama.PREFIX + "The password of " + target.getColoredName() + " §7was reset.");
        }

        if (sender.isPremium()) {
            sender.sendMessage(Ralama.PREFIX + "§cYou have a premium account!");
            return;
        }

        if (sender.getPassword() == null) {
            sender.sendMessage(Ralama.PREFIX + "§cYou are not registered yet!");
            return;
        }

        if (sender.isLoggedIn()) {
            sender.sendMessage(Ralama.PREFIX + "§cYou are already logged in!");
            return;
        }

        if (!(args.length >= 1)) {
            sender.sendMessage(Ralama.PREFIX + "Usage§8: §b/login <password>");
            return;
        }

        if (!args[0].equalsIgnoreCase(sender.getPassword())) {
            sender.kick("§cWrong password!");
            return;
        }

        sender.setLoggedIn(true);
        sender.sendMessage(Ralama.PREFIX + "§aYou have logged in.");
    }
}
