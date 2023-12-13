package net.ralama.ralamaproxy.netty;

import io.netty.channel.ChannelHandlerContext;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.chat.ComponentSerializer;
import net.ralama.packets.Packet;
import net.ralama.packets.api.Constants;
import net.ralama.packets.api.Language;
import net.ralama.packets.base.ResponsePacket;
import net.ralama.packets.in.InServerStopPacket;
import net.ralama.packets.in.PlayerLanguagePacket;
import net.ralama.packets.out.*;
import net.ralama.ralamaproxy.RalamaProxy;

import java.net.InetSocketAddress;
import java.util.Map;

public class PacketReceiver {
    public static void receivePacket(Packet raw, ChannelHandlerContext ctx) {

        if (raw instanceof ResponsePacket) {
            ResponsePacket packet = (ResponsePacket) raw;
            RalamaProxy.getInstance().getSentPackets().get(packet.getTargetId()).complete(packet);
            RalamaProxy.getInstance().getSentPackets().remove(packet.getTargetId());
            return;
        }

        if (raw instanceof PlayerMessagePacket) {
            PlayerMessagePacket packet = (PlayerMessagePacket) raw;

            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packet.getName());

            if (player == null) return;

            player.sendMessage(packet.getMessage());
        } else if (raw instanceof PlayerKickPacket) {
            PlayerKickPacket packet = (PlayerKickPacket) raw;

            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packet.getName());

            if (player == null) return;

            player.disconnect(packet.getReason());
        } else if (raw instanceof PlayerConnectPacket) {
            PlayerConnectPacket packet = (PlayerConnectPacket) raw;

            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packet.getName());
            ServerInfo server = ProxyServer.getInstance().getServerInfo(packet.getServer());

            if (player == null || server == null) return;

            player.connect(server);
        } else if (raw instanceof MessageComponentPacket) {
            MessageComponentPacket packet = (MessageComponentPacket) raw;

            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packet.getName());
            if (player == null) {
                for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                    p.sendMessage(ComponentSerializer.parse(packet.getJson()));
                }
                return;
            }

            player.sendMessage(ComponentSerializer.parse(packet.getJson()));
        } else if (raw instanceof ServerAddPacket) {
            ServerAddPacket packet = (ServerAddPacket) raw;

            if (ProxyServer.getInstance().getServerInfo(packet.getName()) != null) return;

            ServerInfo info = ProxyServer.getInstance().constructServerInfo(packet.getName(), new InetSocketAddress(packet.getHost(), packet.getPort()), "-", false);
            ProxyServer.getInstance().getServers().put(packet.getName(), info);
            System.out.println("[RalamaCloud] Added server: " + packet.getName());

        } else if (raw instanceof OutOnlineCountPacket) {
            RalamaProxy.getInstance().setOnlinePlayers(((OutOnlineCountPacket) raw).getOnline());
        } else if (raw instanceof InitialProxyInformationPacket) {
            InitialProxyInformationPacket packet = (InitialProxyInformationPacket) raw;

            RalamaProxy.getInstance().setOnlinePlayers(packet.getOnline());
            RalamaProxy.getInstance().setMotd(packet.getMotd());
            RalamaProxy.getInstance().getFilterWords().addAll(packet.getFilterEntries());

            for (Map.Entry<String, InetSocketAddress> entry : packet.getServers().entrySet()) {
                ProxyServer.getInstance().getServers().put(entry.getKey(), ProxyServer.getInstance().constructServerInfo(entry.getKey(), entry.getValue(), "-", false));
                System.out.println("[RalamaCloud] Added server: " + entry.getKey());
            }
            for (String command : packet.getCommands()) {
                RalamaProxy.getInstance().register(command);
            }
        } else if (raw instanceof PlayerMutePacket) {
            PlayerMutePacket packet = (PlayerMutePacket) raw;

            RalamaProxy.getInstance().getPlayer(packet.getName()).setMuteData(packet.getMuteData());
        } else if (raw instanceof FilterPacket) {
            FilterPacket packet = (FilterPacket) raw;

            RalamaProxy.getInstance().getFilterWords().clear();
            RalamaProxy.getInstance().getFilterWords().addAll(packet.getEntries());
        } else if (raw instanceof PlayerAuthPacket) {
            PlayerAuthPacket packet = (PlayerAuthPacket) raw;

            RalamaProxy.getInstance().getPlayer(packet.getPlayerName()).setLoggedIn(packet.isLoggedIn());
        } else if (raw instanceof InServerStopPacket) {
            InServerStopPacket packet = (InServerStopPacket) raw;

            System.out.println("[RalamaCloud] Removed server " + packet.getServerName());

            ProxyServer.getInstance().getServers().remove(packet.getServerName());
        } else if (raw instanceof OutKillRequestPacket) {
            OutKillRequestPacket packet = (OutKillRequestPacket) raw;
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                Language language = RalamaProxy.getInstance().getPlayer(player).getLanguage();
                if (packet.isGlobal()) {
                    if (language == Language.ROMANIAN) {
                        player.disconnect(Constants.KICK_PREFIX + "\n\n§cRalama se inchide.\n§cIntra pe discord.ralama.net pentru informatii.");
                    } else {
                        player.disconnect(Constants.KICK_PREFIX + "\n\n§cThe network is shutting down.\n§cJoin discord.ralama.net for information.");
                    }
                } else {
                    player.disconnect(Constants.KICK_PREFIX + "§cThe proxy server you were on is restarting.");
                }
            }
            ProxyServer.getInstance().stop();
        } else if (raw instanceof PlayerLanguagePacket) {
            PlayerLanguagePacket languagePacket = (PlayerLanguagePacket) raw;

            RalamaProxy.getInstance().getPlayer(languagePacket.getName()).setLanguage(languagePacket.getLanguage());

            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(languagePacket.getName());

            if (player != null) {
                RalamaProxy.updateTab(player, player.getServer().getInfo().getName(), languagePacket.getLanguage());
            }
        }
    }
}
