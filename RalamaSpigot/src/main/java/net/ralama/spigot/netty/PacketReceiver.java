package net.ralama.spigot.netty;

import io.netty.channel.ChannelHandlerContext;
import net.ralama.packets.Packet;

public abstract class PacketReceiver {
    public abstract void receivePacket(Packet rawPacket, ChannelHandlerContext ctx);
}
