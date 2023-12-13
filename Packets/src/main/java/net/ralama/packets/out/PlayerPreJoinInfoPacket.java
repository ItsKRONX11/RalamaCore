package net.ralama.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.ralama.packets.api.Language;
import net.ralama.packets.base.GetterPacket;
import net.ralama.packets.base.ResponsePacket;

import java.util.UUID;

@AllArgsConstructor
public class PlayerPreJoinInfoPacket extends GetterPacket {
    @Getter
    private final String name;
    @Getter
    private final String ip;

    public static class Response extends ResponsePacket {
        @Getter
        private final UUID uuid;
        @Getter
        private final String name;
        @Getter
        private final boolean online;
        @Getter
        private final boolean premium;
        @Getter
        private final String[] banData;
        @Getter
        private final String[] muteData;
        @Getter
        private final Language language;

        public Response(UUID targetId, UUID playerId, String name, boolean online, boolean premium, String[] muteData, String[] banData, Language language) {
            super(targetId);
            this.uuid = playerId;
            this.name = name;
            this.online = online;
            this.premium = premium;
            this.muteData = muteData;
            this.banData = banData;
            this.language = language;
        }
    }
}
