package net.ralama.command.commands;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.ralama.Ralama;
import net.ralama.command.Command;
import net.ralama.message.ChatMessage;
import net.ralama.message.Messages;
import net.ralama.packets.api.Rank;
import net.ralama.player.RalamaPlayer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class ChatlogCommand extends Command {
    public static final String URL = "http://chatlog.ralama.net/";
    public ChatlogCommand() {
        super("chatlog", Rank.OWNER);
    }
    private final Cache<UUID, Integer> cooldown = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).build();
    @Override
    public void execute(RalamaPlayer sender, String[] args) throws Exception{
        if (args.length < 1) {
            sender.sendMessage(Ralama.PREFIX + "Usage§8: §b/chatlog <player>");
            return;
        }
        RalamaPlayer target = Ralama.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Messages.NEVER_JOINED);
            return;
        }
        if (target.getMessages().isEmpty()) {
            sender.sendMessage(Ralama.PREFIX + "§cThis player hasn't written anything!");
            return;
        }
        if (!sender.isStaff()) {
            if (this.cooldown.asMap().containsKey(sender.getUuid())) {
                sender.sendMessage(Ralama.PREFIX + "§cPlease wait a little bit...");
                return;
            }
            this.cooldown.put(sender.getUuid(), 0);
        }
        String id = uploadChatlog(target, sender);
        sender.sendMessage(
                new ComponentBuilder(Ralama.PREFIX + "The chatlog has been uploaded.\n"+Ralama.PREFIX + "Click here§8: ").append(new ComponentBuilder("§b§l"+id).event(new ClickEvent(ClickEvent.Action.OPEN_URL, URL + id)).create()).create()
        );
    }
    private static String randomId(){
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder idBuilder = new StringBuilder();

        for (int i = 0; i < 10; i++){
            idBuilder.append(chars.charAt(ThreadLocalRandom.current().nextInt(chars.length())));
        }

        return idBuilder.toString();
    }
    /** Uploads a chatlog for the specified player and returns the chatlog's id. */
    public static String uploadChatlog(RalamaPlayer player, RalamaPlayer requester) throws SQLException{
        long now = System.currentTimeMillis();
        String id = randomId();

        if (player.getMessages().isEmpty()) return null;

        for (ChatMessage message : player.getMessages()) {

            if ((System.currentTimeMillis() - message.getSent()) <= TimeUnit.MINUTES.toMillis(15)) {
                PreparedStatement ps = Ralama.getDatabase().getConnection().prepareStatement(
                        "INSERT INTO chatlogs(ID,PLAYER,SERVER,MESSAGE,SENT,CREATED,BY_WHO) VALUES (?,?,?,?,?,?,?);"
                );
                ps.setString(1, id);
                ps.setString(2, message.getSender());
                ps.setString(3, message.getSender());
                ps.setString(4, message.getMessage());
                ps.setLong(5, message.getSent());
                ps.setLong(6, now);
                ps.setString(7, requester.getName());
                ps.executeUpdate();
            }

        }

        return id;
    }

}
