package net.ralama.server;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import net.ralama.packets.Packet;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RalamaProxy {
    @Getter
    private final String name;
    @Getter
    private final int port;
    @Getter
    private final List<String> players = new CopyOnWriteArrayList<>();
    @Getter
    @Setter
    private Channel channel;

    public RalamaProxy(String name, int port) {
        this.name = name;
        this.port = port;
    }

    public void sendPacket(Packet packet) {
        this.channel.writeAndFlush(packet.serialize());
    }
}
