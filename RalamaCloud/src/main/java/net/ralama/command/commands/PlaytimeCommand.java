package net.ralama.command.commands;

import net.ralama.Ralama;
import net.ralama.command.Command;
import net.ralama.player.RalamaPlayer;

import java.util.concurrent.TimeUnit;

public class PlaytimeCommand extends Command {
    public PlaytimeCommand() {
        super("playtime", "onlinetime");
    }

    @Override
    public void execute(RalamaPlayer sender, String[] args) {
        String hours = TimeUnit.MILLISECONDS.toHours(sender.getPlayTime()) + "h";
        String minutes = TimeUnit.MILLISECONDS.toMinutes(sender.getPlayTime()) + "m";

        sender.sendMessage(Ralama.PREFIX,
                "Your playtime: §b" + hours + " §8<-> §b" + minutes,
                "Timpul jucat: §b" + hours + " §8<-> §b" + minutes);
    }
}
