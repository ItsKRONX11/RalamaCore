package net.ralama.ralamaproxy.listener;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.ralama.packets.in.PlayerJoinPacket;
import net.ralama.packets.in.get.GetLobbyPacket;
import net.ralama.packets.out.PlayerConnectPacket;
import net.ralama.ralamaproxy.RalamaProxy;

public class ConnectListener implements Listener {
    @EventHandler
    public void onJoin(LoginEvent e) {
        RalamaProxy.getInstance().sendPacket(new PlayerJoinPacket(e.getConnection().getName(), RalamaProxy.getInstance().getName(), e.getConnection().getAddress().getHostName()));
    }

    @EventHandler
    public void onServerConnected(ServerConnectedEvent e) {
        RalamaProxy.getInstance().sendPacket(new PlayerConnectPacket(e.getServer().getInfo().getName(), e.getPlayer().getName()));

        RalamaProxy.updateTab(e.getPlayer(), e.getServer().getInfo().getName(), RalamaProxy.getInstance().getPlayer(e.getPlayer()).getLanguage());
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent e) {
        if (e.getTarget().getName().equalsIgnoreCase("lobby")) {
            GetLobbyPacket.Response response = (GetLobbyPacket.Response) RalamaProxy.getInstance().sendGetter(new GetLobbyPacket(e.getPlayer().getName()));
            if (response.getLobbyName() != null) {
                e.setTarget(ProxyServer.getInstance().getServerInfo(response.getLobbyName()));
            }
        }
    }
}
