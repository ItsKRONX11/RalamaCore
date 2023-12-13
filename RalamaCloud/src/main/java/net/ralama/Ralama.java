package net.ralama;

import lombok.Getter;
import net.ralama.command.CommandManager;
import net.ralama.database.Database;
import net.ralama.discord.DiscordManager;
import net.ralama.file.FileManager;
import net.ralama.filter.FilterManager;
import net.ralama.message.Messages;
import net.ralama.player.PlayerManager;
import net.ralama.player.RalamaPlayer;
import net.ralama.server.ServerManager;
import org.apache.logging.log4j.Logger;

public final class Ralama {
    public static final String PREFIX = "§9§lRalama§r §7▸ ";
    public static final String STAFF_PREFIX = "§5§lStaff §7▸ ";
    public static final String NOT_ALLOWED_INTERACT = PREFIX + "§cYou are not allowed to interact with this player!";
    public static final String SERVER_PREFIX = "§b§lServer §7▸ ";
    public static final String NEVER_JOINED = PREFIX + "§cThis user never joined the network!";
    public static final String COINS_PREFIX = "§6§lRalaCoins §7▸ ";
    public static final String WARN_PREFIX = "§c§lWarn §7▸ ";
    public static final String ERROR = PREFIX + "§cAn error occured while executing the command.";
    public static final String CLOUD_PREFIX = "§b§lCloud §7▸ ";
    public static final String UNKNOWN_COMMAND = PREFIX + "§cUnknown sub-command.";
    @Getter
    private static RalamaCloud instance;

    private Ralama() {
    }

    public static void setInstance(RalamaCloud instance) {
        if (Ralama.instance != null) {
            throw new UnsupportedOperationException("Cannot redefine singleton instance");
        }
        Ralama.instance = instance;
    }

    public static CommandManager getCommandManager() {
        return instance.getCommandManager();
    }

    public static Database getDatabase() {
        return instance.getDatabase();
    }

    public static DiscordManager getDiscordManager() {
        return instance.getDiscordManager();
    }

    public static RalamaPlayer getPlayer(String name) {
        return instance.getPlayerManager().getPlayerByName(name);
    }

    public static PlayerManager getPlayerManager() {
        return instance.getPlayerManager();
    }

    public static FileManager getFileManager() {
        return instance.getFileManager();
    }

    public static ServerManager getServerManager() {
        return instance.getServerManager();
    }

    public static Logger getLogger() {
        return instance.getLogger();
    }

    public static FilterManager getFilterManager() {
        return instance.getFilterManager();
    }
    public static boolean checkPlayer(RalamaPlayer sender, RalamaPlayer target) {
        if (target == null) {
            sender.sendMessage(Messages.NEVER_JOINED);
            return true;
        }
        if (!target.isOnline()) {
            sender.sendMessage(Messages.NOT_ONLINE);
            return true;
        }
        return false;
    }
}
