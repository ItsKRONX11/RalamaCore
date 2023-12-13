package net.ralama.ralamaproxy.listener;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.ralama.packets.api.Constants;
import net.ralama.packets.api.Message;
import net.ralama.packets.api.RemoteUser;
import net.ralama.packets.out.PlayerMessagePacket;
import net.ralama.ralamaproxy.RalamaProxy;

public class ChatListener implements Listener {
    private static final Message NOT_LOGGED_IN = new Message(
            Constants.PREFIX + "§cYou are not logged in!",
            Constants.PREFIX + "§cNu esti logat!");

    private static final Message MUTE = new Message(
            Constants.PREFIX + "§cYou are muted for §e%reason%§c.\n" +
                    Constants.PREFIX + "§cMute expires: §e%expires%",

            Constants.PREFIX + "§cAi primit mute pentru §e%reason%§c.\n" +
                    Constants.PREFIX + "§cMute-ul expira: §e%expires%");

    @EventHandler
    public void onChat(ChatEvent e) {
        if (!(e.getSender() instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) e.getSender();
        RemoteUser user = RalamaProxy.getInstance().getPlayer(player);

        if (!user.isLoggedIn()
                && !(e.getMessage().startsWith("/login") || e.getMessage().startsWith("/l") || e.getMessage().startsWith("/register") || e.getMessage().startsWith("/reg"))) {
            player.sendMessage(NOT_LOGGED_IN.toString(user.getLanguage()));
            e.setCancelled(true);
            return;
        }

        if (e.getMessage().startsWith("/")) return;

        String[] muteData = user.getMuteData();

        if (muteData != null) {
            e.setCancelled(true);

            long expires = Long.parseLong(muteData[1]);

            player.sendMessage(MUTE
                    .replaced("%reason%", muteData[2])
                    .replaced("%expires%", (expires == 0 ? "Never" : Constants.formatDate(expires)))
                    .toString(user.getLanguage()));
            return;
        }

        for (String entry : RalamaProxy.getInstance().getFilterWords()) {
            if (e.getMessage().toLowerCase().contains(entry)) {
                e.setCancelled(true);
                break;
            }
        }

        RalamaProxy.getInstance().getChannel().writeAndFlush(new PlayerMessagePacket(player.getName(), e.getMessage()).serialize());
    }
}
