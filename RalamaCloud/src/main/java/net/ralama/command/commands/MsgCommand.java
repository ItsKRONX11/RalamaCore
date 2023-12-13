package net.ralama.command.commands;

import net.ralama.Ralama;
import net.ralama.command.Command;
import net.ralama.message.Messages;
import net.ralama.packets.api.Constants;
import net.ralama.player.RalamaPlayer;

public class MsgCommand extends Command {
    public static String MSG_PREFIX = "§a§lMSG §7▸ ";
    public MsgCommand() {
        super("msg", "w", "whisper", "tell");
    }

    @Override
    public void execute(RalamaPlayer sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Ralama.PREFIX + "Usage§8: §b/msg <player> <message>");
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
        String message = Constants.joinArgs(args, 1);
        target.sendMessage(MSG_PREFIX + sender.getColoredName() + " §8→ " + target.getRank().getColor() + "You§7: " + message);
        sender.sendMessage(MSG_PREFIX + sender.getRank().getColor() + "You §8→ " + target.getColoredName() + "§7: " + message);
        target.setLastSender(sender);
    }
}
