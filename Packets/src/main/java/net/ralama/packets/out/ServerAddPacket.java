package net.ralama.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.ralama.packets.Packet;

@AllArgsConstructor
public class ServerAddPacket extends Packet {
    @Getter
    public final String name;
    @Getter
    public final String host;
    @Getter
    public final int port;
}
