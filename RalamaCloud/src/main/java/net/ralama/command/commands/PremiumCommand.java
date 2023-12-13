package net.ralama.command.commands;

import net.ralama.Ralama;
import net.ralama.command.Command;
import net.ralama.message.MessageGroup;
import net.ralama.message.Messages;
import net.ralama.packets.api.Rank;
import net.ralama.player.RalamaPlayer;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class PremiumCommand extends Command {

    public PremiumCommand() {
        super("premium");
    }

    @Override
    public void execute(RalamaPlayer sender, String[] args) {
        if (!(args.length >= 1)) {
            MessageGroup group = new MessageGroup();
            group.add("§8» §b/premium on §8- §7Activate the premium login");
            group.add("§8» §b/premium off §8- §7Disable the premium login");
            if (sender.getRank().equalsIsHigher(Rank.MANAGER)) {
                group.add("§8» §b/premium admin §8- §7Manage premium players");
            }
            group.add("§8» §b/premium status §8- §7Display your premium login status");

            sender.sendMessage(group.insert(Ralama.PREFIX + "§b§lPremium §7▸ Usage §8(§b" + group.size() + "§8):"));
            return;
        }

        switch (args[0].toLowerCase()) {
            case "on" -> {
                if (sender.isPremium()) {
                    sender.sendMessage(Ralama.PREFIX + "§cYou have already enabled the premium option");
                    return;
                }
                sender.setPremium(true);
                sender.setAwaitingPremium(true);
                sender.kick("§aYour premium option was enabled!");

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (sender.isAwaitingPremium()) {
                            sender.setPremium(false);
                            sender.setAwaitingPremium(false);
                        }
                    }
                }, TimeUnit.SECONDS.toMillis(30));
            }
            case "off" -> {
                if (!sender.isPremium()) {
                    sender.sendMessage(Ralama.PREFIX + "§cYour premium option is not enabled!");
                    return;
                }
                sender.setPremium(false);
                sender.kick("Your premium option was disabled!");
            }
            case "status" ->
                    sender.sendMessage(Ralama.PREFIX + "Premium status: " + ((sender.isPremium()) ? "§aEnabled" : "§cDisabled"));

            case "admin" -> {
                if (!sender.getRank().equalsIsHigher(Rank.MANAGER)) {
                    sender.sendMessage(Messages.NO_PERMISSION);
                    return;
                }
                if (!(args.length >= 3)) {
                    sender.sendMessage(Ralama.PREFIX + "Usage§8: §b/premium admin <off|on> <player>");
                    return;
                }
                RalamaPlayer target = Ralama.getPlayer(args[2]);
                if (target == null) {
                    sender.sendMessage(Messages.NEVER_JOINED);
                    return;
                }
                switch (args[1].toLowerCase()) {
                    case "on" -> {
                        if (target.isPremium()) {
                            sender.sendMessage(Ralama.PREFIX + "§cThis player already has premium enabled!");
                            return;
                        }
                        target.setPremium(true);
                    }
                    case "off" -> {
                        if (!target.isPremium()) {
                            sender.sendMessage(Ralama.PREFIX + "§cThis player does not have premium enabled!");
                            return;
                        }
                        target.setPremium(false);
                    }
                    default -> {
                        sender.sendMessage(Ralama.PREFIX + "§cUnknown option.");
                        return;
                    }
                }
                target.kick("§6Your premium option was " + (target.isPremium() ? "enabled" : "disabled") + " by staff!");
                sender.sendMessage(Ralama.PREFIX + target.getColoredName() + " §7is now " + (target.isPremium() ? "§bPremium" : "§cCracked") + "§7.");
            }
            default -> sender.sendMessage(Ralama.PREFIX + "§cUnknown sub-command.");
        }
    }
}
