package net.ralama.packets.base;

import lombok.Getter;
import net.ralama.packets.Packet;

import java.util.UUID;

public abstract class ResponsePacket extends Packet {
    @Getter
    private final UUID targetId;

    public ResponsePacket(UUID targetId) {
        this.targetId = targetId;
    }

}
