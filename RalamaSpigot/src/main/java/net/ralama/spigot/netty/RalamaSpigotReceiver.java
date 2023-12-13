package net.ralama.spigot.netty;

import io.netty.channel.ChannelHandlerContext;
import net.ralama.packets.Packet;
import net.ralama.packets.api.Rank;
import net.ralama.packets.base.ResponsePacket;
import net.ralama.packets.out.*;
import net.ralama.spigot.RalamaSpigot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RalamaSpigotReceiver extends PacketReceiver {
    public void receivePacket(Packet rawPacket, ChannelHandlerContext ctx) {

        if (rawPacket instanceof ResponsePacket) {
            ResponsePacket packet = (ResponsePacket) rawPacket;
            RalamaSpigot.getInstance().getSentPackets().get(packet.getTargetId()).complete((ResponsePacket) rawPacket);
            RalamaSpigot.getInstance().getSentPackets().remove(packet.getTargetId());
        }

        if (rawPacket instanceof OutServerInfoPacket) {
            OutServerInfoPacket packet = (OutServerInfoPacket) rawPacket;

            RalamaSpigot.getInstance().setServerName(packet.getServerName());
            RalamaSpigot.getInstance().setServerId(packet.getServerId());
            RalamaSpigot.getInstance().setHost(packet.getHost());
        } else if (rawPacket instanceof OutKillRequestPacket) {
            Bukkit.getServer().shutdown();
        } else if (rawPacket instanceof OutOnlineCountPacket) {
            RalamaSpigot.getInstance().setGlobalOnline(((OutOnlineCountPacket) rawPacket).getOnline());

        } else if (rawPacket instanceof OutPlayerRankPacket) {
            OutPlayerRankPacket packet = (OutPlayerRankPacket) rawPacket;
            Rank rank = Rank.valueOf(packet.getRank());
            String name = packet.getName();
            RalamaSpigot.getInstance().getPlayer(name).setRank(rank);

            if (rank.equalsIsHigher(Rank.MANAGER)) {
                Player player = Bukkit.getPlayer(name);
                if (player != null) Bukkit.getScheduler().runTask(RalamaSpigot.getInstance(), () -> player.setOp(true));
            }
        } else if (rawPacket instanceof PlayerCoinsPacket) {
            PlayerCoinsPacket packet = (PlayerCoinsPacket) rawPacket;

            RalamaSpigot.getInstance().getPlayer(packet.getName()).setCoins(packet.getCoins());
        } else if (rawPacket instanceof PlayerAuthPacket) {
            PlayerAuthPacket packet = (PlayerAuthPacket) rawPacket;

            RalamaSpigot.getInstance().getPlayer(packet.getPlayerName()).setLoggedIn(packet.isLoggedIn());
        } else if (rawPacket instanceof PlaytimePacket) {
            PlaytimePacket packet = (PlaytimePacket) rawPacket;
            RalamaSpigot.getInstance().getPlayer(packet.getName()).setPlaytime(packet.getPlaytime());
        }
    }
}
