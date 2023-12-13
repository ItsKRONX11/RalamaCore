package net.ralama.packets.in;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.ralama.packets.Packet;
import net.ralama.packets.api.Language;

@AllArgsConstructor
public class PlayerLanguagePacket extends Packet {
    @Getter
    private final String name;
    @Getter
    private final Language language;
}
