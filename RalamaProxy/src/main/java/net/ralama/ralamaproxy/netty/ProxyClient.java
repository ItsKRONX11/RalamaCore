package net.ralama.ralamaproxy.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.AllArgsConstructor;
import net.ralama.ralamaproxy.RalamaProxy;

@AllArgsConstructor
public class ProxyClient implements Runnable {
    private final String host;
    private final int port;

    @Override
    public void run() {
        System.out.println("Attempting connection to RalamaCloud");
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)))
                                    .addLast(new ObjectEncoder())
                                    .addLast(new ProxyHandler());
                        }
                    });

            b.connect(host, port).sync().channel().closeFuture().sync();
        } catch (Exception e) {
            System.out.println("Could not connect to cloud! Reattempt in 3 seconds!");
            try {
                Thread.sleep(3000);
                RalamaProxy.getInstance().initNettyClient();
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        } finally {
            group.shutdownGracefully();
        }
    }
}
