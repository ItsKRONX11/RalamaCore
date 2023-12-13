package net.ralama.command.commands;

import net.ralama.Ralama;
import net.ralama.command.Command;
import net.ralama.message.Messages;
import net.ralama.packets.api.Rank;
import net.ralama.player.RalamaPlayer;

public class JumpToCommand extends Command {
    public JumpToCommand() {
        super("jumpto", Rank.HELPER, "goto");
    }

    @Override
    public void execute(RalamaPlayer sender, String[] args) {
        if (!(args.length >= 1)) {
            sender.sendMessage(Ralama.PREFIX + "Usage§8: §b/jumpto <player>");
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
        sender.connect(target.getServer());
    }
}
