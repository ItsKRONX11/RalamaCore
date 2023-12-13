package net.ralama.packets.base;

import lombok.Getter;
import net.ralama.packets.Packet;

import java.util.UUID;

public abstract class GetterPacket extends Packet {
    @Getter
    private final UUID id;

    public GetterPacket() {
        this.id = UUID.randomUUID();
    }
}
