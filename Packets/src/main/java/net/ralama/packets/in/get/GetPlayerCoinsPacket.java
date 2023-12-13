package net.ralama.packets.in.get;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.ralama.packets.base.GetterPacket;
import net.ralama.packets.base.ResponsePacket;

import java.util.UUID;

@AllArgsConstructor
public class GetPlayerCoinsPacket extends GetterPacket {
    @Getter
    private final String name;

    public static class Response extends ResponsePacket {
        @Getter
        private final int coins;

        public Response(UUID targetId, int coins) {
            super(targetId);
            this.coins = coins;
        }
    }
}
