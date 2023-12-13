package net.ralama.command.commands;

import net.ralama.Ralama;
import net.ralama.command.Command;
import net.ralama.player.RalamaPlayer;

public class WhereAmICommand extends Command {
    public WhereAmICommand() {
        super("whereami", "wai");
    }

    @Override
    public void execute(RalamaPlayer sender, String[] args) {
        if (!sender.isOnline()) return;

        sender.sendMessage(
                Ralama.PREFIX + "You are connected to: §b" + sender.getServer().getName() + " §7with ID §b" + sender.getServer().getServerId(),
                Ralama.PREFIX + "Esti conectat pe: §b" + sender.getServer().getName() + " §7cu ID-ul §b" + sender.getServer().getServerId());
    }
}
