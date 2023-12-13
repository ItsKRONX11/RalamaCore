package net.ralama.command.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.ralama.Ralama;
import net.ralama.command.Command;
import net.ralama.packets.api.Constants;
import net.ralama.packets.api.Rank;
import net.ralama.player.RalamaPlayer;
import net.ralama.server.RalamaProxy;
import net.ralama.server.RalamaServer;

public class ServerCommand extends Command {
    public ServerCommand() {
        super("server", Rank.HELPER, "connect");
    }

    @Override
    public void execute(RalamaPlayer sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Ralama.PREFIX + Ralama.SERVER_PREFIX + "Usage: \n" +
                    Ralama.PREFIX + " §8» §b/server list §8- §7List all servers\n" +
                    Ralama.PREFIX + " §8» §b/server <server> §8- §7- Connect to a server");
            return;
        }
        if (args[0].equalsIgnoreCase("list")) {
            sender.sendMessage(Ralama.SERVER_PREFIX + "Available servers: (§b" + Ralama.getServerManager().getServers().size() + "§7)");

            for (RalamaServer server : Ralama.getServerManager().getServers()) {
                sender.sendMessage(new ComponentBuilder(" §8» §a" + server.getName())
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + server.getName()))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Server ID: " + server.getServerId() +
                                "\nServer name: " + server.getName() +
                                "\nCreated: " + Constants.formatDate(server.getStarted()) +
                                "\nPlayers: " + server.getPlayers().size() +
                                "\nHost: " + server.getHost() + ":" + server.getPort() +
                                "\nServerState: " + server.getState().name()))).create());
            }

            sender.sendMessage(Ralama.SERVER_PREFIX + "Available proxies: (§b" + Ralama.getServerManager().getProxies().size() + "§7)");

            for (RalamaProxy proxy : Ralama.getServerManager().getProxies()) {
                sender.sendMessage(" §8» §a" + proxy.getName());
            }

            return;
        }
        sender.connect(Ralama.getServerManager().getServerByName(args[0]));
    }
}
