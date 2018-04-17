package netty.ende2;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * Created by 汪 胜 安 on 2018/4/16.
 */
public class Client {
    private String host;
    private int port;

    public Client(){}

    public Client(String host,int port){
        this.host = host;
        this.port = port;
    }

    public void run() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();

        try{
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sc) throws Exception {
                            sc.pipeline().addLast(new FixedLengthFrameDecoder(5));
                            sc.pipeline().addLast(new StringDecoder());
                            sc.pipeline().addLast(new ClientHandler());
                        }
                    });
            ChannelFuture cf1 = b.connect(host,port).sync();
            cf1.channel().writeAndFlush(Unpooled.wrappedBuffer("aaacdsergf".getBytes())).sync();
            cf1.channel().writeAndFlush(Unpooled.wrappedBuffer("bbbbb".getBytes())).sync();
            cf1.channel().writeAndFlush(Unpooled.wrappedBuffer("cccccccc".getBytes())).sync();

            cf1.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        try {
            new Client("127.0.0.1",9876).run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
