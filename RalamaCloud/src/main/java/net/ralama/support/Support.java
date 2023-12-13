package net.ralama.support;

import lombok.Getter;
import net.ralama.Ralama;
import net.ralama.player.RalamaPlayer;

import java.util.ArrayList;
import java.util.List;

public class Support {
    @Getter
    private final RalamaPlayer initiator;
    @Getter
    private final List<String> members = new ArrayList<>();
    public Support(RalamaPlayer initiator, RalamaPlayer helper) {
        this.initiator = initiator;
        this.members.add(initiator.getName());
        this.members.add(helper.getName());
    }
    public void sendMessage(String message) {
        this.members.forEach(t-> Ralama.getPlayer(t).sendMessage(message));
    }
}
