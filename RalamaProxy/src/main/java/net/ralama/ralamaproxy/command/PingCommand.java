package net.ralama.ralamaproxy.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.ralama.packets.api.Constants;
import net.ralama.packets.api.Message;
import net.ralama.ralamaproxy.RalamaProxy;

public class PingCommand extends Command {
    private static final Message MESSAGE = new Message(
            Constants.PREFIX + "Your ping: §b%ping%ms",
            Constants.PREFIX + "Ping-ul tau: §b%ping%ms");

    public PingCommand() {
        super("ping", null, "latency");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (!(commandSender instanceof ProxiedPlayer)) return;

        commandSender.sendMessage(MESSAGE
                .replaced("%ping%",
                        Integer.toString(((ProxiedPlayer) commandSender).getPing()))
                .toString(RalamaProxy.getInstance().getPlayer(commandSender.getName()).getLanguage()));
    }
}
