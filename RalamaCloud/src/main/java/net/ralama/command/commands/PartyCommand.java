package net.ralama.command.commands;


import net.ralama.Ralama;
import net.ralama.command.Command;
import net.ralama.message.MessageGroup;
import net.ralama.message.Messages;
import net.ralama.packets.api.Message;
import net.ralama.packets.api.Rank;
import net.ralama.player.RalamaPlayer;
import net.ralama.player.multi.Party;

import java.util.List;

public class PartyCommand extends Command {
    public PartyCommand() {
        super("party");
    }
    private static final Message NOT_IN_PARTY = new Message(Party.PARTY_PREFIX + "§cYou are not a in party!", Party.PARTY_PREFIX + "§cNu esti intr-un party!");
    private static final Message NOT_LEADER = new Message(Party.PARTY_PREFIX + "§cYou are not allowed to do this!", Party.PARTY_PREFIX + "§cNu ai voie sa faci asta!");
    private static final Message ALREADY_IN_PARTY = new Message(Party.PARTY_PREFIX + "§cYou are already in a party!", Party.PARTY_PREFIX + "§cEsti deja intr-un party!");
    private static final Message PLAYER_NOT_IN_PARTY = new Message(Party.PARTY_PREFIX + "§cThis player is not in a party!", Party.PARTY_PREFIX + "§cAcest jucator nu este intr-un party!");

