package net.ralama.spigot;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.Getter;
import lombok.Setter;
import net.ralama.packets.Packet;
import net.ralama.packets.api.RemoteUser;
import net.ralama.packets.base.GetterPacket;
import net.ralama.packets.base.ResponsePacket;
import net.ralama.packets.in.InServerStopPacket;
import net.ralama.spigot.commands.ChatColorCommand;
import net.ralama.spigot.commands.LocaleCommand;
import net.ralama.spigot.event.LobbyJoinListener;
import net.ralama.spigot.listeners.InventoryClickListener;
import net.ralama.spigot.listeners.JoinQuitListener;
import net.ralama.spigot.netty.PacketReceiver;
import net.ralama.spigot.netty.RalamaSpigotReceiver;
import net.ralama.spigot.netty.SpigotClient;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public final class RalamaSpigot extends JavaPlugin {
    public static PacketReceiver[] receivers = new PacketReceiver[2];
    @Getter
    private static RalamaSpigot instance;
    @Getter
    private final Map<String, RemoteUser> namePlayer = new ConcurrentHashMap<>();
    @Getter
    @Setter
    private String serverName;
    @Getter
    @Setter
    private LobbyJoinListener lobbyJoinListener;
    @Getter
    @Setter
    private String serverId;
    @Getter
    @Setter
    private String host;
    @Getter
    @Setter
    private Channel channel;
    @Getter
    @Setter
    private int globalOnline;
    @Getter
    private HashMap<UUID, CompletableFuture<ResponsePacket>> sentPackets;
    @Getter
    private PacketReceiver receiver;

    @Override
    public void onEnable() {
        initSpigotClient();
        instance = this;
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholder().register();
        }
        this.receiver = new RalamaSpigotReceiver();
        receivers[0] = receiver;
        sentPackets = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(new JoinQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), this);

        getCommand("chatcolor").setExecutor(new ChatColorCommand());

        LocaleCommand localeCommand = new LocaleCommand();
        getCommand("locale").setExecutor(localeCommand);
        getCommand("language").setExecutor(localeCommand);
        Bukkit.getPluginManager().registerEvents(localeCommand, this);
    }

    @Override
    public void onDisable() {
        channel.writeAndFlush(new InServerStopPacket(this.serverName).serialize()).addListener(ChannelFutureListener.CLOSE);
        channel.close();
    }

    public RemoteUser getPlayer(Player player) {
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

    public boolean isLobby() {
        if (serverName == null) {
            return false;
        }
        return serverName.toLowerCase().startsWith("lobby") || serverName.toLowerCase().startsWith("silent");
    }

    public void initSpigotClient() {
        new Thread(new SpigotClient("localhost", 1300)).start();
    }

    public ResponsePacket sendGetterPacket(GetterPacket packet) {
        this.sendPacket(packet);
        CompletableFuture<ResponsePacket> future = new CompletableFuture<>();
        sentPackets.put(packet.getId(), future);
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void sendPacket(Packet packet) {
        this.channel.writeAndFlush(packet.serialize());
    }
}
