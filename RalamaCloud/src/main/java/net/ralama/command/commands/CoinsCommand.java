package net.ralama.command.commands;

import net.ralama.Ralama;
import net.ralama.command.Command;
import net.ralama.message.Messages;
import net.ralama.packets.api.Rank;
import net.ralama.player.RalamaPlayer;

public class CoinsCommand extends Command {
    public CoinsCommand() {
        super("coins", "ralacoins");
    }

    @Override
    public void execute(RalamaPlayer sender, String[] args) {
        if (!sender.getRank().equalsIsHigher(Rank.MANAGER)) {
            sender.sendMessage(Ralama.PREFIX + "Your RalaCoins: §b" + sender.getCoins());
            return;
        }
        if (!(args.length >= 3)) {
            sender.sendMessage(Ralama.PREFIX + Ralama.COINS_PREFIX + "Usage §8(§62§8): \n" +
                    "§8» §6/coins add <player> <coins>\n§8» §6/coins remove <player> <coins>");
            return;
        }
        RalamaPlayer target = Ralama.getPlayer(args[1]);

        if (target == null) {
            sender.sendMessage(Messages.NEVER_JOINED);
            return;
        }
        int coins;

        try {
            coins = Integer.parseInt(args[2]);
        } catch (NumberFormatException nfe) {
            sender.sendMessage(Ralama.PREFIX + "§cEnter a valid number!");
            return;
        }
        if (coins <= 0) {
            sender.sendMessage(Ralama.PREFIX + "§cEnter a positive value!");
            return;
        }

        switch (args[0].toLowerCase()) {
            case "add" -> {
                target.addCoins(coins);
                sender.sendMessage(Ralama.PREFIX + "§b" + coins + " §7were added to " + target.getColoredName() + "§7.");
            }

            case "remove" -> {
                if (coins > target.getCoins()) {
                    coins = target.getCoins();
                }
                target.removeCoins(coins);
            }

            default -> sender.sendMessage(Ralama.UNKNOWN_COMMAND);
        }
    }
}
