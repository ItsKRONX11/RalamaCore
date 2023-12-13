package net.ralama.command.commands;

import net.ralama.Ralama;
import net.ralama.command.Command;
import net.ralama.message.Messages;
import net.ralama.packets.api.Rank;
import net.ralama.player.RalamaPlayer;
import net.ralama.server.RalamaServer;

public class SendCommand extends Command {
    public SendCommand() {
        super("send", Rank.COORDINATOR);
    }

    @Override
    public void execute(RalamaPlayer sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Ralama.PREFIX + "Usage§8: §b/send <player> <server>");
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
        RalamaServer server = Ralama.getServerManager().getServerByName(args[1]);
        if (server == null) {
            sender.sendMessage(Ralama.PREFIX + "§cThat server does not exist!");
            return;
        }
        target.connect(server);

    }
}
