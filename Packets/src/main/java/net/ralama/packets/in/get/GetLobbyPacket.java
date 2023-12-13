package net.ralama.packets.in.get;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.ralama.packets.base.GetterPacket;
import net.ralama.packets.base.ResponsePacket;

import java.util.UUID;

@AllArgsConstructor
public class GetLobbyPacket extends GetterPacket {
    @Getter
    private final String playerName;

    public static class Response extends ResponsePacket {
        @Getter
        private final String lobbyName;

        public Response(UUID targetId, String lobbyName) {
            super(targetId);
            this.lobbyName = lobbyName;
        }
    }
}
