package com.zhitu.jt808server.server;

import com.zhitu.jt808server.server.codec.Jt808Decoder;
import com.zhitu.jt808server.server.codec.Jt808Encoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLException;

/**
 * broker 启动器
 *
 * @author Jun
 * @date 2020-03-03 20:55
 */
@Component
public class ServerLauncher {

    private ServerHandler serverHandler;

    private Jt808Encoder jt808Encoder;

    public ServerLauncher(ServerHandler serverHandler, Jt808Encoder jt808Encoder) {
        this.serverHandler = serverHandler;
        this.jt808Encoder = jt808Encoder;
    }

    /**
     * 启动服务
     */
    public void start() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();

            b
                    .group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws SSLException {
                            ChannelPipeline pipeline = socketChannel.pipeline();

                            pipeline.addLast(new Jt808Decoder(1024));
                            pipeline.addLast(jt808Encoder);
                            pipeline.addLast(serverHandler);
                        }
                    });

            Channel channel = b.bind("127.0.0.1", 8088).sync().channel();
            channel.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
