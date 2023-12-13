package net.ralama.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.ralama.packets.Packet;

@AllArgsConstructor
public class PlaytimePacket extends Packet {
    @Getter
    private final long playtime;
    @Getter
    private final String name;
}
