package net.ralama.command.commands;

import net.ralama.Ralama;
import net.ralama.command.Command;
import net.ralama.message.Messages;
import net.ralama.packets.api.Rank;
import net.ralama.player.RalamaPlayer;

public class RemovePointsCommand extends Command {
    public RemovePointsCommand() {
        super("removepoints", Rank.MANAGER);
    }

    @Override
    public void execute(RalamaPlayer sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(Ralama.PREFIX + "Usage§8: §b/removepoints <player> <ban|mute> <points>");
            return;
        }
        RalamaPlayer target = Ralama.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Messages.NEVER_JOINED);
            return;
        }
        int points;

        try {
            points = Integer.parseInt(args[2]);

            if (points <= 0) {
                sender.sendMessage(Ralama.PREFIX + "§cPoints must be a positive number!");
                return;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(Ralama.PREFIX + "§cEnter a valid number!");
            return;
        }

        switch (args[1].toLowerCase()) {
            case "ban" -> {
                if (points > target.getBanPoints()) {
                    sender.sendMessage(Ralama.PREFIX + "§cThis player has less ban points!");
                    return;
                }
                target.removeBanPoints(points);
                sender.sendMessage(Ralama.PREFIX + "Removed §b" + points + " §7ban points from " + target.getColoredName() + "§7.");
            }
            case "mute" -> {
                if (points > target.getMutePoints()) {
                    sender.sendMessage(Ralama.PREFIX + "§cThis player has less mute points!");
                    return;
                }
                target.removeMutePoints(points);
                sender.sendMessage(Ralama.PREFIX + "Removed §b" + points + " §7mute points from " + target.getColoredName() + "§7.");
            }
            default -> sender.sendMessage(Ralama.UNKNOWN_COMMAND);
        }
    }
}
