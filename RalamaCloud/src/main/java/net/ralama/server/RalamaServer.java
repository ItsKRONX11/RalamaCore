package net.ralama.server;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import net.ralama.packets.Packet;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RalamaServer {
    @Getter
    private final long started = System.currentTimeMillis();
    @Getter
    private final String host;
    @Getter
    private final int port;
    @Getter
    private final String name;
    @Getter
    private final String serverId;
    @Getter
    private final ServerTemplate template;
    @Getter
    private final List<String> players = new CopyOnWriteArrayList<>();
    @Setter
    @Getter
    private ServerState state;
    @Setter
    @Getter
    private Channel channel;
    @Getter
    @Setter
    private int usedMemory;
    @Getter
    @Setter
    private int maxMemory;


    public RalamaServer(String name, String host, int port, String serverId, ServerTemplate template) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.serverId = serverId;
        this.state = ServerState.STARTING;
        this.template = template;
        this.maxMemory = template.getMaxMemory();
    }
    public RalamaServer(String name, String host, int port, String serverId) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.serverId = serverId;
        this.state = ServerState.STARTING;
        this.template = null;
        this.maxMemory = 0;
    }

    public void sendPacket(Packet packet) {
        this.channel.writeAndFlush(packet.serialize());
    }

    public boolean isLobby() {
        String name = this.name.toLowerCase();
        return name.startsWith("lobby") || name.startsWith("silent");
    }

    @Override
    public String toString() {
        return this.name;
    }
}
