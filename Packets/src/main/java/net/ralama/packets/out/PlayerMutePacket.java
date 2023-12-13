package net.ralama.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.ralama.packets.Packet;

@AllArgsConstructor
public class PlayerMutePacket extends Packet {
    @Getter
    private final String name;
    @Getter
    private final String[] muteData;
}
