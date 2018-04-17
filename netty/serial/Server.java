package netty.serial;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Created by 汪 胜 安 on 2018/4/16.
 */
public class Server {

    private int port;

    public Server(){}

    public Server(int port){
        this.port = port;
    }

    public void run() throws InterruptedException {
        EventLoopGroup bGroup = new NioEventLoopGroup();
        EventLoopGroup wGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bGroup,wGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_SNDBUF,32*1024)
                    .option(ChannelOption.SO_RCVBUF,32*1024)
                    .option(ChannelOption.SO_BACKLOG,100)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    //设置日志（LogLevel为netty中的日志）
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sc) throws Exception {
                            sc.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
                            sc.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
                            sc.pipeline().addLast(new ServerHandler());
                        }
                    });
            ChannelFuture cf1 = b.bind(port).sync();
            cf1.channel().closeFuture().sync();
        } finally {
            bGroup.shutdownGracefully();
            wGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        try {
            new Server(8888).run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}