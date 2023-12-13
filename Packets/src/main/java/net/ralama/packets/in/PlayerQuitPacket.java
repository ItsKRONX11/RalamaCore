package net.ralama.packets.in;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.ralama.packets.Packet;

@AllArgsConstructor
public class PlayerQuitPacket extends Packet {
    @Getter
    private final String name;
}
