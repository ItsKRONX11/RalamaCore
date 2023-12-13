package net.ralama.spigot.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.ralama.packets.Packet;
import net.ralama.packets.api.PacketAdapter;
import net.ralama.packets.in.InServerStartPacket;
import net.ralama.spigot.RalamaSpigot;
import org.bukkit.Bukkit;

public class SpigotHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        RalamaSpigot.getInstance().setChannel(ctx.channel());
        RalamaSpigot.getInstance().sendPacket(new InServerStartPacket(Bukkit.getPort()));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object o) {
        Bukkit.getScheduler().runTaskAsynchronously(RalamaSpigot.getInstance(), () -> {
            Packet packet = (Packet) PacketAdapter.deserialize((byte[]) o);
            RalamaSpigot.receivers[0].receivePacket(packet, ctx);
            if (RalamaSpigot.receivers[1] != null) {
                RalamaSpigot.receivers[1].receivePacket(packet, ctx);
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        e.printStackTrace();
        ctx.close();
    }
}
