package net.ralama.ralamaproxy.listener;

import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.ralama.packets.api.Constants;
import net.ralama.packets.api.Message;
import net.ralama.packets.out.PlayerPreJoinInfoPacket;
import net.ralama.ralamaproxy.RalamaProxy;

import java.lang.reflect.Field;

public class PreJoinListener implements Listener {
    private static final Message ALREADY_ONLINE = new Message(
            Constants.KICK_PREFIX + "\n\n§cYou are already online on Ralama.",
            Constants.KICK_PREFIX + "\n\n§cEsti deja online pe Ralama.");

    private static final Message WRONG_CAPS = new Message(
            Constants.KICK_PREFIX + "\n\n§cWrong username capitalisation! Please use §6%name% §cinstead.",
            Constants.KICK_PREFIX + "\n\n§cCapitalizare gresita a numelui! Foloseste §6%name%§c.");

    @EventHandler
    public void preJoin(PreLoginEvent e) {

        String name = e.getConnection().getName();

        if (name.equalsIgnoreCase("CloudAdmin") || name.length() < 3 || name.length() > 16) {
            e.setCancelReason("§cINVALID NAME");
            e.setCancelled(true);
            return;
        }

        PlayerPreJoinInfoPacket.Response info = (PlayerPreJoinInfoPacket.Response) RalamaProxy.getInstance().sendGetter(new PlayerPreJoinInfoPacket(name, e.getConnection().getAddress().getHostName()));
        if (info == null) {
            e.setCancelReason(Constants.KICK_PREFIX + "\n\n§cCould not retrieve user data. Contact an administrator");
            e.setCancelled(true);
            return;
        }

        if (info.isOnline()) {
            e.setCancelReason(ALREADY_ONLINE.toString(info.getLanguage()));
            e.setCancelled(true);
            return;
        }

        if (!name.equals(info.getName())) {
            e.setCancelReason(WRONG_CAPS.replaced("%name%", info.getName()).toString(info.getLanguage()));
            e.setCancelled(true);
            return;
        }

        String[] banData = info.getBanData();

        if (banData != null) {
            e.setCancelReason(Constants.getBanKick(banData[2], Long.parseLong(banData[1]), info.getLanguage()));
            e.setCancelled(true);
            return;
        }

        if (info.isPremium()) {
            PendingConnection connection = e.getConnection();
            connection.setOnlineMode(true);
            try {
                Class<? extends PendingConnection> initialHandler = connection.getClass();
                Field uuidField = initialHandler.getDeclaredField("uniqueId");
                uuidField.setAccessible(true);
                uuidField.set(connection, info.getUuid());
            } catch (ReflectiveOperationException exception) {
                exception.printStackTrace();
                e.setCancelReason(Constants.KICK_PREFIX + "\n\n§cAn error occurred in your connection. Please contact an administrator.");
                e.setCancelled(true);
                return;
            }
        }

        RalamaProxy.getInstance().getPlayer(name).setLanguage(info.getLanguage());
        if (info.getMuteData() != null) RalamaProxy.getInstance().getPlayer(name).setMuteData(info.getMuteData());
    }
}
