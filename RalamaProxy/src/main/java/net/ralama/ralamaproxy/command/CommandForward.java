package net.ralama.ralamaproxy.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.ralama.packets.in.CommandPacket;
import net.ralama.ralamaproxy.RalamaProxy;

public class CommandForward extends Command {
    public CommandForward(String name) {
        super(name);
    }

    public CommandForward(String name, String... aliases) {
        super(name, null, aliases);
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (!(commandSender instanceof ProxiedPlayer)) return;
        RalamaProxy.getInstance().getChannel().writeAndFlush(new CommandPacket(getName(), strings, commandSender.getName()).serialize());
    }
}