    @Override
    public void execute(RalamaPlayer sender, String[] args) {
        if (args.length == 0) {
            MessageGroup group = new MessageGroup();
            group.add("§8» §d/party create §8- §5").add("Create a party", "Creeaza un party");
            group.add("§8» §d/party leave §8- §5").add("Leave your party", "Iesi dintr-un party");
            group.add("§8» §d/party invite <player> §8- §5").add("Invite a player", "Invita un jucator");
            group.add("§8» §d/party kick <player> §8- §5").add("Kick a player from your party", "Da kick unui jucator din party");
            group.add("§8» §d/party list §8- §5").add("List party members","Listeaza jucatorii din party");
            group.add("§8» §d/party join <player> §8- §5").add("Join a party", "Intra intr-un party");
            group.add("§8» §d/party toggle §8- §5").add("Toggle your party's open status", "Comuta publicitatea party-ului");
            group.add("§8» §d/party leader <player> §8- §5").add("Change the party leader", "Schimba liderul party-ului");

            sender.sendMessage(group.insert(Ralama.PREFIX + Party.PARTY_PREFIX + "Usage §8(§d" + group.size() + "§8):"));
            return;
        }
        switch (args[0].toLowerCase()) {
            case "create" -> {
                if (sender.getParty() != null) {
                    sender.sendMessage(Party.PARTY_PREFIX + "§cYou are already in a party!", Party.PARTY_PREFIX + "§cEsti deja intr-un party!");
                    return;
                }
                new Party(sender);
            }
            case "leave" -> {
                if (sender.getParty() == null) {
                    sender.sendMessage(NOT_IN_PARTY);
                    return;
                }
                Party party = sender.getParty();
                party.memberLeave(sender);
                sender.sendMessage(Party.PARTY_PREFIX + "§cYou have left the party!", Party.PARTY_PREFIX + "§cAi iesit din party!");
                party.sendMessage(sender.getColoredName() + " §7has left the party.", sender.getColoredName() + " §7a iesit din party.");
            }
            case "invite" -> {
                if (args.length < 2) {
                    sender.sendMessage(Ralama.PREFIX + "Usage§8: §d/party invite <player>");
                    return;
                }
                RalamaPlayer target = Ralama.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(Messages.NEVER_JOINED);
                    return;
                }
                if (!target.isOnline()) {
                    sender.sendMessage(Messages.NOT_ONLINE);
                    return;
                }

                if (sender.getParty() != null) {
                    if (!sender.getParty().isLeader(sender)) {
                        sender.sendMessage(NOT_LEADER);
                        return;
                    }
                    if (sender.getParty().getInvites().contains(target)) {
                        sender.sendMessage(Party.PARTY_PREFIX, "§cThis player was already invited!", "§cJucatorul respectiv a fost deja invitat!");
                        return;
                    }
                    if (sender.getParty().getPlayers().contains(target)) {
                        sender.sendMessage(Party.PARTY_PREFIX, "§cThis player is already in the party!", "§cJucatorul respectiv este deja in party!");
                        return;
                    }
                } else {
                    new Party(sender);
                }
                sender.getParty().invite(target);
            }
            case "join" -> {
                if (args.length < 2) {
                    sender.sendMessage(Ralama.PREFIX + "Usage§8: §d/party join <player>");
                    return;
                }
                if (sender.getParty() != null) {
                    sender.sendMessage(ALREADY_IN_PARTY);
                    return;
                }
                RalamaPlayer target = Ralama.getPlayer(args[1]);

                if (Ralama.checkPlayer(sender, target)) return;

                Party party = target.getParty();

                if (party == null) {
                    sender.sendMessage(PLAYER_NOT_IN_PARTY);
                    return;
                }

                if (!(party.isOpen() || sender.getRank().equalsIsHigher(Rank.MODERATOR) || party.getInvites().contains(sender))) {
                    sender.sendMessage(Party.PARTY_PREFIX, "§cYou can't join this party!", "§cNu poti intra in acest party!");
                    return;
                }

                party.add(sender);
            }
            case "toggle" -> {
                if (sender.getParty() == null) {
                    sender.sendMessage(NOT_IN_PARTY);
                    return;
                }
                if (!sender.getParty().isLeader(sender)) {
                    sender.sendMessage(NOT_LEADER);
                    return;
                }
                sender.getParty().setOpen(!sender.getParty().isOpen());
                if (sender.getParty().isOpen()) {
                    sender.sendMessage(Party.PARTY_PREFIX, "§aThe party is now open.", "§aParty-ul este acum public.");
                } else {
                    sender.sendMessage(Party.PARTY_PREFIX, "§cThe party is now closed", "§cParty-ul este acum privat.");
                }
            }
            case "leader" -> {
                if (args.length < 2) {
                    sender.sendMessage(Party.PARTY_PREFIX + "Usage§8: §d/party leader <player>");
                    return;
                }
                if (sender.getParty() == null) {
                    sender.sendMessage(NOT_IN_PARTY);
                    return;
                }
                if (!sender.getParty().isLeader(sender)) {
                    sender.sendMessage(NOT_LEADER);
                    return;
                }
                RalamaPlayer target = Ralama.getPlayer(args[1]);

                if (Ralama.checkPlayer(sender, target)) return;

                if (target.getParty() != sender.getParty()) {
                    sender.sendMessage(Party.PARTY_PREFIX, "§cThis player is not in your party!", "§cAcest jucator nu este in party-ul tau.");
                    return;
                }

                sender.getParty().setLeader(target);
            }
            case "kick" -> {
                if (args.length < 2) {
                    sender.sendMessage(Party.PARTY_PREFIX + "Usage§8: §d/party kick <player>");
                    return;
                }
                if (sender.getParty() == null) {
                    sender.sendMessage(NOT_IN_PARTY);
                    return;
                }
                if (!sender.getParty().isLeader(sender)) {
                    sender.sendMessage(NOT_LEADER);
                    return;
                }
                RalamaPlayer target = Ralama.getPlayer(args[1]);

                if (Ralama.checkPlayer(sender, target)) return;

                if (target == sender) {
                    sender.sendMessage(Party.PARTY_PREFIX, "§cYou can't kick yourself!", "Nu iti poti da kick singur!");
                }

                if (target.getParty() != sender.getParty()) {
                    sender.sendMessage(Party.PARTY_PREFIX, "§cThis player is not in your party!", "§cAcest jucator nu este in party-ul tau.");
                    return;
                }

                sender.getParty().memberLeave(target);

                sender.getParty().sendMessage(Party.PARTY_PREFIX + target.getColoredName() + " §7was kicked from the party!", Party.PARTY_PREFIX + target.getColoredName() + " §7a primit kick din party!");
            }
            case "list" -> {
                if (sender.getParty() == null) {
                    sender.sendMessage(NOT_IN_PARTY);
                    return;
                }
                Party party = sender.getParty();
                List<RalamaPlayer> players = sender.getParty().getPlayers().stream().filter(t -> !party.isLeader(t)).toList();

                StringBuilder builder = new StringBuilder()
                        .append(Ralama.PREFIX + Party.PARTY_PREFIX).append("§dParty info:")
                        .append("\n§8» §7").append("Leader: ").append(party.getLeader().getColoredName())
                        .append("\n§8» §7").append("Open: ").append(party.isOpen() ? "§a■" : "§c■")
                        .append("\n§8» §7").append("Members: ").append("§8(§d" + players.size() + "§8):");
                for (RalamaPlayer player : players) {
                    builder.append("\n §8- " + player.getColoredName());
                }
                sender.sendMessage(builder.toString());
            }
            case "info" -> this.execute(sender, new String[]{"list"});

            default -> sender.sendMessage(Messages.UNKNOWN_COMMAND);
        }
    }
}
