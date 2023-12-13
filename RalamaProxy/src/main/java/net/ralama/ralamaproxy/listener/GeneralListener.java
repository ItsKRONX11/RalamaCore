package net.ralama.ralamaproxy.listener;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.ralama.packets.in.PlayerQuitPacket;
import net.ralama.ralamaproxy.RalamaProxy;

public class GeneralListener implements Listener {
    @EventHandler
    public void onQuit(PlayerDisconnectEvent e) {
        RalamaProxy.getInstance().getChannel().writeAndFlush(new PlayerQuitPacket(e.getPlayer().getName()).serialize());
    }

    @EventHandler
    public void onTabComplete(TabCompleteEvent e) {
        e.setCancelled(true);
    }
}
