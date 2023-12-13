package net.ralama.lobby;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import net.ralama.lobby.listener.Listener;
import net.ralama.lobby.packet.LobbyPacketReceiver;
import net.ralama.lobby.scoreboard.ScoreboardManager;
import net.ralama.packets.api.Rank;
import net.ralama.packets.api.RemoteUser;
import net.ralama.spigot.ItemBuilder;
import net.ralama.spigot.RalamaSpigot;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class RalamaLobby extends JavaPlugin {
    @Getter
    private static RalamaLobby instance;
    @Getter
    private LobbyPacketReceiver packetReceiver;
    @Getter
    private Map<String, Location> playerLocation;
    @Getter
    private Material launchingBlock;
    @Getter
    private Location spawnLocation;
    @Getter
    private ScoreboardManager scoreboardManager;

    @Override
    public void onEnable() {
        if (getServer().getPluginManager().getPlugin("RalamaSpigot") == null) {
            getLogger().warning("RalamaSpigot not found! This plugin will NOT start.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        instance = this;
        packetReceiver = new LobbyPacketReceiver();
        RalamaSpigot.receivers[1] = packetReceiver;
        scoreboardManager = new ScoreboardManager();
        playerLocation = new ConcurrentHashMap<>();
        launchingBlock = Material.SLIME_BLOCK;
        spawnLocation = new Location(Bukkit.getWorld("world"), 243.5, 50, 1347.5, -180, 0); // 243 50 1347 | -180 0
        System.out.println("[RalamaLobby] " + RalamaSpigot.getInstance().getServerName());
        Bukkit.getPluginManager().registerEvents(new Listener(), this);

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, () -> {
            spawnLocation.getWorld().setStorm(false);
            spawnLocation.getWorld().setTime(14000);
        }, 20L, 20L);

        RalamaSpigot.getInstance().setLobbyJoinListener(player -> {
            player.getInventory().clear();
            player.setGameMode(GameMode.ADVENTURE);
            player.setHealth(20);
            player.setFoodLevel(20);
            player.getInventory().setItem(8, new ItemBuilder(XMaterial.CLOCK.parseMaterial()).name("§a§lLobby Selector").build());
            RemoteUser user = RalamaSpigot.getInstance().getPlayer(player);
            if (user.getRank().isHigher(Rank.PLAYER)) {
                player.setAllowFlight(true);
            }
            if (RalamaLobby.getInstance().getPlayerLocation().containsKey(player.getName())) {
                boolean wasLoggedIn = user.isLoggedIn();
                if (!wasLoggedIn) user.setLoggedIn(true);
                player.teleport(RalamaLobby.getInstance().getPlayerLocation().get(player.getName()));
                if (!wasLoggedIn) user.setLoggedIn(false);
                if (user.getRank().isHigher(Rank.PLAYER) && player.getLocation().getBlock().getRelative(0, -1, 0).getType() == Material.AIR) {
                    player.setFlying(true);
                }
            } else {
                player.teleport(RalamaLobby.getInstance().getSpawnLocation());
            }
        });
    }

    public Location getLocation(Player player) {
        return this.playerLocation.getOrDefault(player.getName(), spawnLocation);
    }
}
