package net.ralama.ralamaproxy.listener;

import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.ralama.ralamaproxy.RalamaProxy;

public class PingListener implements Listener {
    @EventHandler
    public void onPing(ProxyPingEvent e) {
        e.getResponse().setPlayers(new ServerPing.Players(1000, RalamaProxy.getInstance().getOnlinePlayers(), null));
        e.getResponse().setDescriptionComponent(new TextComponent(RalamaProxy.getInstance().getMotd()));
    }
}
