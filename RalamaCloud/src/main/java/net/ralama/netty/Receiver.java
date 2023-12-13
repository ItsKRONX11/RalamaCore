package net.ralama.netty;

import io.netty.channel.ChannelHandlerContext;
import net.ralama.Ralama;
import net.ralama.RalamaCloud;
import net.ralama.command.Command;
import net.ralama.message.ChatMessage;
import net.ralama.packets.Packet;
import net.ralama.packets.api.Language;
import net.ralama.packets.api.Rank;
import net.ralama.packets.base.GetterPacket;
import net.ralama.packets.in.*;
import net.ralama.packets.in.get.GetLobbyPacket;
import net.ralama.packets.in.get.GetOnlineCountPacket;
import net.ralama.packets.in.get.GetPlayerCoinsPacket;
import net.ralama.packets.in.get.GetPlayerRankPacket;
import net.ralama.packets.out.*;
import net.ralama.player.RalamaPlayer;
import net.ralama.server.RalamaProxy;
import net.ralama.server.RalamaServer;
import net.ralama.server.ServerState;
import org.apache.commons.io.FileUtils;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class Receiver {
    private Receiver() {
    }

    public static void receive(Packet raw, ChannelHandlerContext ctx) {
        System.out.println(raw.getClass().getName());

        if (raw instanceof GetterPacket) {
            receive((GetterPacket) raw, ctx);
            return;
        }

        if (raw instanceof CommandPacket packet) {
            RalamaPlayer sender = Ralama.getPlayer(packet.getName());

            if (sender == null) {
                Ralama.getLogger().info("Player " + packet.getName() + " not found on command packet.");
                return;
            }

            Command command = Ralama.getCommandManager().getCommand(packet.getCommand());

            if (command == null) {
                Ralama.getLogger().info("Command " + packet.getCommand() + " not found on command packet");
                return;
            }

            Ralama.getCommandManager().dispatchCommand(command, sender, packet.getArgs());
        } else if (raw instanceof InProxyStartPacket packet) {
            String name = packet.getName();

            RalamaProxy proxy = new RalamaProxy(name, packet.getPort());
            proxy.setChannel(ctx.channel());
            Ralama.getServerManager().add(proxy);

            Map<String, InetSocketAddress> servers = new HashMap<>();
            for (RalamaServer server : Ralama.getServerManager().getServers()) {
                servers.put(server.getName(), new InetSocketAddress(server.getHost(), server.getPort()));
            }

            Ralama.getLogger().info("The proxy " + packet.getName() + " has been added. Host: " + packet.getHost() + " Port: " + packet.getPort());
            ctx.writeAndFlush(new InitialProxyInformationPacket(servers, RalamaCloud.MOTD, Ralama.getPlayerManager().getOnlinePlayers().size(), Ralama.getFilterManager().getEntries(), Ralama.getCommandManager().getCommands()).serialize());
        } else if (raw instanceof PlayerConnectPacket packet) {
            RalamaPlayer player = Ralama.getPlayer(packet.getName());
            RalamaServer server = Ralama.getInstance().getServerManager().getServerByName(packet.getServer());
            if (player == null || server == null) return;

            player.switchServer(server);
        } else if (raw instanceof InServerStopPacket packet) {
            RalamaServer server = Ralama.getServerManager().getServerByName(packet.getServerName());

            if (server == null) return;

            Ralama.getLogger().info("Received stop packet for " + server.getName());

            Ralama.getServerManager().sendProxiesPacket(packet);

            Ralama.getServerManager().remove(server);

            server.getChannel().close();

            FileUtils.deleteQuietly(Ralama.getInstance().getFileManager().get("servers/" + server.getServerId()));
        } else if (raw instanceof PlayerQuitPacket packet) {
            RalamaPlayer player = Ralama.getPlayer(packet.getName());

            if (player == null) return;

            System.out.println(player.getName() + "has left from proxy " + player.getProxy().getName() + ", server " + player.getServer());

            player.setLoggedIn(false);

            player.addPlayTime(System.currentTimeMillis() - player.getLastJoined());

            player.getProxy().getPlayers().remove(player.getName());
            if (player.getServer() != null)
                player.getServer().getPlayers().remove(player.getName());

            player.setServer(null);
            player.setProxy(null);

            Ralama.getPlayerManager().getOnlinePlayers().remove(player.getName());
            Ralama.getServerManager().sendAllPacket(new OutOnlineCountPacket(Ralama.getPlayerManager().getOnlinePlayers().size()));

            if (player.isStaff()) {
                Ralama.getPlayerManager().sendStaffMessage(Ralama.STAFF_PREFIX + player.getColoredName() + " §cis now offline.");
            }
        } else if (raw instanceof PlayerJoinPacket packet) {
            RalamaPlayer player = Ralama.getPlayer(packet.getName());
            RalamaProxy proxy = Ralama.getServerManager().getProxyByName(packet.getProxy());

            if (player == null) return;
            if (proxy == null) return;

            if (player.isOnline()) {
                Ralama.getLogger().warn("Player " + player.getName() + " tried to login twice! Already online on proxy " + player.getProxy().getName() + " and tried to connect on " + packet.getProxy());
                return;
            }
            long now = System.currentTimeMillis();
            player.setIp(packet.getIp());
            player.setLastJoined(now);
            player.setProxy(proxy);

            proxy.getPlayers().add(player.getName());

            if (player.isPremium()) {
                player.setLoggedIn(true);
            } else {
                AtomicInteger i = new AtomicInteger(0);
                new Timer().scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        i.set(i.get() + 1);
                        if (i.get() > 6) {
                            player.kick("You took too long to log in!");
                            cancel();
                        }
                        if (!player.isLoggedIn() && player.isOnline() && player.getLastJoined() == now) {
                            if (player.getPassword() == null) {
                                player.sendMessage(Ralama.PREFIX + "Register using §b/register <password> <password>§7.");
                            } else {
                                player.sendMessage(Ralama.PREFIX + "Please login using §b§l/login <password>§7.");
                            }
                        } else {
                            cancel();
                        }
                    }
                }, TimeUnit.SECONDS.toMillis(1), TimeUnit.SECONDS.toMillis(5));
            }
            if (player.isAwaitingPremium()) {
                player.setAwaitingPremium(false);
            }

            Ralama.getPlayerManager().getOnlinePlayers().add(player.getName());
            Ralama.getServerManager().sendAllPacket(new OutOnlineCountPacket(Ralama.getPlayerManager().getOnlinePlayers().size()));

            if (player.isStaff()) {
                Ralama.getPlayerManager().sendStaffMessage(Ralama.STAFF_PREFIX + player.getColoredName() + " §ais now online.");
            }
            System.out.println(player.getName() + " has joined on proxy " + proxy.getName());
        } else if (raw instanceof PlayerMessagePacket packet) {
            RalamaPlayer player = Ralama.getPlayer(packet.getName());
            String message = packet.getMessage();

            if (player == null) return;
            if (!player.isOnline()) return;

            player.getMessages().add(new ChatMessage(player.getName(), message, player.getServer().getName(), System.currentTimeMillis()));
            for (String entry : Ralama.getFilterManager().getEntries()) {
                if (message.toLowerCase().contains(entry)) {
                    Ralama.getCommandManager().dispatchCommand(
                            Ralama.getCommandManager().getCommand("punish"),
                            Ralama.getPlayer("CloudAdmin"), // system account
                            new String[]{player.getName(), "16", message});

                    break;
                }
            }
        } else if (raw instanceof InServerStartPacket packet) {
            RalamaServer server = Ralama.getServerManager().getServerByPort(packet.getPort());
            if (server == null) return;

            Ralama.getLogger().info("Received start packet for server " + server.getName());
            server.setChannel(ctx.channel());
            if (server.isLobby()) {
                server.setState(ServerState.LOBBY);
            } else {
                server.setState(ServerState.STATIC);
            }

            server.sendPacket(new OutServerInfoPacket(server.getName(), server.getHost(), server.getPort(), server.getServerId()));

        } else if (raw instanceof PlayerLobbyLocationPacket packet) {
            RalamaPlayer player = Ralama.getPlayerManager().getPlayerByName(packet.getPlayerName());

            if (player == null) return;

            player.setX(packet.getX());
            player.setY(packet.getY());
            player.setZ(packet.getZ());
            player.setYaw(packet.getYaw());
            player.setPitch(packet.getPitch());
        } else if (raw instanceof ProxyStopPacket packet) {
            RalamaProxy proxy = Ralama.getServerManager().getProxyByName(packet.getName());
            if (proxy == null) return;

            Ralama.getServerManager().remove(proxy);
        } else if (raw instanceof PlayerSendPacket packet) {
            RalamaPlayer player = Ralama.getPlayer(packet.getPlayerName());
            RalamaServer server = Ralama.getServerManager().getServerByName(packet.getServerName());

            if (player == null || server == null) return;

            if (player.isOnline()) {
                player.connect(server);
            }
        } else if (raw instanceof PlayerLanguagePacket packet) {
            RalamaPlayer player = Ralama.getPlayer(packet.getName());

            if (player == null) return;

            player.setLanguage(packet.getLanguage());
        }
    }

    public static void receive(GetterPacket raw, ChannelHandlerContext ctx) {
        if (raw instanceof PlayerPreJoinInfoPacket packet) {
            RalamaPlayer player = Ralama.getPlayer(packet.getName());

            if (player == null) {
                long now = System.currentTimeMillis();
                player = new RalamaPlayer(
                        now,
                        now,
                        Rank.PLAYER,
                        Rank.PLAYER,
                        0,
                        0,
                        packet.getName(),
                        UUID.nameUUIDFromBytes(("OfflinePlayer:" + packet.getName()).getBytes()),
                        null,
                        null,
                        0,
                        0,
                        0,
                        packet.getIp(),
                        null,
                        false,
                        "",
                        0,
                        false,
                        0,
                        0,
                        0,
                        -361,
                        0,
                        Language.ENGLISH
                );
                player.updateToDatabase(true);
            }

            ctx.writeAndFlush(new PlayerPreJoinInfoPacket.Response(
                    packet.getId(), player.getUuid(), player.getName(), player.isOnline(), player.isPremium(), player.getMute() != null ? player.getMute().toSaveArray() : null, player.getBan() != null ? player.getBan().toSaveArray() : null, player.getLanguage()
            ).serialize());
        } else if (raw instanceof GetLobbyPacket packet) {
            RalamaServer server = Ralama.getServerManager().getBestLobby(Ralama.getPlayer(packet.getPlayerName()));
            String name = null;
            if (server != null) {
                name = server.getName();
            }
            ctx.writeAndFlush(new GetLobbyPacket.Response(packet.getId(), name).serialize());

        } else if (raw instanceof GetOnlineCountPacket packet) {
            ctx.writeAndFlush(new GetOnlineCountPacket.Response(packet.getId(), Ralama.getPlayerManager().getOnlinePlayers().size()).serialize());

        } else if (raw instanceof GetPlayerCoinsPacket packet) {
            int coins = 0;

            RalamaPlayer player = Ralama.getPlayer(packet.getName());
            if (player != null) {
                coins = player.getCoins();
            }
            ctx.writeAndFlush(new GetPlayerCoinsPacket.Response(packet.getId(), coins).serialize());

        } else if (raw instanceof GetPlayerRankPacket packet) {
            RalamaPlayer player = Ralama.getPlayer(packet.getName());

            if (player == null) return;

            ctx.writeAndFlush(new GetPlayerRankPacket.Response(packet.getId(), player.getRank().name()).serialize());
        }
    }
}

