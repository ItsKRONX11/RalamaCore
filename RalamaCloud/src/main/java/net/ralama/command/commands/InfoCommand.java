package net.ralama.command.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.ralama.Ralama;
import net.ralama.command.Command;
import net.ralama.message.Messages;
import net.ralama.packets.api.Constants;
import net.ralama.packets.api.Rank;
import net.ralama.player.RalamaPlayer;
import net.ralama.punishment.Punishment;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class InfoCommand extends Command {

    public InfoCommand() {
        super("info", Rank.HELPER);
    }

    private static Text[] getHover(Punishment punishment) {
        return new Text[]{
                new Text("§8» §7Reason: §a" + punishment.getReason()),
                new Text("\n§8» §7Type: §a" + punishment.getType().name()),
                new Text("\n§8» §7Expires: §a" + Constants.formatDate(punishment.getEnd())),
                new Text("\n§8» §7Created: §a" + Constants.formatDate(punishment.getCreated())),
                new Text("\n§8» §7Staff: §a" + punishment.getSender().getColoredName())
        };
    }

    @Override
    public void execute(RalamaPlayer sender, String[] args) {

        if (!(args.length >= 1)) {
            sender.sendMessage(Ralama.PREFIX + "Usage§8: §b/info <player> [su]");
            return;
        }
        RalamaPlayer target = Ralama.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(Messages.NEVER_JOINED);
            return;
        }

        ComponentBuilder builder = new ComponentBuilder(Ralama.PREFIX + "Information §8- " + target.getColoredName());
        builder.append("\n §8» §7Name: §b" + target.getName());
        builder.append("\n §8» §7Rank: " + target.getRank().getDisplayName() + ((target.getRankEnd() != 0) ? " §7[" + Constants.formatDate(target.getRankEnd()) + "]" : ""));
        builder.append("\n §8» §7Fallback rank: " + target.getFallback().getDisplayName());
        if (sender.getRank().equalsIsHigher(Rank.COORDINATOR) && !sender.getName().equals("KingBlah")) {
            builder.append("\n §8» §7IP Address: §b" + target.getIp());
        }
        builder.append("\n §8» §7Status: " + (target.isPremium() ? "§bPremium" : "§cCracked"));
        builder.append("\n §8» §7RalaCoins: §b" + target.getCoins());
        builder.append("\n §8» §7Language: §b" + target.getLanguage());
        builder.append("\n §8» §7Playtime: §b" + TimeUnit.MILLISECONDS.toHours(target.getPlayTime()) + "h §7-> §b" + TimeUnit.MILLISECONDS.toMinutes(target.getPlayTime()) + "m");
        builder.append("\n §8» §7Online status: ");
        if (target.isOnline()) {
            builder.append(new ComponentBuilder("online").color(ChatColor.GREEN)
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/jumpto " + target.getName()))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Proxy: §a" + target.getProxy().getName()), new Text("§7Server: §a" + target.getServer().getName())))
                    .create());
            if (!target.isPremium()) {
                builder.append("\n §8» §7Login status: " + (target.isLoggedIn() ? "§aLogged in" : "§cNot logged in"));
            }
        } else {
            builder.append("§coffline");
        }
        builder.append("\n §8» §7First joined: §b" + Constants.formatDate(target.getFirstJoined()));
        builder.append("\n §8» §7Last joined: §b" + Constants.formatDate(target.getLastJoined()));
        builder.append("\n §8» §7Discord ID: §b" + (target.isLinked() ? target.getDiscordId() : "§cNot linked"));
        builder.append("\n §8» §7Ban points / Mute points: §b" + target.getBanPoints() + " §7/ §b" + target.getMutePoints());
        builder.append("\n §8» §7Ban status: ");
        if (target.getBan() != null) {
            builder.append(new ComponentBuilder("banned").color(ChatColor.RED)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getHover(target.getBan()))).create());
        } else {
            builder.append("§bnot banned");
        }
        builder.append("\n §8» §7Mute status: ");
        if (target.getMute() != null) {
            builder.append(new ComponentBuilder("muted").color(ChatColor.RED)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getHover(target.getMute()))).create());
        } else {
            builder.append("§bnot muted");
        }

        if (args.length >= 2 && args[1].equalsIgnoreCase("su")) {
            List<RalamaPlayer> subAccounts = Ralama.getPlayerManager().getPlayers().stream().sorted(Comparator.comparingLong(RalamaPlayer::getLastJoined).reversed()).filter(t -> t.getIp().equals(target.getIp())).filter(t -> t != target).toList();

            ComponentBuilder subAccountBuilder = new ComponentBuilder("\n §8» §7Sub-accounts §8(§a" + subAccounts.size() + "§8):");

            for (RalamaPlayer player : subAccounts) {
                subAccountBuilder.append("\n §8» " + player.getColoredName() + " §8 - " + (player.isOnline() ? "§aonline" : "§coffline"));
                if (player.getBan() != null) {
                    subAccountBuilder.append(" §8- ").append(new ComponentBuilder("§cbanned").event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getHover(player.getBan()))).create());
                }
                if (player.getMute() != null) {
                    subAccountBuilder.append(" §8- ").append(new ComponentBuilder("§cmuted").event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getHover(player.getMute()))).create());
                }
            }
            builder.append(subAccountBuilder.create());
        }
        sender.sendMessage(builder.create());
    }
}
