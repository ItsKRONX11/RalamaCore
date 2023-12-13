package net.ralama.server;

import com.google.common.collect.ImmutableList;
import net.ralama.Ralama;
import net.ralama.packets.Packet;
import net.ralama.packets.api.Constants;
import net.ralama.packets.api.Rank;
import net.ralama.packets.out.OutKillRequestPacket;
import net.ralama.packets.out.ServerAddPacket;
import net.ralama.player.RalamaPlayer;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ServerManager {
    private final List<RalamaServer> servers = new ArrayList<>();
    private final List<RalamaProxy> proxies = new ArrayList<>();
    private final List<ServerTemplate> templates = new ArrayList<>();

    public RalamaServer getServerByName(String name) {
        for (RalamaServer server : this.servers) {
            if (server.getName().equalsIgnoreCase(name)) return server;
        }
        return null;
    }

    public RalamaServer getServerByPort(int port) {
        for (RalamaServer server : this.servers) {
            if (server.getPort() == port) return server;
        }
        return null;
    }

    public RalamaProxy getProxyByName(String name) {
        for (RalamaProxy proxy : this.proxies) {
            if (proxy.getName().equalsIgnoreCase(name)) {
                return proxy;
            }
        }
        return null;
    }

    public ServerTemplate getTemplateByName(String name) {
        for (ServerTemplate template : this.templates) if (template.getName().equalsIgnoreCase(name)) return template;
        return null;
    }

    public ImmutableList<RalamaServer> getServers() {
        return ImmutableList.copyOf(servers);
    }

    public ImmutableList<RalamaProxy> getProxies() {
        return ImmutableList.copyOf(proxies);
    }

    public ImmutableList<RalamaServer> getLobbies() {
        return ImmutableList.<RalamaServer>builder().addAll(servers.stream().filter(RalamaServer::isLobby).collect(Collectors.toList())).build();
    }

    public void startServer(ServerTemplate template, String name) {
        new Thread(() -> {
            try {
                RalamaServer server = new RalamaServer(name, "localhost",
                        Constants.RANDOM.nextInt(4000) + 2000,
                        UUID.randomUUID().toString().split("-")[0], template);

                String id = server.getServerId();

                File temp = new File(Ralama.getFileManager().get("servers"), id);
                if (!temp.exists()) temp.mkdir();
                Ralama.getFileManager().unzip(template.getPath(), temp.getPath());

                ProcessBuilder builder = new ProcessBuilder("java", "-jar", "-Xmx" + template.getMaxMemory() + "M", "-Xms" + template.getMaxMemory() + "M",
                        template.getJarName(), "nogui", "--port", String.valueOf(server.getPort()));
                builder.directory(temp);
                builder.start();

                server.setState(ServerState.STARTING);
                Ralama.getServerManager().add(server);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void add(RalamaServer server) {
        if (this.servers.contains(server)) return;

        this.servers.add(server);

        for (RalamaProxy proxy : proxies) {
            proxy.sendPacket(new ServerAddPacket(server.getName(), server.getHost(), server.getPort()));
        }
    }

    public void add(RalamaProxy proxy) {
        this.proxies.add(proxy);
    }

    public void remove(RalamaServer server) {
        this.servers.remove(server);
    }

    public void remove(RalamaProxy proxy) {
        this.proxies.remove(proxy);
    }

    public RalamaServer getBestLobby(RalamaPlayer player) {
        Stream<RalamaServer> stream = getLobbies().stream();

        if (player.getRank().equalsIsHigher(Rank.TIKTOK) && getLobbies().stream().anyMatch(s -> s.getName().toLowerCase().startsWith("silent"))) {
            stream = stream.filter(s -> s.getName().toLowerCase().startsWith("silent"));
        }

        Optional<RalamaServer> option = stream
                .min(Comparator.comparing(server -> server.getPlayers().size()));

        return option.orElse(null);
    }

    public void sendLobbiesPacket(Packet packet) {
        for (RalamaServer server : this.servers) {
            if (server.isLobby()) {
                server.sendPacket(packet);
            }
        }
    }

    public void sendAllPacket(Packet packet) {
        this.sendServersPacket(packet);
        this.sendProxiesPacket(packet);
    }

    public void sendProxiesPacket(Packet packet) {
        for (RalamaProxy proxy : this.proxies) {
            proxy.sendPacket(packet);
        }
    }

    public void sendServersPacket(Packet packet) {
        for (RalamaServer server : this.servers) {
            server.sendPacket(packet);
        }
    }

    public void stopServer(RalamaServer server) {
        server.sendPacket(new OutKillRequestPacket());
    }

    public void loadTemplates() throws SQLException {
        this.templates.clear();

        ResultSet rs = Ralama.getDatabase().getConnection().prepareStatement("SELECT * FROM templates;").executeQuery();

        while (rs.next())
            this.templates.add(new ServerTemplate(rs.getString("NAME"), rs.getString("PATH"), rs.getString("JAR_NAME"), rs.getInt("MAX_SERVERS"), rs.getInt("MAX_MEMORY"), rs.getInt("MIN_MEMORY")));
    }
}
