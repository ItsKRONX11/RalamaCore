package net.ralama.command.commands;

import net.ralama.Ralama;
import net.ralama.command.Command;
import net.ralama.message.Messages;
import net.ralama.packets.api.Rank;
import net.ralama.player.RalamaPlayer;

public class UnmuteCommand extends Command {
    public UnmuteCommand() {
        super("unmute", Rank.COORDINATOR);
    }

    @Override
    public void execute(RalamaPlayer sender, String[] args) {
        if (!(args.length >= 1)) {
            sender.sendMessage(Ralama.PREFIX + "Usage§8: §b/unmute <player> [reason]");
            return;
        }
        RalamaPlayer target = Ralama.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Messages.NEVER_JOINED);
            return;
        }
        if (target.getMute() == null) {
            sender.sendMessage(Ralama.PREFIX + "§cThis player is not muted!");
            return;
        }
        String reason = "MANUAL";
        if (args.length >= 2) {
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i < args.length; i++) builder.append(args[i]).append(" ");
            reason = builder.toString();
        }
        Ralama.getPlayerManager().unmute(target, sender, System.currentTimeMillis(), reason);
        sender.sendMessage(Ralama.PREFIX + "§aThe player " + target.getColoredName() + " §awas unmuted.");
    }
}
