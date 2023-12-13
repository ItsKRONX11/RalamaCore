package net.ralama.player.multi;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.ralama.Ralama;
import net.ralama.player.RalamaPlayer;
import net.ralama.server.RalamaServer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Party {
    public static final String PARTY_PREFIX = "§5§lParty §7▸ ";
    private final List<String> players = new ArrayList<>();
    private final List<String> invites = new ArrayList<>();
    private final List<String> banned = new ArrayList<>();
    @Getter
    private RalamaPlayer leader;
    @Getter
    @Setter
    private boolean open;

    public Party(RalamaPlayer leader) {
        this.leader = leader;
        this.players.add(leader.getName());
        leader.setParty(this);

        leader.sendMessage(PARTY_PREFIX, "§aYou have created a party.", "§aAi creat un party.");
    }

    public void sendMessage(String message) {
        getPlayers().forEach(t -> t.sendMessage(PARTY_PREFIX + message));
    }
    public void sendMessage(String en, String ro) {
        getPlayers().forEach(t -> t.sendMessage(PARTY_PREFIX, en, ro));
    }

    public void setLeader(RalamaPlayer leader) {
        if (this.leader.equals(leader)) {
            leader.sendMessage(PARTY_PREFIX + "§cYou are already leader.");
        }
        this.leader = leader;
        sendMessage(leader.getColoredName() + " §7is now party leader.", leader.getColoredName() + " §7este acum lider.");
    }

    public boolean isLeader(RalamaPlayer player) {
        return this.leader.equals(player);
    }

    public void switchServer(RalamaServer server) {
        for (RalamaPlayer player : getPlayers()) {
            if (player.getServer() == server) continue;

            player.connect(server);
            player.sendMessage(PARTY_PREFIX, "Party switching to §d" + server.getName() + "§7.", "Party-ul intra pe §d" + server.getName() + "§7.");
        }
    }

    public void invite(RalamaPlayer player) {
        if (this.invites.contains(player.getName())) return;
        if (this.players.contains(player.getName())) return;
        if (!player.isOnline()) return;

        this.invites.add(player.getName());

        player.sendMessage(
                new ComponentBuilder(PARTY_PREFIX + leader.getColoredName() + " §7has invited you to their party!\n"
                        + PARTY_PREFIX).append("§a§nClick here to accept").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party join " + this.leader.getName()))
                        .create());
    }

    public void memberLeave(RalamaPlayer player) {
        this.players.remove(player.getName());
        player.setParty(null);

        if (this.players.size() == 1 || this.players.size() == 0) {
            this.disband();
            return;
        }

        if (isLeader(player)) {
            this.setLeader(getPlayers().get(ThreadLocalRandom.current().nextInt(this.players.size())));
        }
    }
    public void add(RalamaPlayer player) {
        this.invites.remove(player.getName());
        this.players.add(player.getName());
        player.setParty(this);
        this.sendMessage(player.getColoredName() + " §7has joined the party!", player.getColoredName() + " §7a intrat in party!");
    }

    public void disband() {
        sendMessage("§cThe party has been disbanded!");

        getPlayers().forEach(player -> player.setParty(null));
        players.clear();
        invites.clear();
    }

    public List<RalamaPlayer> getPlayers() {
        return this.players.stream().map(Ralama::getPlayer).toList();
    }

    public List<RalamaPlayer> getInvites() {
        return this.invites.stream().map(Ralama::getPlayer).toList();
    }
    public List<String> getPlayersRaw() {
        return this.players;
    }
    public List<String> getInvitesRaw() {
        return this.invites;
    }
}
