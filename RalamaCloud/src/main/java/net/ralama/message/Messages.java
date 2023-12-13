package net.ralama.message;

import net.ralama.Ralama;
import net.ralama.packets.api.Message;

public final class Messages {
    public static final Message NO_PERMISSION = new Message(Ralama.PREFIX + "§cYou do not have permission to perform this action!", Ralama.PREFIX + "§cNu ai permisiune sa efectuezi aceasta comanda!");
    public static final Message NEVER_JOINED = new Message(Ralama.PREFIX + "§cThis user never joined the network!", Ralama.NEVER_JOINED + "§cAcest jucator nu a intrat vreodata pe server!");
    public static final Message NOT_ONLINE = new Message(Ralama.PREFIX + "That user is not online.", Ralama.PREFIX + "§cAcest jucator nu este online.");
    public static final Message LANGUAGE_CHANGE = new Message(Ralama.PREFIX + "Your language was changed to §bEnglish§7.", Ralama.PREFIX + "Limba ta a fost schimbata in §bRomana§7.");
    public static final Message RANK_CHANGE = new Message(Ralama.PREFIX + "Your rank has been changed to %rank%§7.\n" + Ralama.PREFIX + "Expires: §e%expires%",
            Ralama.PREFIX + "Rank-ul tau a fost schimbat la %rank%§7.\n" + Ralama.PREFIX + "Expira: §e%expires%");
    public static final Message BAN_ALERT = new Message(
            Ralama.WARN_PREFIX + "%player% §7was banned by %staff%§7.\n" +
                    Ralama.WARN_PREFIX + "Reason§8: §e%reason%\n" +
                    Ralama.WARN_PREFIX + "Expires§8: §e%expires%\n" +
                    Ralama.WARN_PREFIX + "Comment§8: §e%comment%",

            Ralama.WARN_PREFIX + "%player% §7a fost banat de catre %staff%§7.\n" +
                    Ralama.WARN_PREFIX + "Motiv§8: §e%reason%\n" +
                    Ralama.WARN_PREFIX + "Expira§8: §e%expires%\n" +
                    Ralama.WARN_PREFIX + "Comentariu§8: §e%comment%");
    public static final Message KICK_ALERT = new Message(
            Ralama.WARN_PREFIX + "%player% §7was kicked by %staff%§7.\n" +
                    Ralama.WARN_PREFIX + "Reason§8: §e%reason%",

            Ralama.WARN_PREFIX + "%player% §7a primit kick de la %staff%§7." +
                    Ralama.WARN_PREFIX + "Motiv§8: §e%reason%");
    public static final Message MUTE_ALERT = new Message(
            Ralama.WARN_PREFIX + "%player% §7was muted by %staff%§7.\n" +
                    Ralama.WARN_PREFIX + "Reason§8: §e%reason%\n" +
                    Ralama.WARN_PREFIX + "Expires§8: §e%expires%\n" +
                    Ralama.WARN_PREFIX + "Comment§8: §e%comment%",

            Ralama.WARN_PREFIX + "%player% §7a primit mute de la %staff%§7.\n" +
                    Ralama.WARN_PREFIX + "Motiv§8: §e%reason%\n" +
                    Ralama.WARN_PREFIX + "Expira§8: §e%expires%\n" +
                    Ralama.WARN_PREFIX + "Comentariu§8: §e%comment%");

    public static final Message UNKNOWN_COMMAND = new Message(
            Ralama.PREFIX + "§cUnknown command.",
            Ralama.PREFIX + "§cComanda necunoscuta."
    );

    private Messages() {
    } // static class
}
