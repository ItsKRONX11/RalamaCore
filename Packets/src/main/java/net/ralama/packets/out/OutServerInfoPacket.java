package net.ralama.packets.out;

import lombok.Getter;
import net.ralama.packets.Packet;

public class OutServerInfoPacket extends Packet {
    @Getter
    private String serverName;
    @Getter
    private String host;
    @Getter
    private int port;
    @Getter
    private String serverId;

    public OutServerInfoPacket(String serverName, String host, int port, String serverId) {
        this.serverName = serverName;
        this.host = host;
        this.port = port;
        this.serverId = serverId;
    }
}
