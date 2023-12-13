package net.ralama.packets.in;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.ralama.packets.Packet;

@AllArgsConstructor
public class InProxyStartPacket extends Packet {
    @Getter
    private final int port;
    @Getter
    private final String host;
    @Getter
    private final String name;
}
