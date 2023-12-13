package net.ralama.spigot;

import com.avaje.ebean.validation.NotNull;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.ralama.packets.api.Rank;
import org.bukkit.entity.Player;

public class Placeholder extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "ralama";
    }

    @Override
    public @NotNull String getAuthor() {
        return "ItsKRONX11";
    }

    @Override
    public @NotNull String getVersion() {
        return null;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        Rank rank = RalamaSpigot.getInstance().getPlayer(player).getRank();
        if (params.equalsIgnoreCase("coins")) {
            return Integer.toString(RalamaSpigot.getInstance().getPlayer(player).getCoins());
        }
        if (params.equalsIgnoreCase("color")) {
            return rank.getColor();
        }
        if (params.equalsIgnoreCase("rank")) {
            return rank.getDisplayName();
        }
        if (params.equalsIgnoreCase("rankshortname")) {
            return rank.getShortName();
        }
        if (params.equalsIgnoreCase("onlinecount")) {
            return String.valueOf(RalamaSpigot.getInstance().getGlobalOnline());
        }
        if (params.equalsIgnoreCase("ranktab")) {
            return rank.getAccessLevel() <= 10 ? "" : rank.getColor() + rank.getShortName() + " §8┃ " + rank.getColor();
        }
        return null;
    }
}
