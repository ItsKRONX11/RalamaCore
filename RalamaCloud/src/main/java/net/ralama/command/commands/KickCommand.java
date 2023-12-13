package net.ralama.command.commands;

import net.ralama.Ralama;
import net.ralama.command.Command;
import net.ralama.message.Messages;
import net.ralama.packets.api.Rank;
import net.ralama.player.RalamaPlayer;

public class KickCommand extends Command {
    public KickCommand() {
        super("kick", Rank.COORDINATOR);
    }

    @Override
    public void execute(RalamaPlayer sender, String[] args) {
        if (!(args.length >= 2)) {
            sender.sendMessage(Ralama.PREFIX + "Usage§8: §b/kick <player> <reason>");
            return;
        }
        RalamaPlayer target = Ralama.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Messages.NEVER_JOINED);
            return;
        }
        if (!target.isOnline()) {
            sender.sendMessage(Messages.NOT_ONLINE);
            return;
        }
        if (target.getRank().equalsIsHigher(sender.getRank()) && !sender.getRank().equalsIsHigher(Rank.OWNER)) {
            sender.sendMessage(Ralama.PREFIX + "§cYou are not allowed to interact with this player.");
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            builder.append(args[i]).append(" ");
        }
        Ralama.getPlayerManager().kick(target, sender, System.currentTimeMillis(), builder.toString());
        sender.sendMessage(Ralama.PREFIX + "§aThe player " + target.getColoredName() + " §awas kicked.");
    }
}
