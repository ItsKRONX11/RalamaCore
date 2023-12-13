package net.ralama.command.commands;

import net.ralama.Ralama;
import net.ralama.command.Command;
import net.ralama.packets.api.Rank;
import net.ralama.player.RalamaPlayer;

public class NotifyCommand extends Command {
    public NotifyCommand() {
        super("notify", Rank.HELPER);
    }

    @Override
    public void execute(RalamaPlayer sender, String[] args) {

        if (sender.isNotify()) {
            sender.sendMessage(Ralama.PREFIX + "§aYou have enabled staff notifications.",
                    Ralama.PREFIX + "§aAi activat notificarile de staff.");
        } else {
            sender.sendMessage(Ralama.PREFIX + "§cYou have disabled staff notifications.",
                    Ralama.PREFIX + "§cAi dezactivat notificarile de staff.");
        }
        sender.setNotify(!sender.isNotify());
    }
}
