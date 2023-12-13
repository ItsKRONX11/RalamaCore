package net.ralama.ralamaproxy;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;
import net.ralama.packets.Packet;
import net.ralama.packets.api.Language;
import net.ralama.packets.api.RemoteUser;
import net.ralama.packets.base.GetterPacket;
import net.ralama.packets.base.ResponsePacket;
import net.ralama.packets.in.ProxyStopPacket;
import net.ralama.ralamaproxy.command.CommandForward;
import net.ralama.ralamaproxy.command.PingCommand;
import net.ralama.ralamaproxy.listener.*;
import net.ralama.ralamaproxy.netty.ProxyClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public final class RalamaProxy extends Plugin {
    @Getter
    private static RalamaProxy instance;
    @Getter
    private final Map<UUID, CompletableFuture<ResponsePacket>> sentPackets = new ConcurrentHashMap<>();
    @Getter
    private final Map<String, RemoteUser> namePlayer = new ConcurrentHashMap<>();
    @Getter
    private final List<String> filterWords = new ArrayList<>();
    @Getter
    @Setter
    private Channel channel;
    @Getter
    private int port;
    @Getter
    private String name;
    @Getter
    private String host;
    @Getter
    @Setter
    private int onlinePlayers;
    @Getter
    @Setter
    private String motd = "Proxy server not yet loaded.";

    public static void updateTab(ProxiedPlayer player, String server, Language language) {
        if (RalamaProxy.getInstance().getPlayer(player).getLanguage() == Language.ROMANIAN) {
            player.setTabHeader(new ComponentBuilder(
                            ChatColor.translateAlternateColorCodes('&', "\n&e&l◂ &9&l&oRALAMA NETWORK&e&l ▸\n&3▸ &7Esti conectat pe &3&l" + server + "\n")).create(),
                    new ComponentBuilder(ChatColor.translateAlternateColorCodes('&',
                            "\n\n&3&l&lIntra pe serverul de discord!\n&e&l↳ &3&ndiscord.ralama.net§r &e&l↲\n\n&e◂ &7mc.ralama.net&e ▸")).create());
        } else {
            player.setTabHeader(new ComponentBuilder(
                            ChatColor.translateAlternateColorCodes('&', "\n&e&l◂ &9&l&oRALAMA NETWORK&e&l ▸\n&3▸ &7Connected to &3&l" + server + "\n")).create(),
                    new ComponentBuilder(ChatColor.translateAlternateColorCodes('&',
                            "\n\n&3&l&oJoin the discord server!\n&e&l↳ &3&ndiscord.ralama.net§r &e&l↲\n\n&e◂ &7mc.ralama.net&e ▸")).create());
        }
    }

    @Override
    public void onEnable() {
        instance = this;
        saveResource("config.yml");


        //getProxy().getServers().put("lobby", ProxyServer.getInstance().constructServerInfo("lobby", new InetSocketAddress("localhost", 25565), "Placeholder server", false));

        try {
            Configuration config = YamlConfiguration.getProvider(YamlConfiguration.class).load(new File(ProxyServer.getInstance().getPluginsFolder().getParentFile(), "config.yml"));
            LinkedHashMap<?, ?> map = (LinkedHashMap<?, ?>) config.getList("listeners").get(0);
            String host = (String) map.get("host");
            port = Integer.parseInt(host.split(":")[1]);

            Configuration pluginConfig = YamlConfiguration.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
            name = pluginConfig.getString("name");
            this.host = pluginConfig.getString("host");
        } catch (IOException e) {
            e.printStackTrace();
            getProxy().stop();
        }

        getProxy().getPluginManager().registerListener(this, new PingListener());
        getProxy().getPluginManager().registerListener(this, new ConnectListener());
        getProxy().getPluginManager().registerListener(this, new ChatListener());
        getProxy().getPluginManager().registerListener(this, new PreJoinListener());
        getProxy().getPluginManager().registerListener(this, new GeneralListener());
        getProxy().getPluginManager().registerCommand(this, new PingCommand());

        initNettyClient();
    }

    public void initNettyClient() {
        ProxyServer.getInstance().getScheduler().runAsync(this, new ProxyClient("localhost", 1300));
    }

    public RemoteUser getPlayer(ProxiedPlayer player) {
        return this.getPlayer(player.getName());
    }

    public RemoteUser getPlayer(String name) {
        RemoteUser user = this.namePlayer.get(name);
        if (user == null) {
            user = new RemoteUser(name);
            this.namePlayer.put(name, user);
        }
        return user;
    }

    @Override
    public void onDisable() {
        sendPacket(new ProxyStopPacket(name));
        channel.close();
    }

    public void sendPacket(Packet packet) {
        this.channel.writeAndFlush(packet.serialize());
    }

    public ResponsePacket sendGetter(GetterPacket getter) {
        this.sendPacket(getter);

        CompletableFuture<ResponsePacket> future = new CompletableFuture<>();
        this.sentPackets.put(getter.getId(), future);
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveResource(String resourcePath) {
        try {
            File file = new File(getDataFolder(), resourcePath);

            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            if (!file.exists()) {
                InputStream in = getResourceAsStream(resourcePath);
                Files.copy(in, file.toPath());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void register(String command) {
        getProxy().getPluginManager().registerCommand(this, new CommandForward(command));
    }

}
