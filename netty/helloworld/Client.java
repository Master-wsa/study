package netty.helloworld;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by 汪 胜 安 on 2018/4/14.
 */
public class Client {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sc) throws Exception {
                            sc.pipeline().addLast(new ClientHandler());
                        }
                    });

            ChannelFuture cf1 = b.connect("127.0.0.1",9999).sync();
            ChannelFuture cf2 = b.connect("127.0.0.1",8888).sync();
            //通过这个管道写数据
            cf1.channel().writeAndFlush(Unpooled.copiedBuffer("hello netty!".getBytes()));
            cf2.channel().writeAndFlush(Unpooled.copiedBuffer("hello world!".getBytes()));
            cf1.channel().closeFuture().sync();
            cf2.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
