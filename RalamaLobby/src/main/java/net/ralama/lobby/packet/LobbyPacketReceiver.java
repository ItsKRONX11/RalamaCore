package net.ralama.lobby.packet;

import io.netty.channel.ChannelHandlerContext;
import net.ralama.lobby.RalamaLobby;
import net.ralama.packets.Packet;
import net.ralama.packets.in.PlayerLobbyLocationPacket;
import net.ralama.packets.out.OutOnlineCountPacket;
import net.ralama.packets.out.OutPlayerRankPacket;
import net.ralama.packets.out.PlayerCoinsPacket;
import net.ralama.packets.out.PlaytimePacket;
import net.ralama.spigot.RalamaSpigot;
import net.ralama.spigot.netty.PacketReceiver;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LobbyPacketReceiver extends PacketReceiver {

    public void receivePacket(Packet rawPacket, ChannelHandlerContext ctx) {
        if (rawPacket instanceof PlayerLobbyLocationPacket) {
            PlayerLobbyLocationPacket packet = (PlayerLobbyLocationPacket) rawPacket;
            String name = packet.getPlayerName();

            RalamaLobby.getInstance().getPlayerLocation().put(name, new Location(Bukkit.getWorld("world"),
                    packet.getX(),
                    packet.getY(),
                    packet.getZ(),
                    packet.getYaw(),
                    packet.getPitch()
            ));
        } else if (rawPacket instanceof OutOnlineCountPacket) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                RalamaLobby.getInstance().getScoreboardManager().updateScoreboard(p.getName());
                p.setLevel(RalamaSpigot.getInstance().getGlobalOnline());
            }
        } else if (rawPacket instanceof PlayerCoinsPacket) {
            PlayerCoinsPacket packet = (PlayerCoinsPacket) rawPacket;

            RalamaLobby.getInstance().getScoreboardManager().updateScoreboard(packet.getName());
        } else if (rawPacket instanceof OutPlayerRankPacket) {
            OutPlayerRankPacket packet = (OutPlayerRankPacket) rawPacket;

            RalamaLobby.getInstance().getScoreboardManager().updateScoreboard(packet.getName());
        } else if (rawPacket instanceof PlaytimePacket) {
            PlaytimePacket packet = (PlaytimePacket) rawPacket;

            RalamaLobby.getInstance().getScoreboardManager().updateScoreboard(packet.getName());
        }
    }
}



