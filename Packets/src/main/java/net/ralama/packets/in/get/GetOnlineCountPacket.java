package net.ralama.packets.in.get;

import lombok.Getter;
import net.ralama.packets.base.GetterPacket;
import net.ralama.packets.base.ResponsePacket;

import java.util.UUID;

public class GetOnlineCountPacket extends GetterPacket {
    @Getter
    private final String server;

    public GetOnlineCountPacket(String server) {
        this.server = server;
    }

    public static final class Response extends ResponsePacket {
        @Getter
        private final int online;

        public Response(UUID targetId, int online) {
            super(targetId);
            this.online = online;
        }
    }
}
