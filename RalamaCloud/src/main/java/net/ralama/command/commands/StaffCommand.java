package net.ralama.command.commands;

import net.ralama.Ralama;
import net.ralama.command.Command;
import net.ralama.packets.api.Rank;
import net.ralama.player.RalamaPlayer;

import java.util.Comparator;
import java.util.List;

public class StaffCommand extends Command {
    public StaffCommand() {
        super("staff", Rank.HELPER, "team");
    }

    @Override
    public void execute(RalamaPlayer sender, String[] args) {
        boolean online = args.length >= 1 && (args[0].equalsIgnoreCase("online") || args[0].equalsIgnoreCase("on"));
        List<RalamaPlayer> staff = Ralama.getPlayerManager().getRankPlayers().stream()
                .map(Ralama::getPlayer)
                .filter(RalamaPlayer::isStaff)
                .sorted(Comparator.comparingInt(RalamaPlayer::getAccesLevel).reversed())
                .toList();

        StringBuilder builder = new StringBuilder(Ralama.PREFIX + "Staff members §8(§a" + (online ? staff.stream().filter(RalamaPlayer::isOnline).toList().size() : staff.size()) + "§8):");

        for (RalamaPlayer player : staff) {
            if (player.isOnline() || !online)
                builder.append("\n§7[").append(player.isOnline() ? "§a" : "§c").append("X§7] ").append(player.getColoredName()).append(" §7[").append(player.getRank().getDisplayName()).append("§7]").append(player.isNotify() ? " §5✖" : "");
        }
        sender.sendMessage(builder.toString());
    }
}
