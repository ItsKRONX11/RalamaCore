package net.ralama.spigot.listeners;

import net.ralama.packets.api.Rank;
import net.ralama.packets.in.get.GetPlayerCoinsPacket;
import net.ralama.packets.in.get.GetPlayerRankPacket;
import net.ralama.spigot.RalamaSpigot;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class JoinQuitListener implements Listener {
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        RalamaSpigot.getInstance().getNamePlayer().remove(e.getPlayer().getName());
        e.setQuitMessage(null);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.setJoinMessage(null);
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(RalamaSpigot.getInstance(), () -> {
            String name = e.getPlayer().getName();
            GetPlayerRankPacket.Response rankResponse = (GetPlayerRankPacket.Response) RalamaSpigot.getInstance().sendGetterPacket(new GetPlayerRankPacket(name));
            GetPlayerCoinsPacket.Response coinsResponse = (GetPlayerCoinsPacket.Response) RalamaSpigot.getInstance().sendGetterPacket(new GetPlayerCoinsPacket(name));

            Rank rank = Rank.PLAYER;
            try {
                rank = Rank.valueOf(rankResponse.getRankName());
            } catch (IllegalArgumentException ignored) {
            }

            RalamaSpigot.getInstance().getPlayer(e.getPlayer()).setRank(rank);
            RalamaSpigot.getInstance().getPlayer(e.getPlayer()).setCoins(coinsResponse.getCoins());

            if (rank.equalsIsHigher(Rank.MANAGER)) {
                e.getPlayer().setOp(true);
            }

            if (RalamaSpigot.getInstance().isLobby())
                Bukkit.getScheduler().runTask(RalamaSpigot.getInstance(), () -> RalamaSpigot.getInstance().getLobbyJoinListener().onJoin(e.getPlayer()));
        });

    }

}
