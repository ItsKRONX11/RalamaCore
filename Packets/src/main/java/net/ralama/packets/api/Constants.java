package net.ralama.packets.api;

import net.ralama.packets.Packet;

import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class Constants {
    private static Consumer<Packet> packetConsumer;
    public static final String PREFIX = "§9§lRalama§r §7▸ ";
    public static final Random RANDOM = new Random();
    public static final String KICK_PREFIX = "§e◂ §c§lRalama §e▸";
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss");
    private Constants() {
    }

    public static String getBanKick(String reason, long ends, Language language) {
        if (language == Language.ROMANIAN) {
            return KICK_PREFIX +
                    "\n\n§e▸ §cAi fost §4§lbanat §cde pe acest server.\n§e▸ §cMotiv §8» §e" + reason +
                    "\n§e▸ §cSanctiunea Expira §8» §e" + ((ends == 0) ? "Niciodata" : formatDate(ends)) +
                    "\n\n§7▸ You can appeal on §3discord.ralama.net";
        } else {
            return KICK_PREFIX +
                    "\n\n§e▸ §cYou have been §4§lbanned §cfrom this server.\n§e▸ §cReason §8» §e" + reason +
                    "\n§e▸ §cPunishment Expires §8» §e" + ((ends == 0) ? "Never" : formatDate(ends)) +
                    "\n\n§7▸ Poti contesta ban-ul pe §3discord.ralama.net";
        }
    }

    public static String formatDate(long time) {
        return (time == 0) ? "§4never" : SIMPLE_DATE_FORMAT.format(time);
    }

    public static long getDuration(String duration) {
        if (duration.equalsIgnoreCase("perma") || duration.equalsIgnoreCase("perm") || duration.equalsIgnoreCase("-"))
            return 0;

        char unit = duration.charAt(duration.length() - 1);
        long value = Long.parseLong(duration.substring(0, duration.length() - 1));

        switch (unit) {
            case 'd':
                return System.currentTimeMillis() + TimeUnit.DAYS.toMillis(value);
            case 'h':
                return System.currentTimeMillis() + TimeUnit.HOURS.toMillis(value);
            case 'm':
                return System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(value);
            case 's':
                return System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(value);
            case 'y':
                return System.currentTimeMillis() + TimeUnit.DAYS.toMillis(value * 365);
            default:
                throw new IllegalArgumentException();
        }
    }

    public static String joinArgs(String[] args, int start) {
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            builder.append(args[i]);
            if (i != args.length-1) {
                builder.append(" ");
            }
        }
        return builder.toString();
    }

    public static void sendPacket(Packet packet) {
        packetConsumer.accept(packet);
    }
    public static void setPacketConsumer(Consumer<Packet> packetConsumer) {
        Constants.packetConsumer = packetConsumer;
    }

}
