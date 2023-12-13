package net.ralama.spigot.event;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface LobbyJoinListener {
    void onJoin(Player player);
}
