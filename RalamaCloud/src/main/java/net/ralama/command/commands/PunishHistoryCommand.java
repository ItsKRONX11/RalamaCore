package net.ralama.command.commands;

import net.ralama.Ralama;
import net.ralama.command.Command;
import net.ralama.message.Messages;
import net.ralama.packets.api.Constants;
import net.ralama.packets.api.Rank;
import net.ralama.player.RalamaPlayer;
import net.ralama.punishment.Punishment;
import net.ralama.punishment.PunishmentType;

import java.util.List;

public class PunishHistoryCommand extends Command {
    public PunishHistoryCommand() {
        super("punishhistory", Rank.HELPER, "punishistory", "baninfo");
    }

    @Override
    public void execute(RalamaPlayer sender, String[] args) {

        if (!(args.length >= 1)) {
            sender.sendMessage(Ralama.PREFIX + "Usage§8: §b/punishHistory <player>");
            return;
        }
        RalamaPlayer target = Ralama.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Messages.NEVER_JOINED);
            return;
        }
        List<Punishment> punishments = target.getPunishments().stream().filter(punishment -> punishment.getType() != PunishmentType.KICK).toList();
        if (punishments.isEmpty()) {
            sender.sendMessage(Ralama.PREFIX + "§cThis player has no punishments!");
            return;
        }
        StringBuilder builder = new StringBuilder(Ralama.PREFIX + target.getColoredName() + "'s §7punishments" + " §8(§a" + punishments.size() + "§8):");
        for (Punishment punishment : punishments) {
            String type = punishment.getType().name();
            String expires = punishment.getEnd() == 0 ? "§cexpires never" : Constants.formatDate(punishment.getEnd());
            String staff = punishment.getSender().getColoredName();
            String created = Constants.formatDate(punishment.getCreated());
            String reason = punishment.getReason();

            builder.append("\n§8» §e").append(type).append("§8, §e").append(created).append(" §8➟ §e").append(expires).append("\n§8➥ ").append(staff).append("§8, §e").append(reason);
        }
        sender.sendMessage(builder.toString());
    }
}
