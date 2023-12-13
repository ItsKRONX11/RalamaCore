package net.ralama.command.commands;

import net.ralama.Ralama;
import net.ralama.command.Command;
import net.ralama.player.RalamaPlayer;

public class RegisterCommand extends Command {
    public RegisterCommand() {
        super("register", "reg");
    }

    @Override
    public void execute(RalamaPlayer sender, String[] args) {

        if (sender.isPremium()) {
            sender.sendMessage(Ralama.PREFIX + "§cYou have a premium account!");
            return;
        }

        if (sender.getPassword() != null) {
            sender.sendMessage(Ralama.PREFIX + "§cYou are already registered!");
            return;
        }

        if (!(args.length >= 2)) {
            sender.sendMessage(Ralama.PREFIX + "Usage§8: §b/register <password> <password>");
            return;
        }

        if (!(args[0].equals(args[1]))) {
            sender.sendMessage(Ralama.PREFIX + "§cPasswords don't match!");
            return;
        }

        if (args[0].length() > 24) {
            sender.sendMessage(Ralama.PREFIX + "§cYour password is too long!");
            return;
        }

        if (args[0].length() < 6) {
            sender.sendMessage(Ralama.PREFIX + "§cYour password is too short!");
            return;
        }

        sender.setPassword(args[0]);
        sender.setLoggedIn(true);
        sender.sendMessage(Ralama.PREFIX + "§aYou have registered your account!");
    }
}
