package net.ralama.command.commands;

import net.ralama.Ralama;
import net.ralama.command.Command;
import net.ralama.packets.api.Message;
import net.ralama.player.RalamaPlayer;

import java.util.Comparator;
import java.util.List;

public class ListCommand extends Command {
    private static final Message message = new Message(
            Ralama.PREFIX + "There are §b§l%amount% §7players on your server.\n" +
                    Ralama.PREFIX + "Online donors: %donors%",

            Ralama.PREFIX + "Sunt §b§l%amount% §7jucatori conectati pe serverul tau.\n" +
                    Ralama.PREFIX + "Donatori conectati: %donors%");

    public ListCommand() {
        super("list");
    }

    @Override
    public void execute(RalamaPlayer sender, String[] args) {
        if (sender.getServer() == null) return;

        List<RalamaPlayer> donorList = sender.getServer().getPlayers().stream()
                .map(Ralama::getPlayer)
                .filter(player -> player.hasRank() && player.getRank().getAccessLevel() < 75)
                .sorted(Comparator.comparingInt(RalamaPlayer::getAccesLevel).reversed()).toList();

        String donors;
        if (!donorList.isEmpty()) {
            StringBuilder donorBuilder = new StringBuilder();
            for (RalamaPlayer player : donorList) {
                donorBuilder.append(player.getColoredName());
                if (donorList.indexOf(player) != donorList.size() - 1) {
                    donorBuilder.append("§7, ");
                }
            }
            donors = donorBuilder.toString();
        } else {
            donors = "§cNone ;(";
        }

        sender.sendMessage(message.replaced("%amount%", Integer.toString(sender.getServer().getPlayers().size())).replaced("%donors%", donors));
    }
}
