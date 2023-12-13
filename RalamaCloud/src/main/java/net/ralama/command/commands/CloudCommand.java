package net.ralama.command.commands;

import net.ralama.Ralama;
import net.ralama.command.Command;
import net.ralama.message.Messages;
import net.ralama.packets.api.Rank;
import net.ralama.player.RalamaPlayer;
import net.ralama.server.RalamaServer;
import net.ralama.server.ServerState;
import net.ralama.server.ServerTemplate;

public class CloudCommand extends Command {
    public CloudCommand() {
        super("cloud", Rank.SRDEVELOPER);
    }

    @Override
    public void execute(RalamaPlayer sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Ralama.PREFIX + Ralama.CLOUD_PREFIX + "Usage§8: §7(§b3§7)" +
                    "\n§8» §b/cloud end §8- §3Stop everything" +
                    "\n§8» §b/cloud kill <server> §8- §3Stop a server" +
                    "\n§8» §b/cloud start <template> §8- §3Start a non dynamic server" +
                    "\n8» §b/cloud add <host:port> §8- §3Add a manual server");
            return;
        }

        switch (args[0].toLowerCase()) {
            case "end" -> {
                if (!sender.getRank().equalsIsHigher(Rank.OWNER)) {
                    sender.sendMessage(Messages.NO_PERMISSION);
                    return;
                }
                Ralama.getInstance().stop();
            }
            case "kill" -> {
                RalamaServer serverToStop = Ralama.getServerManager().getServerByName(args[1]);
                if (serverToStop == null) {
                    sender.sendMessage(Ralama.PREFIX + "§cThat server doesn't exist!");
                    return;
                }
                if (serverToStop.getState() == ServerState.STARTING) {
                    sender.sendMessage(Ralama.PREFIX + "§cThat server is only starting!");
                }
                Ralama.getServerManager().stopServer(serverToStop);
                sender.sendMessage(Ralama.PREFIX + "The server §b" + serverToStop.getName() + " §7is stopping.");
            }
            case "start" -> {
                ServerTemplate template = Ralama.getServerManager().getTemplateByName(args[1]);
                if (template == null) {
                    sender.sendMessage(Ralama.PREFIX + "§cThat template doesn't exist!");
                    return;
                }
                String name = null;
                for (int i = 1; i < template.getMaxServers(); i++) {
                    if (Ralama.getServerManager().getServerByName(template.getName() + "-" + i) != null) continue;
                    name = template.getName() + "-" + i;
                    break;
                }
                if (name == null) {
                    sender.sendMessage(Ralama.PREFIX + "§cThere are too many online servers on that template!");
                    return;
                }
                Ralama.getServerManager().startServer(template, name);
                sender.sendMessage(Ralama.PREFIX + "§aThe server §b" + name + " §ais now starting.");
            }
            case "add" -> {
                if (args.length < 3) {
                    sender.sendMessage(Ralama.PREFIX + "Usage§8: §b/cloud add <host:port> <name>");
                    return;
                }
                if (!args[1].contains(":")) return;

                String[] hostPort = args[1].split(":");
                String host = hostPort[0];
                int port;
                try {
                    port = Integer.parseInt(hostPort[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(Ralama.PREFIX + "§cEnter a valid port!");
                    return;
                }
                Ralama.getServerManager().add(new RalamaServer(args[2], host, port, "ManualServer"));
            }
        }
    }
}
