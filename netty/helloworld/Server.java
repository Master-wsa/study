package netty.helloworld;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by 汪 胜 安 on 2018/4/14.
 */
public class Server {

    public static void main(String[] args) throws InterruptedException {
        //第一个线程组是用于接收Client连接的
        EventLoopGroup bGroup = new NioEventLoopGroup();
        //第二个线程组是用于实际的业务处理操作的
        EventLoopGroup wGroup = new NioEventLoopGroup();
        try {
            //创建辅助工具类，用于服务通道的一系列配置
            ServerBootstrap b = new ServerBootstrap();
            //绑定两个线程组
            b.group(bGroup, wGroup)
                    //指定NIO模式   服务端为NIOSocketChannel.class
                    .channel(NioServerSocketChannel.class)
                    //指定tcp缓冲区
                    .option(ChannelOption.SO_BACKLOG, 100)
                    //设置发送缓冲大小
                    .option(ChannelOption.SO_SNDBUF, 32 * 1024)
                    //设置接收缓冲大小
                    .option(ChannelOption.SO_RCVBUF, 32 * 1024)
                    //保持连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sc) throws Exception {
                            //配置具体数据接收方式的处理
                            sc.pipeline().addLast(new ServerHandler());
                        }
                    });
            //进行绑定
            ChannelFuture cf1 = b.bind(9999).sync();
            ChannelFuture cf2 = b.bind(8888).sync();
            //等待服务器端关闭  相当于Integer.MAX_VALUE
            cf1.channel().closeFuture().sync();
            cf2.channel().closeFuture().sync();
        } finally {
            bGroup.shutdownGracefully();
            wGroup.shutdownGracefully();
        }
    }
}
