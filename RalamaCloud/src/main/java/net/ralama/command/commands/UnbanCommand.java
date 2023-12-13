package net.ralama.command.commands;

import net.ralama.Ralama;
import net.ralama.command.Command;
import net.ralama.message.Messages;
import net.ralama.packets.api.Rank;
import net.ralama.player.RalamaPlayer;

public class UnbanCommand extends Command {
    public UnbanCommand() {
        super("unban", Rank.COORDINATOR);
    }

    @Override
    public void execute(RalamaPlayer sender, String[] args) {
        if (!(args.length >= 1)) {
            sender.sendMessage(Ralama.PREFIX + "Usage§8: §b/unban <player> [reason]");
            return;
        }
        RalamaPlayer target = Ralama.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Messages.NEVER_JOINED);
            return;
        }
        if (target.getBan() == null) {
            sender.sendMessage(Ralama.PREFIX + "§cThis player is not banned!");
            return;
        }
        String reason = "MANUAL";
        if (args.length >= 2) {
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i < args.length; i++) builder.append(args[i]).append(" ");
            reason = builder.toString();
        }
        Ralama.getPlayerManager().unban(target, sender, System.currentTimeMillis(), reason);
        sender.sendMessage(Ralama.PREFIX + "§aThe player " + target.getColoredName() + " §awas unbanned.");
    }
}
