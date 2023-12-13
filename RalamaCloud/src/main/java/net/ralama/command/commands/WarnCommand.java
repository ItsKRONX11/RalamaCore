package net.ralama.command.commands;

import net.ralama.Ralama;
import net.ralama.command.Command;
import net.ralama.message.Messages;
import net.ralama.packets.api.Rank;
import net.ralama.player.RalamaPlayer;
import net.ralama.punishment.PunishmentType;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class WarnCommand extends Command {
    public static String PREFIX = Ralama.WARN_PREFIX;

    public WarnCommand() {
        super("warn", Rank.HELPER, "punish");
    }

    @Override
    public void execute(RalamaPlayer sender, String[] args) throws SQLException {
        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            int count = 0;
            StringBuilder builder = new StringBuilder();
            for (Cause cause : Cause.values()) {
                if (cause.minRank.equalsIsLower(sender.getRank())) {
                    ++count;
                    builder.append("\n§8» §c").append(cause.id).append(" §8- §7").append(cause.reason).append(" §8[§7").append(cause.points).append(" points§8, §7").append(cause.type.name()).append("§8]");
                }
            }
            builder.insert(0, Ralama.WARN_PREFIX + "Available punishment templates §8(§c" + count + "§8):");
            sender.sendMessage(builder.toString());
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(Ralama.WARN_PREFIX + "Usage §8(§c2§8):" +
                    "\n§8» §c/warn <player> <id> [comment] §8- §cPunish a player" +
                    "\n§8» §c/warn list §8- §cList punishment templates");
            return;
        }
        RalamaPlayer target = Ralama.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Messages.NEVER_JOINED);
            return;
        }

        if (target.getRank().equalsIsHigher(sender.getRank()) && !sender.getRank().equalsIsHigher(Rank.OWNER)) {
            sender.sendMessage(Ralama.NOT_ALLOWED_INTERACT);
            return;
        }

        Cause cause;
        try {
            cause = Cause.getById(Integer.parseInt(args[1]));
        } catch (NumberFormatException nfe) {
            sender.sendMessage(Ralama.WARN_PREFIX + "§cPlease enter a valid number!");
            return;
        }

        if (cause == null) {
            sender.sendMessage(Ralama.WARN_PREFIX + "§cThat reason doesn't exist!");
            return;
        }

        if (cause.minRank.isHigher(sender.getRank())) {
            sender.sendMessage(Ralama.WARN_PREFIX + "§cYou are not allowed to use this reason!");
            return;
        }

        String comment = null;
        if (args.length >= 3) {
            StringBuilder builder = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                builder.append(args[i]);
                if (i != args.length-1){
                    builder.append(" ");
                }
            }
            comment = builder.toString();
        }

        if (cause.needsComment && comment == null && !sender.getRank().equalsIsHigher(Rank.MANAGER)) {
            sender.sendMessage(Ralama.WARN_PREFIX + "§cYou need to add a comment!");
            return;
        }

        if (cause.type == PunishmentType.BAN) {
            if (target.getBan() != null) {
                sender.sendMessage(Ralama.WARN_PREFIX + "§cThis player is already banned!");
                return;
            }
            target.addBanPoints(cause.points);
            long expires = Cause.getBanLength(target.getBanPoints());
            expires = expires == 0 ? 0 : System.currentTimeMillis() + expires;
            Ralama.getPlayerManager().ban(target, sender, System.currentTimeMillis(), expires, cause.reason, comment);
            sender.sendMessage(Ralama.WARN_PREFIX + "§aYou have banned " + target.getColoredName() + "§a.");

        } else if (cause.type == PunishmentType.MUTE) {
            if (target.getMute() != null) {
                sender.sendMessage(Ralama.WARN_PREFIX + "§cThis player is already muted!");
                return;
            }
            target.addMutePoints(cause.points);
            long expires = Cause.getMuteLength(target.getMutePoints());
            expires = expires == 0 ? 0 : System.currentTimeMillis() + expires;

            String chatlogId = ChatlogCommand.uploadChatlog(target, sender);

            if (chatlogId != null){
                if (comment == null) {
                    comment = ChatlogCommand.URL + chatlogId;
                } else {
                    comment = comment + " " + ChatlogCommand.URL + chatlogId;
                }
            }


            Ralama.getPlayerManager().mute(target, sender, System.currentTimeMillis(), expires, cause.reason, comment);
            sender.sendMessage(Ralama.WARN_PREFIX + "§aYou have muted " + target.getColoredName() + "§a.");

        } else if (cause.type == PunishmentType.KICK) {
            if (!target.isOnline()) {
                sender.sendMessage(Ralama.WARN_PREFIX + "§cThis player is not online!");
                return;
            }
            Ralama.getPlayerManager().kick(target, sender, System.currentTimeMillis(), cause.reason);
            sender.sendMessage(Ralama.WARN_PREFIX + "§aYou have kicked " + target.getColoredName() + "§a.");
        }
    }

    public enum Cause {
        RANK_SCAM(1, "Rank Scam", 40, PunishmentType.BAN, true, Rank.MANAGER),
        BUG_ABUSE_PVP(2, "PvP Bug Abuse", 10, PunishmentType.BAN, true, Rank.DEVELOPER),
        BUG_ABUSE_ECO(3, "Economy Bug Abuse", 20, PunishmentType.BAN, true, Rank.DEVELOPER),
        TOXIC(4, "Toxic", 10, PunishmentType.BAN, true, Rank.COORDINATOR),
        BAN_WORKAROUND(5, "Ban Workaround", 40, PunishmentType.BAN, true, Rank.COORDINATOR),
        UNFAIR_ADVANTAGE(6, "Unfair Advantage", 40, PunishmentType.BAN, true, Rank.MODERATOR),
        OFFENSIVE_USERNAME(7, "Offensive Username", 40, PunishmentType.BAN, false, Rank.HELPER),
        TEAMING(8, "Teaming", 4, PunishmentType.BAN, true, Rank.HELPER),
        SCAM(9, "Scam", 15, PunishmentType.BAN, true, Rank.MODERATOR),
        TROLLING(10, "Trolling", 6, PunishmentType.BAN, true, Rank.HELPER),
        TARGET(11, "Target", 0, PunishmentType.KICK, true, Rank.MODERATOR),
        SKYBASE(12, "Skybase", 0, PunishmentType.KICK, false, Rank.HELPER),
        STAFF_IMPERSONATION(13, "Staff Impersonation", 40, PunishmentType.BAN, true, Rank.MANAGER),
        RACISM(14, "Racism", 10, PunishmentType.MUTE, false, Rank.MODERATOR),
        INSULT(15, "Insult", 4, PunishmentType.MUTE, false, Rank.HELPER),
        CHAT_ABUSE(16, "Chat Abuse", 3, PunishmentType.MUTE, false, Rank.HELPER),
        ADVERTISING(17, "Advertising", 50, PunishmentType.MUTE, false, Rank.MODERATOR),
        MUTE_WORKAROUND(18, "Mute Workaround", 50, PunishmentType.MUTE, true, Rank.COORDINATOR),
        CAPS(19, "Caps Lock", 2, PunishmentType.MUTE, false, Rank.HELPER),
        SPAM(20, "Spam", 2, PunishmentType.MUTE, false, Rank.HELPER),
        OFFENSIVE_BUILDING(21, "Offensive Building", 5, PunishmentType.BAN, true, Rank.HELPER),
        OFFENSIVE_SKIN(22, "Offensive Skin", 1, PunishmentType.BAN, true, Rank.MODERATOR),
        SUPPORT_ABUSE(23, "Support Chat Abuse", 10, PunishmentType.BAN, true, Rank.HELPER),
        SUPPORT_SPAM(24, "Support Chat Spam", 0, PunishmentType.KICK, false, Rank.HELPER),
        BUG_ABUSE_GEN(25, "Bug Abuse", 12, PunishmentType.BAN, true, Rank.DEVELOPER),
        BUG_ABUSE_PERFORMANCE(26, "Bug Abuse (Lag-Machines)", 20, PunishmentType.BAN, true, Rank.SRDEVELOPER);
        private final String reason;
        private final int points;
        private final int id;
        private final PunishmentType type;
        private final boolean needsComment;
        private final Rank minRank;

        Cause(int id, String reason, int points, PunishmentType type, boolean needsComment, Rank minRank) {
            this.reason = reason;
            this.points = points;
            this.id = id;
            this.type = type;
            this.needsComment = needsComment;
            this.minRank = minRank;
        }

        public static Cause getById(int id) {
            for (Cause cause : values()) {
                if (cause.id == id) return cause;
            }
            return null;
        }

        public static long getBanLength(int points) {
            if (points <= 1) {
                return TimeUnit.HOURS.toMillis(12);
            }
            if (points <= 3) {
                return TimeUnit.HOURS.toMillis(20);
            }
            if (points <= 5) {
                return TimeUnit.DAYS.toMillis(1);
            }
            if (points <= 8) {
                return TimeUnit.DAYS.toMillis(3);
            }
            if (points <= 10) {
                return TimeUnit.DAYS.toMillis(5);
            }
            if (points <= 12) {
                return TimeUnit.DAYS.toMillis(7);
            }
            if (points <= 15) {
                return TimeUnit.DAYS.toMillis(10);
            }
            if (points <= 17) {
                return TimeUnit.DAYS.toMillis(14);
            }
            if (points <= 21) {
                return TimeUnit.DAYS.toMillis(20);
            }
            if (points <= 25) {
                return TimeUnit.DAYS.toMillis(30);
            }
            if (points <= 29) {
                return TimeUnit.DAYS.toMillis(60);
            }
            if (points <= 34) {
                return TimeUnit.DAYS.toMillis(90);
            }
            if (points <= 39) {
                return TimeUnit.DAYS.toMillis(120);
            }
            return 0;
        }

        public static long getMuteLength(int points) {
            if (points <= 1) {
                return TimeUnit.MINUTES.toMillis(30);
            }
            if (points == 2) {
                return TimeUnit.MINUTES.toMillis(45);
            }
            if (points <= 4) {
                return TimeUnit.MINUTES.toMillis(60);
            }
            if (points <= 6) {
                return TimeUnit.HOURS.toMillis(6);
            }
            if (points == 7) {
                return TimeUnit.HOURS.toMillis(8);
            }
            if (points <= 10) {
                return TimeUnit.HOURS.toMillis(12);
            }
            if (points <= 12) {
                return TimeUnit.HOURS.toMillis(24);
            }
            if (points <= 14) {
                return TimeUnit.HOURS.toMillis(36);
            }
            if (points <= 18) {
                return TimeUnit.HOURS.toMillis(48);
            }
            if (points <= 20) {
                return TimeUnit.HOURS.toMillis(60);
            }
            if (points <= 24) {
                return TimeUnit.DAYS.toMillis(4);
            }
            if (points <= 26) {
                return TimeUnit.DAYS.toMillis(5);
            }
            if (points <= 28) {
                return TimeUnit.DAYS.toMillis(7);
            }
            if (points <= 30) {
                return TimeUnit.DAYS.toMillis(10);
            }
            if (points <= 32) {
                return TimeUnit.DAYS.toMillis(12);
            }
            if (points <= 35) {
                return TimeUnit.DAYS.toMillis(14);
            }
            if (points <= 38) {
                return TimeUnit.DAYS.toMillis(17);
            }
            if (points <= 40) {
                return TimeUnit.DAYS.toMillis(20);
            }
            if (points == 41) {
                return TimeUnit.DAYS.toMillis(21);
            }
            if (points <= 43) {
                return TimeUnit.DAYS.toMillis(26);
            }
            if (points <= 45) {
                return TimeUnit.DAYS.toMillis(30);
            }
            if (points <= 47) {
                return TimeUnit.DAYS.toMillis(40);
            }
            if (points == 48) {
                return TimeUnit.DAYS.toMillis(50);
            }
            if (points == 49) {
                return TimeUnit.DAYS.toMillis(60);
            }
            return 0;

        }
    }
}
