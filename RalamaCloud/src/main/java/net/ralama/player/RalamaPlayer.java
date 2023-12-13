package net.ralama.player;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Member;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.ralama.ColoredConsole;
import net.ralama.Ralama;
import net.ralama.message.ChatMessage;
import net.ralama.message.MessageGroup;
import net.ralama.message.Messages;
import net.ralama.packets.Packet;
import net.ralama.packets.api.Constants;
import net.ralama.packets.api.Language;
import net.ralama.packets.api.Message;
import net.ralama.packets.api.Rank;
import net.ralama.packets.in.PlayerLanguagePacket;
import net.ralama.packets.in.PlayerLobbyLocationPacket;
import net.ralama.packets.out.*;
import net.ralama.player.multi.Party;
import net.ralama.punishment.Punishment;
import net.ralama.server.RalamaProxy;
import net.ralama.server.RalamaServer;
import net.ralama.server.ServerState;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public final class RalamaPlayer {
    @Getter
    private final long firstJoined;
    @Getter
    private final String name;
    @Getter
    private final UUID uuid;
    @Getter
    private final List<String> friends = new CopyOnWriteArrayList<>();
    @Getter
    private final List<String> requests = new CopyOnWriteArrayList<>();
    @Getter
    private final List<ChatMessage> messages = new CopyOnWriteArrayList<>();
    @Getter
    private final List<Punishment> punishments = new CopyOnWriteArrayList<>();
    @Getter
    private long lastJoined;
    @Getter
    private int coins;
    @Getter
    private Punishment ban;
    @Getter
    private Punishment mute;
    @Getter
    private Rank rank;
    @Getter
    private Rank fallback;
    @Getter
    private long rankEnd;
    @Getter
    private int banPoints;
    @Getter
    private int mutePoints;
    @Getter
    private long playTime;
    @Setter
    @Getter
    private String lastMessage;
    @Getter
    private String ip;
    @Getter
    private String password;
    @Getter
    private boolean loggedIn;
    @Getter
    private boolean premium;
    @Getter
    @Setter
    private boolean notify;
    @Getter
    @Setter
    private boolean awaitingPremium;
    @Getter
    @Setter
    private RalamaPlayer lastSender;
    @Getter
    private String chatColor;
    @Getter
    private Language language;
    @Getter
    @Setter
    private Party party;
    @Getter
    private long discordId;
    @Getter
    @Setter
    private RalamaServer server;
    @Getter
    @Setter
    private RalamaProxy proxy;
    @Getter
    @Setter
    private double x;
    @Getter
    @Setter
    private double y;
    @Getter
    @Setter
    private double z;
    @Getter
    @Setter
    private float yaw;
    @Getter
    @Setter
    private float pitch;

    // String banData[] = new String[]{created, expires, reason, comment, from}

    public RalamaPlayer(long firstJoined, long lastJoined, Rank rank, Rank fallback, long rankEnd, int coins, String name, UUID uuid, Punishment ban, Punishment mute, int banPoints, int mutePoints, long playTime, String ip, String password, boolean premium, String chatColor, long discordId, boolean notify, double x, double y, double z, float yaw, float pitch, Language language) {
        this.firstJoined = firstJoined;
        this.lastJoined = lastJoined;
        this.coins = coins;
        this.name = name;
        this.uuid = uuid;
        this.banPoints = banPoints;
        this.mutePoints = mutePoints;
        this.ban = ban;
        this.mute = mute;
        this.rank = rank;
        this.fallback = fallback;
        this.rankEnd = rankEnd;
        this.playTime = playTime;
        this.language = language;
        this.ip = ip;
        this.password = password;
        this.loggedIn = false;
        this.notify = notify;
        this.premium = premium;
        this.awaitingPremium = false;
        this.chatColor = chatColor;
        this.discordId = discordId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;

        Ralama.getPlayerManager().getNamePlayer().put(this.name.toLowerCase(), this);
        Ralama.getPlayerManager().getPlayers().add(this);

        if (discordId != 0)
            Ralama.getPlayerManager().getDiscordIdPlayer().put(discordId, this.name);

        if (ban != null || mute != null) {
            Ralama.getPlayerManager().getPunishedPlayers().add(this.name);
        }

        if (rank.isHigher(Rank.PLAYER)) {
            Ralama.getPlayerManager().addToRankPlayers(this);
        }
    }

    public void updateToDatabase(boolean insert) {
        if (!insert) {
            Ralama.getPlayerManager().addToDatabaseQueue(this);
            return;
        }
        saveUser(true);
    }

    public void updateToDatabase() {
        this.updateToDatabase(false);
    }

    public void saveUser(boolean insert) {
        Connection connection = Ralama.getDatabase().getConnection();
        try {
            PreparedStatement ps;
            if (insert) {
                ps = connection.prepareStatement("INSERT INTO players (UUID,NAME,RANK,COINS,FIRST_JOINED,LAST_JOINED,BANDATA,MUTEDATA,BAN_POINTS,MUTE_POINTS,PLAYTIME,IP,PASSWORD,PREMIUM,RANK_END,FALLBACK,CHATCOLOR,DISCORD_ID,NOTIFY,X,Y,Z,YAW,PITCH,LANGUAGE) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
                ps.setString(1, uuid.toString());
                ps.setString(2, name);
                ps.setString(3, String.valueOf(rank));
                ps.setInt(4, coins);
                ps.setString(5, String.valueOf(firstJoined));
                ps.setString(6, String.valueOf(lastJoined));
                ps.setString(7, ban == null ? null : ban.toString());
                ps.setString(8, mute == null ? null : mute.toString());
                ps.setInt(9, banPoints);
                ps.setInt(10, mutePoints);
                ps.setString(11, String.valueOf(playTime));
                ps.setString(12, ip);
                ps.setString(13, password);
                ps.setInt(14, premium ? 1 : 0);
                ps.setLong(15, rankEnd);
                ps.setString(16, String.valueOf(fallback));
                ps.setString(17, chatColor);
                ps.setLong(18, discordId);
                ps.setBoolean(19, notify);
                ps.setDouble(20, x);
                ps.setDouble(21, y);
                ps.setDouble(22, z);
                ps.setFloat(23, yaw);
                ps.setFloat(24, pitch);
                ps.setString(25, language.name());
                ps.executeUpdate();
            } else {
                ps = connection.prepareStatement(
                        "UPDATE players SET RANK = ?, COINS = ?, FIRST_JOINED = ?, LAST_JOINED = ?, BANDATA = ?, MUTEDATA = ?, BAN_POINTS = ?, MUTE_POINTS = ?, PLAYTIME = ?, IP = ?, PASSWORD = ?, PREMIUM = ?, RANK_END = ?, FALLBACK = ?, CHATCOLOR = ?, DISCORD_ID = ?, NOTIFY = ?, X = ?, Y = ?, Z = ?, YAW = ?, PITCH = ?, LANGUAGE = ? WHERE UUID = ?;");
                ps.setString(1, String.valueOf(rank));
                ps.setInt(2, coins);
                ps.setString(3, String.valueOf(firstJoined));
                ps.setString(4, String.valueOf(lastJoined));
                ps.setString(5, ban == null ? null : ban.toString());
                ps.setString(6, mute == null ? null : mute.toString());
                ps.setInt(7, banPoints);
                ps.setInt(8, mutePoints);
                ps.setString(9, String.valueOf(playTime));
                ps.setString(10, ip);
                ps.setString(11, password);
                ps.setInt(12, (premium) ? 1 : 0);
                ps.setLong(13, rankEnd);
                ps.setString(14, String.valueOf(fallback));
                ps.setString(15, chatColor);
                ps.setLong(16, discordId);
                ps.setBoolean(17, notify);
                ps.setDouble(18, x);
                ps.setDouble(19, y);
                ps.setDouble(20, z);
                ps.setFloat(21, yaw);
                ps.setFloat(22, pitch);
                ps.setString(23, language.name());
                ps.setString(24, uuid.toString());
                ps.executeUpdate();
            }
        } catch (SQLException exc) {
            exc.printStackTrace();
        }
    }

    public void setLastJoined(long lastJoined) {
        this.lastJoined = lastJoined;

        this.updateToDatabase();
    }

    public boolean isOnline() {
        return this.proxy != null;
    }

    public void sendMessage(String english, String romanian) {
        if (this.language == Language.ROMANIAN) {
            this.sendMessage(romanian);
        } else {
            this.sendMessage(english);
        }
    }
    public void sendMessage(String prefix, String en, String ro) {
        this.sendMessage(prefix + en, prefix + ro);
    }

    public void sendMessage(String message) {
        if (this.isOnline()) {
            this.proxy.sendPacket(new PlayerMessagePacket(this.name, message));
        } else if (isSysAcc()) {
            ColoredConsole.sendMessage(message);
        }
    }

    public void sendMessage(MessageGroup messageGroup) {
        this.sendMessage(messageGroup.toString(this.language));
    }

    public void sendMessage(BaseComponent text) {
        if (this.isOnline()) {
            this.proxy.sendPacket(new MessageComponentPacket(this.name, ComponentSerializer.toString(text)));
        } else if (isSysAcc()) {
            ColoredConsole.sendMessage(text.toLegacyText());
        }
    }

    public void sendMessage(BaseComponent... components) {
        if (this.isOnline()) {
            this.proxy.sendPacket(new MessageComponentPacket(this.name, ComponentSerializer.toString(components)));
        } else if (isSysAcc()) {
            ColoredConsole.sendMessage(new TextComponent(components).toLegacyText());
        }
    }

    public void sendMessage(Message message) {
        this.sendMessage(message.toString(this.language));
    }

    public void setCoins(int coins) {
        this.coins = coins;

        this.updateToDatabase();

        this.sendMessage(Ralama.PREFIX + "You now have §b" + coins + " §7coins.");

        if (this.isOnline()) {
            this.server.sendPacket(new PlayerCoinsPacket(name, this.coins));
        }
    }

    public void addCoins(int i) {
        this.coins = coins + i;

        this.updateToDatabase();

        this.sendMessage(Ralama.PREFIX + "§b" + i + " §7coins were added to your balance.");

        if (this.isOnline()) {
            this.server.sendPacket(new PlayerCoinsPacket(name, this.coins));
        }
    }

    public void removeCoins(int i) {
        this.coins = coins - i;

        this.updateToDatabase(false);

        this.sendMessage(Ralama.PREFIX + "§b" + i + " §7coins were removed from your balance.");

        if (this.isOnline()) {
            this.server.sendPacket(new PlayerCoinsPacket(name, this.coins));
        }
    }

    public boolean isStaff() {
        return this.rank.isStaff();
    }

    public boolean hasRank() {
        return this.rank.isHigher(Rank.PLAYER);
    }

    public String getColoredName() {
        return this.rank.getColor() + name;
    }


    public void setRank(Rank rank) {
        Rank oldRank = this.rank;
        this.rank = rank;
        this.sendMessage(Messages.RANK_CHANGE.replaced("%rank%", rank.getDisplayName())
                .replaced("%expires%", rankEnd == 0 ? "§anever" : Constants.formatDate(rankEnd)));

        this.updateToDatabase(false);

        if (this.isOnline()) {
            server.sendPacket(new OutPlayerRankPacket(name, rank.name()));
        }
        if (rank.isHigher(Rank.PLAYER)) {
            Ralama.getPlayerManager().addToRankPlayers(this);
        } else {
            Ralama.getPlayerManager().removeFromRankPlayers(this);
        }

        if (isLinked()) {
            new Thread(() -> {
                Member member = Ralama.getDiscordManager().getGuild().retrieveMemberById(discordId).complete();
                if (member == null) return;

                for (long id : oldRank.getDiscordId()) {
                    if (Ralama.getDiscordManager().getGuild().getRoleById(id) != null) {
                        Ralama.getDiscordManager().getGuild().removeRoleFromMember(member, Ralama.getDiscordManager().getGuild().getRoleById(id)).queue();
                    }
                }

                for (long id : rank.getDiscordId()) {
                    if (Ralama.getDiscordManager().getGuild().getRoleById(id) != null) {
                        Ralama.getDiscordManager().getGuild().addRoleToMember(member, Ralama.getDiscordManager().getGuild().getRoleById(id)).queue();
                    }
                }
            }).start();
        }
    }
    // String banData[] = new String[]{created, expires, reason, comment, from} // 265827501§2159155§Toxic§injura pe bw§itskronx11

    public boolean isLinked() {
        return this.discordId != 0;
    }

    public void setBan(Punishment ban) {
        this.ban = ban;
        this.updateToDatabase();
    }

    public void setMute(Punishment mute) {
        this.mute = mute;

        if (this.isOnline())
            this.proxy.sendPacket(new PlayerMutePacket(this.name, mute == null ? null : mute.toSaveArray()));

        this.updateToDatabase();
    }

    public void setBanPoints(int banPoints) {
        this.banPoints = banPoints;
        this.updateToDatabase();
    }

    public void addBanPoints(int points) {
        setBanPoints(this.banPoints + points);
    }

    public void removeBanPoints(int points) {
        setBanPoints(this.banPoints - points);
    }

    public void setMutePoints(int mutePoints) {
        this.mutePoints = mutePoints;
        this.updateToDatabase();
    }

    public void addMutePoints(int points) {
        setMutePoints(this.mutePoints + points);
    }

    public void removeMutePoints(int points) {
        setMutePoints(this.mutePoints - points);
    }


    public void setIp(String ip) {
        this.ip = ip;

        this.updateToDatabase(false);
    }

    public void setLanguage(Language language) {
        if (this.language == language) return;

        this.language = language;

        this.sendMessage(Messages.LANGUAGE_CHANGE);

        if (this.isOnline()) {
            Packet langPacket = new PlayerLanguagePacket(this.name, this.language);
            this.proxy.sendPacket(langPacket);
            this.server.sendPacket(langPacket);
        }

        this.updateToDatabase();
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;

        Packet authPacket = new PlayerAuthPacket(this.name, loggedIn);
        if (this.isOnline()) {
            this.proxy.sendPacket(authPacket);
        }
        if (this.server != null && this.server.isLobby()) {
            server.sendPacket(authPacket);
        }
    }

    public void setPassword(String password) {
        this.password = password;

        this.updateToDatabase();
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
        this.updateToDatabase();
    }

    public void addPlayTime(long time) {
        playTime = playTime + time;

        this.updateToDatabase();

    }

    public void kick(String reason, boolean format) {
        if (!isOnline()) return;

        if (format) reason = Constants.KICK_PREFIX + "\n\n§c" + reason;

        this.proxy.sendPacket(new PlayerKickPacket(name, reason));
    }

    public void kick(String reason) {
        this.kick(reason, true);
    }

    public int getAccesLevel() {
        return rank.getAccessLevel();
    }

    public boolean isSysAcc() {
        return this.name.equals("CloudAdmin");
    }

    public void setFallbackRank(Rank rank) {
        this.fallback = rank;

        this.updateToDatabase(false);
    }

    public void setRankEnd(long rankEnd) {
        this.rankEnd = rankEnd;

        this.updateToDatabase(false);
    }

    public void setChatColor(String chatPrefix) {
        chatColor = chatPrefix;

        this.updateToDatabase(false);
    }

    public boolean equals(RalamaPlayer player) {
        if (this == player) {
            return true;
        }

        return this.uuid.equals(player.getUuid());
    }

    public void setDiscordId(long id) {
        Ralama.getPlayerManager().getDiscordIdPlayer().remove(discordId);
        this.discordId = id;

        this.updateToDatabase();
        if (id != 0)
            Ralama.getPlayerManager().getDiscordIdPlayer().put(id, this.name);
    }

    public void connect(RalamaServer server) {
        if (!this.isOnline()) return;

        if (server == null) {
            sendMessage(Ralama.PREFIX + "§cThat server does not exist!");
            return;
        }
        if (server.getState() == ServerState.STARTING) {
            sendMessage(Ralama.PREFIX + "§cThat server hasn't started yet!");
            return;
        }
        if (this.server == server) {
            sendMessage(Ralama.PREFIX + "§cYou are already connected to this server!");
            return;
        }

        this.proxy.sendPacket(new PlayerConnectPacket(server.getName(), this.name));

        if (server.isLobby()) {
            if (this.yaw != -361) {
                server.sendPacket(new PlayerLobbyLocationPacket(this.name, this.x, this.y, this.z, this.pitch, this.yaw));
            }
        }
    }

    public void switchServer(RalamaServer server) {
        if (this.server != null) {
            this.server.getPlayers().remove(this.name);
        }

        System.out.println("[Player Connection] [" + this.name + "] [" + this.server + " -> " + server + "]");

        this.server = server;

        server.getPlayers().add(this.name);

        if (this.party != null && this.party.isLeader(this)) {
            this.party.switchServer(server);
        }

        if (server.isLobby()) {
            if (this.yaw != -361) {
                server.sendPacket(new PlayerLobbyLocationPacket(this.name, this.x, this.y, this.z, this.pitch, this.yaw));
            }
        }
        server.sendPacket(new PlaytimePacket(this.playTime, this.name));
        server.sendPacket(new OutPlayerRankPacket(this.name, this.rank.name()));
        server.sendPacket(new PlayerCoinsPacket(this.name, this.coins));
        server.sendPacket(new PlayerAuthPacket(this.name, this.loggedIn));
    }
}
