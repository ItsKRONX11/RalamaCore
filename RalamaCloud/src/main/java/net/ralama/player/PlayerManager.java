package net.ralama.player;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import net.ralama.Ralama;
import net.ralama.message.Messages;
import net.ralama.packets.api.Constants;
import net.ralama.packets.api.Language;
import net.ralama.packets.api.Message;
import net.ralama.packets.api.Rank;
import net.ralama.punishment.Punishment;
import net.ralama.punishment.PunishmentType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class PlayerManager {
    @Getter
    private final List<RalamaPlayer> players = new CopyOnWriteArrayList<>();
    @Getter
    private final Map<String, RalamaPlayer> namePlayer = new ConcurrentHashMap<>();
    @Getter
    private final HashMap<Long, String> discordIdPlayer = new HashMap<>();
    @Getter
    private final List<String> rankPlayers = new CopyOnWriteArrayList<>();
    @Getter
    private final List<String> punishedPlayers = new CopyOnWriteArrayList<>();
    private final List<String> queuedPlayers = new CopyOnWriteArrayList<>();
    @Getter
    private final List<String> onlinePlayers = new CopyOnWriteArrayList<>();
    @Getter
    private final ScheduledFuture<?> rankExpireTask;
    @Getter
    private final ScheduledFuture<?> punishExpireTask;
    @Getter
    private final ScheduledFuture<?> userSaverTask;
    @Getter
    private final ScheduledExecutorService service = Executors.newScheduledThreadPool(5);

    public PlayerManager() {
        rankExpireTask = service.scheduleAtFixedRate(() -> {
            for (String name : rankPlayers) {
                RalamaPlayer player = getPlayerByName(name);
                if (player.getRankEnd() <= System.currentTimeMillis() && player.getRankEnd() != 0) {
                    player.sendMessage(Ralama.PREFIX + "§cYour rank has expired!");

                    player.setRankEnd(0);
                    player.setRank(player.getFallback());
                    player.setFallbackRank(Rank.PLAYER);
                }
            }
        }, 10, 1, TimeUnit.SECONDS);


        punishExpireTask = service.scheduleAtFixedRate(() -> {
            for (String name : punishedPlayers) {
                RalamaPlayer player = getPlayerByName(name);

                Punishment ban = player.getBan();
                Punishment mute = player.getMute();

                if (ban != null) {
                    long end = ban.getEnd();
                    if (end != 0 && end <= System.currentTimeMillis()) {
                        this.unban(player, this.getPlayerByName("CloudAdmin"), System.currentTimeMillis(), "AUTO");
                        Ralama.getLogger().info("Unbanned player " + player.getName() + ".");
                    }
                }
                if (mute != null) {
                    long end = mute.getEnd();
                    if (end != 0 && end <= System.currentTimeMillis()) {
                        this.unmute(player, this.getPlayerByName("CloudAdmin"), System.currentTimeMillis(), "AUTO");
                        Ralama.getLogger().info("Unmuted player " + player.getName() + ".");
                    }
                }
            }
        }, 10, 1, TimeUnit.SECONDS);

        userSaverTask = service.scheduleAtFixedRate(this::saveQueued, 5, 5, TimeUnit.MINUTES);
    }

    public void loadPlayers() throws SQLException {
        players.clear();
        namePlayer.clear();

        PreparedStatement ps = Ralama.getDatabase().getConnection().prepareStatement("SELECT * FROM players;");
        ResultSet rs = ps.executeQuery();
        int count = 0;

        while (rs.next()) {
            ++count;

            UUID uuid = UUID.fromString(rs.getString("UUID"));
            String name = rs.getString("NAME");
            Rank rank = Rank.valueOf(rs.getString("RANK"));
            Rank fallback = rs.getString("FALLBACK") == null ? null : Rank.valueOf(rs.getString("FALLBACK"));
            long rankEnd = rs.getLong("RANK_END");
            int coins = rs.getInt("COINS");
            long firstJoined = Long.parseLong(rs.getString("FIRST_JOINED"));
            long lastJoined = Long.parseLong(rs.getString("LAST_JOINED"));
            Punishment ban = rs.getString("BANDATA") == null ? null : Punishment.fromSaveArray(rs.getString("BANDATA").split("§"), PunishmentType.BAN);
            Punishment mute = rs.getString("MUTEDATA") == null ? null : Punishment.fromSaveArray(rs.getString("MUTEDATA").split("§"), PunishmentType.MUTE);
            int banPoints = rs.getInt("BAN_POINTS");
            int mutePoints = rs.getInt("MUTE_POINTS");
            long playTime = Long.parseLong(rs.getString("PLAYTIME"));
            String ip = rs.getString("IP");
            String password = rs.getString("PASSWORD");
            boolean premium = rs.getInt("PREMIUM") != 0;
            String chatColor = rs.getString("CHATCOLOR");
            long discordId = rs.getLong("DISCORD_ID");
            boolean notify = rs.getBoolean("NOTIFY");
            double x = rs.getDouble("X");
            double y = rs.getDouble("Y");
            double z = rs.getDouble("Z");
            float yaw = rs.getFloat("YAW");
            float pitch = rs.getFloat("PITCH");
            Language language = Language.valueOf(rs.getString("LANGUAGE"));

            new RalamaPlayer(
                    firstJoined,
                    lastJoined,
                    rank, fallback,
                    rankEnd,
                    coins, name,
                    uuid, ban, mute,
                    banPoints, mutePoints,
                    playTime, ip, password,
                    premium, chatColor, discordId,
                    notify,
                    x, y, z, yaw, pitch,
                    language);
        }
        PreparedStatement punishments = Ralama.getDatabase().getConnection().prepareStatement("SELECT * FROM punishments ORDER BY CREATED");
        ResultSet punishmentsResultSet = punishments.executeQuery();

        while (punishmentsResultSet.next()) {
            String name = punishmentsResultSet.getString("NAME");
            long created = punishmentsResultSet.getLong("CREATED");
            long ends = punishmentsResultSet.getLong("EXPIRES");
            PunishmentType type = PunishmentType.valueOf(punishmentsResultSet.getString("TYPE"));
            String staff = punishmentsResultSet.getString("STAFF");
            String reason = punishmentsResultSet.getString("REASON");

            getPlayerByName(name).getPunishments().add(new Punishment(name, staff, ends, created, reason, type));
        }

        Ralama.getLogger().info("Loaded " + count + " players from the database!");

        if (getPlayerByName("CloudAdmin") == null) {
            new RalamaPlayer(0, 0,
                    Rank.OWNER, Rank.OWNER,
                    0, 0,
                    "CloudAdmin",
                    UUID.nameUUIDFromBytes("OfflinePlayer:CloudAdmin".getBytes()),
                    null,
                    null,
                    0, 0, 0,
                    "127.0.0.1",
                    null,
                    true,
                    "",
                    0,
                    false,
                    0,
                    0,
                    0,
                    -361,
                    0,
                    Language.ENGLISH).updateToDatabase(true);
        }
    }

    public RalamaPlayer getPlayerByName(String name) {
        return namePlayer.get(name.toLowerCase());
    }

    public RalamaPlayer getPlayerById(long id) {
        return getPlayerByName(discordIdPlayer.get(id));
    }

    public void saveQueued() {
        for (String name : queuedPlayers) {
            getPlayerByName(name).saveUser(false);
        }
        this.queuedPlayers.clear();
    }

    public void sendStaffMessage(String message) {
        for (String name : this.rankPlayers) {
            RalamaPlayer player = getPlayerByName(name);
            if (player.isStaff() && !player.isNotify()) {
                player.sendMessage(message);
            }
        }
    }

    public void sendStaffMessage(Message message) {
        for (String name : this.rankPlayers) {
            RalamaPlayer player = getPlayerByName(name);
            if (player.isStaff() && !player.isNotify()) {
                player.sendMessage(message);
            }
        }
    }

    public void ban(RalamaPlayer target, RalamaPlayer sender, long created, long expires, String reason, String comment) {
        target.setBan(new Punishment(target.getName(), sender.getName(), expires, created, reason, PunishmentType.BAN));

        if (target.isOnline()) target.kick(Constants.getBanKick(reason, expires, target.getLanguage()), false);

        insertPunishment(target, reason, sender, created, expires, PunishmentType.BAN);

        sendStaffMessage(Messages.BAN_ALERT
                .replaced("%player%", target.getColoredName())
                .replaced("%staff%", sender.getColoredName())
                .replaced("%reason%", reason)
                .replaced("%expires%", Constants.formatDate(expires))
                .replaced("%comment%", comment));

        Ralama.getDiscordManager().sendPunishmentEmbed(target, sender, expires, comment, reason, PunishmentType.BAN);

        if (!this.punishedPlayers.contains(target.getName())) punishedPlayers.add(target.getName());
    }

    public void mute(RalamaPlayer target, RalamaPlayer sender, long created, long expires, String reason, String comment) {
        target.setMute(new Punishment(target.getName(), sender.getName(), expires, created, reason, PunishmentType.MUTE));

        insertPunishment(target, reason, sender, created, expires, PunishmentType.MUTE);

        sendStaffMessage(Messages.MUTE_ALERT
                .replaced("%player%", target.getColoredName())
                .replaced("%staff%", sender.getColoredName())
                .replaced("%reason%", reason)
                .replaced("%expires%", Constants.formatDate(expires))
                .replaced("%comment%", comment));


        Ralama.getDiscordManager().sendPunishmentEmbed(target, sender, expires, comment, reason, PunishmentType.MUTE);

        if (!this.punishedPlayers.contains(target.getName())) punishedPlayers.add(target.getName());
    }

    public void kick(RalamaPlayer target, RalamaPlayer sender, long created, String reason) {
        target.kick(reason);

        insertPunishment(target, reason, sender, created, 0, PunishmentType.KICK);

        sendStaffMessage(Messages.KICK_ALERT
                .replaced("%player%", target.getColoredName())
                .replaced("%staff%", sender.getColoredName())
                .replaced("%reason%", reason));
    }

    public void unban(RalamaPlayer target, RalamaPlayer sender, long created, String reason) {
        target.setBan(null);

        insertPunishment(target, reason, sender, created, 0, PunishmentType.UNBAN);

        if (target.getMute() == null) punishedPlayers.remove(target.getName());
    }

    public void unmute(RalamaPlayer target, RalamaPlayer sender, long created, String reason) {
        target.setMute(null);

        insertPunishment(target, reason, sender, created, 0, PunishmentType.UNMUTE);

        if (target.getBan() == null) punishedPlayers.remove(target.getName());
    }

    private void insertPunishment(RalamaPlayer player, String reason, RalamaPlayer from, long created, long expires, PunishmentType type) {
        player.getPunishments().add(new Punishment(player.getName(), from.getName(), expires, created, reason, type));
        try {
            PreparedStatement ps = Ralama.getDatabase().getConnection().prepareStatement("INSERT INTO punishments (NAME,REASON,STAFF,CREATED,EXPIRES,TYPE) VALUES (?,?,?,?,?,?);");
            ps.setString(1, player.getName());
            ps.setString(2, reason);
            ps.setString(3, from.getName());
            ps.setLong(4, created);
            ps.setLong(5, expires);
            ps.setString(6, type.name());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ImmutableList<String> getQueuedPlayers() {
        return ImmutableList.copyOf(queuedPlayers);
    }

    public void addToDatabaseQueue(RalamaPlayer player) {
        String name = player.getName();
        if (this.queuedPlayers.contains(name)) return;
        this.queuedPlayers.add(name);
    }

    public void addToRankPlayers(RalamaPlayer player) {
        String name = player.getName();
        if (this.rankPlayers.contains(name)) return;
        this.rankPlayers.add(name);
    }

    public void removeFromRankPlayers(RalamaPlayer player) {
        this.rankPlayers.remove(player.getName());
    }

}
