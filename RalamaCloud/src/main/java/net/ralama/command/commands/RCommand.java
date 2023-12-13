package net.ralama.command.commands;

import net.ralama.Ralama;
import net.ralama.command.Command;
import net.ralama.player.RalamaPlayer;

public class RCommand extends Command {
    public RCommand() {
        super("r", "reply");
    }

    @Override
    public void execute(RalamaPlayer sender, String[] args) throws Exception {
        if (args.length < 1) {
            sender.sendMessage(Ralama.PREFIX + "Usage§8: §b/r <message>");
            return;
        }
        if (sender.getLastSender() == null) {
            sender.sendMessage(Ralama.PREFIX + "§cNo one has messaged you!",
                    Ralama.PREFIX + "§cNimeni nu ti-a dat mesaj!");
            return;
        }
        String[] arr = new String[args.length + 1];

        arr[0] = sender.getLastSender().getName();

        System.arraycopy(args, 0, arr, 1, args.length);

        Ralama.getCommandManager().getCommand("msg").execute(sender, arr);
    }
}
