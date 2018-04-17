package netty.serial;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import utils.GzipUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

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

    public void run() throws InterruptedException, IOException {
        EventLoopGroup group = new NioEventLoopGroup();

        try{
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sc) throws Exception {
                            sc.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
                            sc.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
                            sc.pipeline().addLast(new ClientHandler());
                        }
                    });
            ChannelFuture cf1 = b.connect(host,port).sync();

            for (int i = 0; i < 5; i++) {
                Req req = new Req();
                req.setUid(Integer.toString(i));
                req.setName("req" + i);
                req.setRequestMessage("发送消息：" + i);

                //发送压缩后的图片（字节流数据）
                String path = System.getProperty("user.dir") + File.separatorChar + "source" + File.separatorChar + "6.jpg";
                FileInputStream in = new FileInputStream(new File(path));
                byte[] data = new byte[in.available()];
                in.read(data);
                in.close();
                req.setAttachment(GzipUtil.gzip(data));

                cf1.channel().writeAndFlush(req);
            }

            cf1.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        try {
            new Client("127.0.0.1",8888).run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}