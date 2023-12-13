package net.ralama.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.ralama.Ralama;
import net.ralama.player.RalamaPlayer;

import java.awt.*;

public class LinkCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if (!e.getName().equalsIgnoreCase("link")) return;

        if (Ralama.getPlayerManager().getPlayerById(e.getUser().getIdLong()) != null) {
            e.reply("You are already linked!").setEphemeral(true).queue();
            return;
        }
        String code = e.getOption("code").getAsString();

        if (!Ralama.getDiscordManager().getVerificationCodes().asMap().containsKey(code)) {
            e.replyEmbeds(new EmbedBuilder().setColor(Color.RED).addField(
                    "<:Ralama:1136257888378163200> Ralama Account Linking", "``The code " + code + " does not exist! Please try again, or run /discord link in-game.``", false).build()).setEphemeral(true).queue();
            return;
        }
        RalamaPlayer player = Ralama.getPlayerManager().getPlayerByName(
                Ralama.getDiscordManager().getVerificationCodes().asMap().get(code)
        );
        player.setDiscordId(e.getUser().getIdLong());
        player.sendMessage(Ralama.PREFIX + "§aYou have verified your discord.");
        e.reply("You have verified yourself, **" + Ralama.getDiscordManager().getVerificationCodes().asMap().get(code) + "**. Welcome!").queue();
        Ralama.getDiscordManager().getVerificationCodes().asMap().remove(code);
    }
}


