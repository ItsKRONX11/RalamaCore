package net.ralama.packets.in.get;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.ralama.packets.base.GetterPacket;
import net.ralama.packets.base.ResponsePacket;

import java.util.UUID;

@AllArgsConstructor
public class GetPlayerRankPacket extends GetterPacket {
    @Getter
    private final String name;

    public static class Response extends ResponsePacket {
        @Getter
        private final String rankName;

        public Response(UUID targetId, String rankName) {
            super(targetId);
            this.rankName = rankName;
        }
    }

}
