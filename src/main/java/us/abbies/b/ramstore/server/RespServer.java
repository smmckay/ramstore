package us.abbies.b.ramstore.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.redis.RedisArrayAggregator;
import io.netty.handler.codec.redis.RedisBulkStringAggregator;
import io.netty.handler.codec.redis.RedisDecoder;
import io.netty.handler.codec.redis.RedisEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.abbies.b.ramstore.server.discard.DiscardServerHandler;
import us.abbies.b.ramstore.server.redis.InlineCommandDecoder;
import us.abbies.b.ramstore.server.redis.RedisCommandDecoder;

public class RespServer {
    private static final Logger log = LogManager.getLogger(RespServer.class);

    private int port;

    public RespServer(int port) {
        this.port = port;
    }

    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap redisServerBootstrap = new ServerBootstrap();
            redisServerBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline().addLast(new RedisEncoder());
                            socketChannel.pipeline().addLast(new RedisDecoder(true));
                            socketChannel.pipeline().addLast(new RedisBulkStringAggregator());
                            socketChannel.pipeline().addLast(new RedisArrayAggregator());
                            socketChannel.pipeline().addLast(new InlineCommandDecoder());
                            socketChannel.pipeline().addLast(new RedisCommandDecoder());
                            socketChannel.pipeline().addLast(new RamstoreHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = redisServerBootstrap.bind(port).sync();
            log.info("Redis server started on port {}", port);

            f.channel().closeFuture().sync();
            log.info("Redis server shutting down");
        } catch (Exception e) {
            log.error("Fatal server error", e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        int port = 6379;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        new RespServer(port).run();
    }
}
