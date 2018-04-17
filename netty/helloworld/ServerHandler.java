package netty.helloworld;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;



/**
 * Created by 汪 胜 安 on 2018/4/14.
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //对于只有读没有写操作的，需要把这个msg数据给释放掉，否则内存会耗尽，若有写操作时，则不需要再释放了，因为写操作会直接释放这个msg数据
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req); //把buf中的数据读到req字节数组中
        String body = new String(req,"UTF-8");
        System.out.println("客户端发来的消息 : " + body);
        String response = "服务端已经接收到您的消息 ：HI";
        //.addListener(ChannelFutureListener.CLOSE)指客户端在接收到服务端返回的信息后，主动断开客户端连接
        //没有这句可表示长链接，有则表示短链接
        ctx.writeAndFlush(Unpooled.copiedBuffer(response.getBytes())).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
