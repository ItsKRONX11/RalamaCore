package net.ralama.discord;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.ralama.packets.api.Constants;
import net.ralama.player.RalamaPlayer;
import net.ralama.punishment.PunishmentType;

import java.util.concurrent.TimeUnit;

public class DiscordManager extends ListenerAdapter {
    public static final long GUILD_ID = 535086984142651393L;
    public static final long WARNS_CHANNEL_ID = 1107055312504102982L;
    private static boolean started;
    @Getter
    private JDA jda;
    @Getter
    private Cache<String, String> verificationCodes;

    public DiscordManager() {
        if (started) {
            return;
        }
        started = true;
        JDABuilder builder = JDABuilder.createDefault("MTExNDE3MDYyNDQ4NzcyNzE2NA.GaweZW.-0jFZjsRdlWPF1R4S4bCQvYYMl-dBGnPTyA3hY");
        builder.setActivity(Activity.playing("mc.ralama.net"));
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
        builder.addEventListeners(this);
        builder.addEventListeners(new LinkCommand());
        jda = builder.build();
        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verificationCodes = CacheBuilder.newBuilder().expireAfterWrite(60, TimeUnit.SECONDS).build();
    }

    public Guild getGuild() {
        return jda.getGuildById(GUILD_ID);
    }

    public TextChannel getTextChannelById(long id) {
        return getGuild().getTextChannelById(id);
    }

    @Override
    public void onReady(@NonNull ReadyEvent e) {
        getGuild().updateCommands().addCommands(
                Commands.slash("link", "Link your discord account to your minecraft account.")
                        .addOption(OptionType.STRING, "code", "Your verification code. Run /discord link in-game to get it", true)
        ).queue();

    }

    public void sendPunishmentEmbed(RalamaPlayer player, RalamaPlayer from, long expires, String comment, String reason, PunishmentType type) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(type + " INFORMATION");
        embedBuilder.addField("**Player:**", "``" + player.getName() + "``", false);
        embedBuilder.addField("**Staffmember:**", "``" + from.getName() + "``", false);
        if (!reason.equals("MANUAL")) {
            embedBuilder.addField("**Reason:**", reason, false);
        }
        if (expires != 0) {
            embedBuilder.addField("**Ban end:**", Constants.formatDate(expires), false);
        } else {
            embedBuilder.addField("**Ban end:**", "Never", false);
        }
        if (player.isOnline()) {
            embedBuilder.addField("**Server:**", player.getServer().getName(), false);
        }
        if (comment != null) {
            embedBuilder.addField("**Comment:**", comment, false);
        }
        embedBuilder.setThumbnail("https://minotar.net/helm/" + player.getName() + "/64.png ");

        getTextChannelById(WARNS_CHANNEL_ID).sendMessageEmbeds(embedBuilder.build()).queue();
    }
}
