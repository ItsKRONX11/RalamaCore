package net.ralama.lobby.scoreboard;

import net.ralama.packets.api.RemoteUser;
import net.ralama.spigot.RalamaSpigot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class ScoreboardManager {
    public void updateScoreboard(Player player) {
        RemoteUser user = RalamaSpigot.getInstance().getPlayer(player);

        player.getScoreboard().getTeam("playtime").setSuffix(TimeUnit.MILLISECONDS.toHours(user.getPlaytime()) + "h");
        player.getScoreboard().getTeam("rank").setSuffix(user.getRank().getDisplayName());
        player.getScoreboard().getTeam("online").setSuffix(Integer.toString(RalamaSpigot.getInstance().getGlobalOnline()));
        player.getScoreboard().getTeam("coins").setSuffix(Integer.toString(user.getCoins()));

        player.setLevel(RalamaSpigot.getInstance().getGlobalOnline());
    }

    public void updateScoreboard(String name) {
        Player player = Bukkit.getPlayer(name);
        if (player == null) return;
        this.updateScoreboard(player);
    }

    public void createBoard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective("board", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("§e§lMAIN LOBBY");

        objective.getScore("§7" + new SimpleDateFormat("dd/MM/yyyy").format(System.currentTimeMillis())).setScore(15);
        objective.getScore(" ").setScore(14);
        objective.getScore("§e§l»§6§l» §3§lInfo").setScore(13);

        Team name = scoreboard.registerNewTeam("name");
        name.addEntry(ChatColor.GREEN.toString());

        name.setPrefix("§3» §eName: §b");
        name.setSuffix(player.getName());
        objective.getScore(ChatColor.GREEN.toString()).setScore(12);

        Team playtime = scoreboard.registerNewTeam("playtime");
        playtime.addEntry(ChatColor.AQUA.toString());
        playtime.setPrefix("§3» §ePlaytime: §b");
        playtime.setSuffix(0 + "h");
        objective.getScore(ChatColor.AQUA.toString()).setScore(11);

        Team rank = scoreboard.registerNewTeam("rank");
        rank.addEntry(ChatColor.YELLOW.toString());
        rank.setPrefix("§3» §eRank: ");
        rank.setSuffix("?");
        objective.getScore(ChatColor.YELLOW.toString()).setScore(10);

        objective.getScore("  ").setScore(9);

        objective.getScore("§e§l»§6§l» §3§lServer").setScore(8);
        Team online = scoreboard.registerNewTeam("online");
        online.addEntry(ChatColor.BLACK.toString());
        online.setPrefix("§3» §eOnline: §b");
        online.setSuffix(Integer.toString(RalamaSpigot.getInstance().getGlobalOnline()));
        objective.getScore(ChatColor.BLACK.toString()).setScore(7);

        objective.getScore("   ").setScore(6);

        objective.getScore("§e§l»§6§l» §6§lRalaCoins").setScore(5);
        Team coins = scoreboard.registerNewTeam("coins");
        coins.addEntry(ChatColor.GOLD.toString());
        coins.setPrefix("§3» §eCoins: §b");
        coins.setSuffix(0 + "");
        objective.getScore(ChatColor.GOLD.toString()).setScore(4);

        objective.getScore("    ").setScore(3);
        objective.getScore("§7mc.ralama.net").setScore(2);

        player.setScoreboard(scoreboard);
        this.updateScoreboard(player);
    }
}
