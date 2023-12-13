package net.ralama.command.commands;

import net.ralama.Ralama;
import net.ralama.command.Command;
import net.ralama.message.Messages;
import net.ralama.packets.api.Constants;
import net.ralama.packets.api.Rank;
import net.ralama.player.RalamaPlayer;

public class MuteCommand extends Command {
    public MuteCommand() {
        super("mute", Rank.MANAGER);
    }

    @Override
    public void execute(RalamaPlayer sender, String[] args) {
        if (!(args.length >= 3)) {
            sender.sendMessage(Ralama.PREFIX + "Usage§8: §b/mute <player> <duration> <reason>");
            return;
        }
        RalamaPlayer target = Ralama.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Messages.NEVER_JOINED);
            return;
        }
        if (target.getRank().equalsIsHigher(sender.getRank()) && !sender.getRank().equalsIsHigher(Rank.OWNER)) {
            sender.sendMessage(Ralama.NOT_ALLOWED_INTERACT);
            return;
        }
        if (target.getMute() != null) {
            sender.sendMessage(Ralama.PREFIX, "§cThis player is already muted!", "§cAcest jucator are deja mute!");
            return;
        }
        long expires;
        try {
            expires = Constants.getDuration(args[1]);
        } catch (IllegalArgumentException ignored) {
            sender.sendMessage(Ralama.PREFIX, "§cEnter a valid duration!", "§cIntrodu o durata valida!");
            return;
        }

        final StringBuilder builder = new StringBuilder();
        for (int i = 2; i < args.length; i++) builder.append(args[i]).append(" ");


        Ralama.getPlayerManager().mute(target, sender, System.currentTimeMillis(), expires, builder.toString(), null);

        sender.sendMessage(Ralama.PREFIX, "§aThe player " + target.getColoredName() + " §awas muted.", "§aJucatorul " + target.getColoredName() + " §aa primit mute.");
    }
}
