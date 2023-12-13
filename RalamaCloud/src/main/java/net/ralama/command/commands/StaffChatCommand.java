package net.ralama.command.commands;

import net.ralama.Ralama;
import net.ralama.command.Command;
import net.ralama.packets.api.Rank;
import net.ralama.player.RalamaPlayer;

public class StaffChatCommand extends Command {
    public StaffChatCommand() {
        super("tc", Rank.HELPER, "a");
    }

    @Override
    public void execute(RalamaPlayer sender, String[] args) {
        if (!(args.length >= 1)) {
            sender.sendMessage(Ralama.PREFIX + "Usage§8: §b/tc <message>");
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (String s : args) builder.append(s).append(" ");

        Ralama.getPlayerManager().sendStaffMessage("§6§k| §8[§c§o" + sender.getServer() + "§8] §8[§c§lS§8] " + sender.getColoredName() + "§7: §e" + builder);
    }
}
