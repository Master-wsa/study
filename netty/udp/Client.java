package netty.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

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
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST,true)
                    .handler(new ClientHandler());
            Channel ch = b.bind(0).sync().channel();
            //向网段内所有机器广播udp消息
            ch.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("查询请求", CharsetUtil.UTF_8),
                    new InetSocketAddress("255.255.255.255",port))).sync();
            if(!ch.closeFuture().await(15000)){
                System.out.println("查询超时");
            }

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
