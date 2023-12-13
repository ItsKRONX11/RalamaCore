package net.ralama.command.commands;

import net.ralama.Ralama;
import net.ralama.command.Command;
import net.ralama.message.MessageGroup;
import net.ralama.message.Messages;
import net.ralama.packets.api.Rank;
import net.ralama.player.RalamaPlayer;

import java.sql.SQLException;
import java.util.List;

public class FilterCommand extends Command {
    public static String FILTER_PREFIX = "§3§lFilter §7▸ ";

    public FilterCommand() {
        super("filter", Rank.HELPER);
    }

    @Override
    public void execute(RalamaPlayer sender, String[] args) {

        if (args.length == 0) {
            MessageGroup group = new MessageGroup();
            group.add("§8» §b/filter list §8- §3").add("List filter entries", "Listeaza cuvintele interzise");
            if (sender.getRank().equalsIsHigher(Rank.MANAGER)) {
                group.add("§8» §b/filter add <word> §8- §3").add("Add a word to the filter", "Adauga un cuvant in filtru")
                        .add("§8» §b/filter remove <word> §8- §3").add("Remove a word from the filter", "Scoate un cuvant din filtru");
            }
            if (sender.getRank().equalsIsHigher(Rank.SRDEVELOPER)) {
                group.add("§8» §b/filter reload §8- §3").add("Reload the filter entries", "Reincarca cuvintele interzise");
            }
            sender.sendMessage(group.insert(Ralama.PREFIX + "§3§lFilter §8- §7Usage §8(§b" + group.size() + "§8):"));
            return;
        }

        List<String> entries = Ralama.getFilterManager().getEntries().stream().sorted().toList();

        switch (args[0].toLowerCase()) {
            case "list" -> {
                StringBuilder entryBuilder = new StringBuilder(Ralama.PREFIX + FILTER_PREFIX + "Entries §8(§b" + entries.size() + "§8):\n");

                for (String entry : entries) {
                    entryBuilder.append("§7").append(entry);
                    if (entries.indexOf(entry) != entries.size() - 1) {
                        entryBuilder.append("§8, ");
                    }
                }
                sender.sendMessage(entryBuilder.toString());
            }
            case "reload" -> {
                if (!sender.getRank().equalsIsHigher(Rank.SRDEVELOPER)) {
                    sender.sendMessage(Messages.NO_PERMISSION);
                    return;
                }
                try {
                    Ralama.getFilterManager().loadEntries();
                    sender.sendMessage(FILTER_PREFIX, "§aThe entries have been reloaded successfully.", "§aCuvintele interzise au fost reincarcate cu succes.");
                } catch (SQLException e) {
                    sender.sendMessage(FILTER_PREFIX, "§cThere was an error whilst loading the entries.", "§cA aparut o eroare la reincarcarea cuvintelor.");
                    e.printStackTrace();
                }
            }
            case "add" -> {
                if (!sender.getRank().equalsIsHigher(Rank.MANAGER)) {
                    sender.sendMessage(Messages.NO_PERMISSION);
                    return;
                }
                if (args.length < 2) {
                    sender.sendMessage(Ralama.PREFIX + "Usage: §3/filter add <word>");
                    return;
                }
                String word = args[1].toLowerCase();
                if (entries.contains(word)) {
                    sender.sendMessage(Ralama.PREFIX, "§cThe word " + word + " is already in the filter!", "§cCuvantul " + word + " este deja in filtru!");
                    return;
                }
                Ralama.getFilterManager().add(word);
                sender.sendMessage(Ralama.PREFIX, "The word §b" + word + " §7has been added to the filter.", "Cuvantul §b" + word + " §7a fost adaugat in filtru.");
            }
            case "remove" -> {
                if (!sender.getRank().equalsIsHigher(Rank.MANAGER)) {
                    sender.sendMessage(Messages.NO_PERMISSION);
                    return;
                }
                if (args.length < 2) {
                    sender.sendMessage(Ralama.PREFIX + "Usage: §3/filter remove <word>");
                    return;
                }
                String word = args[1].toLowerCase();
                if (!entries.contains(word)) {
                    sender.sendMessage(Ralama.PREFIX, "§cThe word " + word + " is not in the filter!", "§cCuvantul " + word + " §cnu este in filtru!");
                    return;
                }
                Ralama.getFilterManager().remove(word);
                sender.sendMessage(Ralama.PREFIX, "The word §b" + word + " has been removed from the filter.", "Cuvantul §b" + word + " §7a fost scos din filtru.");
            }
            default -> sender.sendMessage(Ralama.UNKNOWN_COMMAND);
        }
    }
}
