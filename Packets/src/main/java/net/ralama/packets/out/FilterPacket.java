package net.ralama.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.ralama.packets.Packet;

import java.util.List;

@AllArgsConstructor
public class FilterPacket extends Packet {
    @Getter
    private final List<String> entries;
}
