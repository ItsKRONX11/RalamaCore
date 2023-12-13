package net.ralama.lobby.listener;

import net.ralama.lobby.RalamaLobby;
import net.ralama.packets.api.Rank;
import net.ralama.packets.in.PlayerLobbyLocationPacket;
import net.ralama.spigot.RalamaSpigot;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public class Listener implements org.bukkit.event.Listener {
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!RalamaSpigot.getInstance().getPlayer(e.getPlayer()).isLoggedIn()) {
            e.getPlayer().teleport(RalamaLobby.getInstance().getSpawnLocation());
            return;
        }
        if (e.getPlayer().getLocation().getBlock().getRelative(0, -1, 0).getType().equals(RalamaLobby.getInstance().getLaunchingBlock())) {
            Vector v = RalamaLobby.getInstance().getSpawnLocation().getDirection();
            v.setX(0);
            v.setZ(-10);
            v.setY(3);
            e.getPlayer().setVelocity(v);
            e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.WITHER_SHOOT, 1.0f, 1.0f);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        RalamaSpigot.getInstance().getChannel().writeAndFlush(new PlayerLobbyLocationPacket(
                e.getPlayer().getName(),
                e.getPlayer().getLocation().getX(),
                e.getPlayer().getLocation().getY(),
                e.getPlayer().getLocation().getZ(),
                e.getPlayer().getLocation().getPitch(),
                e.getPlayer().getLocation().getYaw()
        ).serialize());
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {

        if (!RalamaSpigot.getInstance().getPlayer(e.getPlayer().getName()).isLoggedIn()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (!e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Rank rank = RalamaSpigot.getInstance().getPlayer(e.getPlayer()).getRank();
        e.setFormat(" §8┃ " + rank.getDisplayName() + " §8┃ " + rank.getColor() + e.getPlayer().getName() + " §8> §e" + e.getMessage());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        RalamaLobby.getInstance().getScoreboardManager().createBoard(e.getPlayer());
    }
    @EventHandler
    public void on(PlayerSpawnLocationEvent e) {
        e.setSpawnLocation(RalamaLobby.getInstance().getLocation(e.getPlayer()));

    }

}

