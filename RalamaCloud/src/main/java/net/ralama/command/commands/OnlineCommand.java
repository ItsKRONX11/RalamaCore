package net.ralama.command.commands;

import net.ralama.Ralama;
import net.ralama.command.Command;
import net.ralama.player.RalamaPlayer;

public class OnlineCommand extends Command {
    public OnlineCommand() {
        super("online");
    }

    @Override
    public void execute(RalamaPlayer sender, String[] args) {
        int online = Ralama.getPlayerManager().getOnlinePlayers().size();
        sender.sendMessage(Ralama.PREFIX + "There are §b" + online + " §7players online.",
                Ralama.PREFIX + "Momentan sunt §b" + online + " §7jucatori conectati.");
    }
}
