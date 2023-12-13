package net.ralama.packets.in;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.ralama.packets.Packet;

@AllArgsConstructor
public class PlayerJoinPacket extends Packet {
    @Getter
    private final String name;
    @Getter
    private final String proxy;
    @Getter
    private final String ip;
}
