package net.ralama.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class ServerTemplate {
    @Getter
    private final String name;
    @Getter
    private final String path;
    @Getter
    private final String jarName;
    @Getter
    private final int maxServers;
    @Getter
    @Setter
    private int maxMemory;
    @Getter
    @Setter
    private int minMemory;
}
