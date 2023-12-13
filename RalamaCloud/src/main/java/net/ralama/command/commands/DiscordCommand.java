package net.ralama.command.commands;

import net.ralama.Ralama;
import net.ralama.command.Command;
import net.ralama.player.RalamaPlayer;

public class DiscordCommand extends Command {
    public final static String DISCORD_PREFIX = "§9§lDiscord §7▸ ";

    public DiscordCommand() {
        super("discord");
    }

    @Override
    public void execute(RalamaPlayer sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Ralama.PREFIX + DISCORD_PREFIX + "Usage §8(§b2§8):\n§8»§b/discord link §8- §9Link your account\n§8»§b/discord unlink §8- §9Unlink your account");
            return;
        }
    }
}
