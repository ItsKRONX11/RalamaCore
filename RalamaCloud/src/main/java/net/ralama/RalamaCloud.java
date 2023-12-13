package net.ralama;

import lombok.Getter;
import net.ralama.command.CommandManager;
import net.ralama.database.Database;
import net.ralama.discord.DiscordManager;
import net.ralama.file.FileManager;
import net.ralama.filter.FilterManager;
import net.ralama.netty.NettyServer;
import net.ralama.packets.out.OutKillRequestPacket;
import net.ralama.player.PlayerManager;
import net.ralama.server.RalamaProxy;
import net.ralama.server.RalamaServer;
import net.ralama.server.ServerManager;
import net.ralama.support.SupportWaitList;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class RalamaCloud {
    public static final String MOTD = "§4           §4§l§m◂▸§c§l§m◂▸§6§l§m◂▸§e§l§m◂▸§b§l§m◂▸§3§l§m◂▸§9§l§m◂▸§9§l  Ralama  §9§l§m◂▸§3§l§m◂▸§b§l§m◂▸§e§l§m◂▸§6§l§m◂▸§c§l§m◂▸§4§l§m◂▸" + "\n§c           §6§LBEDWARS §e1.8 §7» §e1.20 §6§lSURVIVAL";
    @Getter
    private final CommandManager commandManager;
    @Getter
    private final Database database;
    @Getter
    private final Logger logger;
    @Getter
    private final DiscordManager discordManager;
    @Getter
    private final PlayerManager playerManager;
    @Getter
    private final ServerManager serverManager;
    @Getter
    private final FileManager fileManager;
    @Getter
    private final FilterManager filterManager;
    @Getter
    private final SupportWaitList supportWaitList;
    @Getter
    private long started;

    public RalamaCloud(Logger logger) {
        Ralama.setInstance(this);

        this.logger = logger;
        this.commandManager = new CommandManager();
        this.database = new Database();
        this.serverManager = new ServerManager();
        this.fileManager = new FileManager();
        this.discordManager = new DiscordManager();
        this.playerManager = new PlayerManager();
        this.filterManager = new FilterManager();
        this.supportWaitList = new SupportWaitList();
        try {
            this.database.connect();
            this.serverManager.loadTemplates();
            this.playerManager.loadPlayers();
            this.filterManager.loadEntries();
        } catch (SQLException e) {
            e.printStackTrace();
            stop();
        }
    }

    public void start() {
        this.started = System.currentTimeMillis();
        final int port = 1300;

        logger.info("Starting netty server on port " + port);
        new NettyServer(port).start();
    }

    public void stop() {
        logger.info("Stopping RalamaCloud now...");

        OutKillRequestPacket packet = new OutKillRequestPacket(true);
        for (RalamaProxy proxy : serverManager.getProxies()) {
            proxy.sendPacket(packet);
        }
        System.out.println("Killing proxies");
        for (RalamaServer server : serverManager.getServers()) {
            server.sendPacket(packet);
        }
        System.out.println("Killing servers");

        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(5));
        } catch (InterruptedException ignored) {
        }

        System.out.println("Saving the data of " + playerManager.getQueuedPlayers().size() + " players...");
        playerManager.saveQueued();

        playerManager.getUserSaverTask().cancel(true);
        playerManager.getRankExpireTask().cancel(true);
        playerManager.getPunishExpireTask().cancel(true);
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {
        }
        this.database.disconnect();
        System.out.println("Disconnected from the database");

        System.exit(0);
    }
}
