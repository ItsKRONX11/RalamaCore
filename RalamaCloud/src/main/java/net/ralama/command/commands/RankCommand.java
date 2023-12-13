package net.ralama.command.commands;

import net.ralama.Ralama;
import net.ralama.command.Command;
import net.ralama.message.Messages;
import net.ralama.packets.api.Constants;
import net.ralama.packets.api.Rank;
import net.ralama.player.RalamaPlayer;

public class RankCommand extends Command {
    public RankCommand() {
        super("rank", Rank.MANAGER);
    }

    @Override
    public void execute(RalamaPlayer sender, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            StringBuilder builder = new StringBuilder();
            for (Rank rank : Rank.values()) {
                builder.append("\n§8» " + rank.getDisplayName() + " §7[§e" + rank.getAccessLevel() + "§8, " + rank.getColor() + rank.getShortName() + "§7]");
            }
            sender.sendMessage(builder.insert(0, Ralama.PREFIX + "Ranks §8(§a" + Rank.values().length + "§8): ").toString());
            return;
        }
        if (!(args.length >= 2)) {
            sender.sendMessage(Ralama.PREFIX + "Usage§8: §b/rank <player> <rank> [duration]");
            return;
        }
        RalamaPlayer target = Ralama.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Messages.NEVER_JOINED);
            return;
        }
        if (target.getRank().equalsIsHigher(sender.getRank()) && !(sender.getRank().equalsIsHigher(Rank.OWNER))) {
            sender.sendMessage(Ralama.PREFIX + "§cYou are not allowed to interact with this player!");
            return;
        }
        Rank rank;
        try {
            rank = Rank.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Ralama.PREFIX + "§cThis rank does not exist!");
            return;
        }
        if (rank.isHigher(sender.getRank())) {
            sender.sendMessage(Ralama.PREFIX + "§cYou are not allowed to use this rank!");
            return;
        }

        if (args.length >= 3) {
            long ends;
            try {
                ends = Constants.getDuration(args[2]);
            } catch (IllegalArgumentException e) {
                sender.sendMessage(Ralama.PREFIX + "§cEnter a valid duration!");
                return;
            }
            if (rank.equals(Rank.PLAYER)) {
                sender.sendMessage(Ralama.PREFIX + "§cMember rank cannot be scheduled to expire!");
                return;
            }
            target.setFallbackRank(target.getRank());
            target.setRankEnd(ends);
        }

        target.setRank(rank);
        sender.sendMessage(Ralama.PREFIX + target.getColoredName() + "'s §7rank was changed to " + rank.getDisplayName() + "§7.");
    }
}
