package net.ralama.command.commands;

import net.ralama.Ralama;
import net.ralama.command.Command;
import net.ralama.packets.api.Constants;
import net.ralama.packets.api.Rank;
import net.ralama.player.RalamaPlayer;

public class CloudStatsCommand extends Command {
    public CloudStatsCommand() {
        super("cloudstats", Rank.DEVELOPER);
    }

    @Override
    public void execute(RalamaPlayer sender, String[] args) {

        int banned = 0;
        int muted = 0;
        for (String name : Ralama.getPlayerManager().getPunishedPlayers()) {
            RalamaPlayer player = Ralama.getPlayer(name);
            if (player.getBan() != null) {
                banned++;
            }
            if (player.getMute() != null) {
                muted++;
            }
        }
        sender.sendMessage(Ralama.PREFIX + "Global stats§8:" +
                "\n§8» §7RalamaCloud started: §b" + Constants.formatDate(Ralama.getInstance().getStarted()) +
                "\n§8» §7Total players: §a" + Ralama.getPlayerManager().getPlayers().size() +
                "\n §8» §7Online players: §b" + Ralama.getPlayerManager().getOnlinePlayers().size() +
                "\n §8» §7Banned players: §c" + banned +
                "\n §8» §7Muted players: §c" + muted +
                "\n §8» §7Premium player: §d" + Ralama.getPlayerManager().getPlayers().stream().filter(RalamaPlayer::isPremium).toList().size() +
                "\n §8» §7Players with rank: §e" + Ralama.getPlayerManager().getRankPlayers().size() +
                "\n §8» §7Players to-be-updated: §2" + Ralama.getPlayerManager().getQueuedPlayers().size() +
                "\n §8» §7Players linked with discord: " + Ralama.getPlayerManager().getDiscordIdPlayer().size());
    }
}
