package netty.runtime;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.concurrent.TimeUnit;

/**
 * Created by 汪 胜 安 on 2018/4/19.
 */
public class Client {

    private static class SingletonHolder{
        static final Client instance = new Client();
    }

    public static Client getInstance(){
        return SingletonHolder.instance;
    }

    private EventLoopGroup group;
    private Bootstrap b;
    private ChannelFuture cf;

    private Client(){
        group = new NioEventLoopGroup();
        b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        sc.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
                        sc.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
                        sc.pipeline().addLast(new ReadTimeoutHandler(5));
                        sc.pipeline().addLast(new ClientHandler());
                    }
                });

    }

    public void connect(){
        try {
            this.cf = b.connect("127.0.0.1",8888).sync();
            System.out.println("远程服务器已经连接，可以进行数据交换。。。");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ChannelFuture getChannelFuture(){
        if(this.cf == null){
            this.connect();
        }
        if(!this.cf.channel().isActive()){
            this.connect();
        }
        return this.cf;
    }

    public static void main(String[] args) throws InterruptedException {
        final Client client = Client.getInstance();

        ChannelFuture cf = client.getChannelFuture();
        for (int i = 0; i <= 3; i++) {
            Request req = new Request();
            req.setUid(Integer.toString(i));
            req.setName("pro" + i);
            req.setRequestMessage("数据信息" + i);
            cf.channel().writeAndFlush(req);
            TimeUnit.SECONDS.sleep(4);
        }
        cf.channel().closeFuture().sync();
        Request req = new Request();
        req.setUid(Integer.toString(5));
        req.setName("pro5");
        req.setRequestMessage("数据信息5");
        cf.channel().writeAndFlush(req);

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("进入子程序...");
                    ChannelFuture cf1 = client.getChannelFuture();
                    Request req = new Request();
                    req.setUid("4");
                    req.setName("pro4");
                    req.setRequestMessage("数据信息4");
                    cf1.channel().writeAndFlush(req);
                    cf1.channel().closeFuture().sync();
                    System.out.println("子程序结束...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();*/
        System.out.println("断开连接，主程序结束....");
    }
}