package netty.serial;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import utils.GzipUtil;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by 汪 胜 安 on 2018/4/16.
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Server is starting....");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Req request = (Req) msg;
        System.out.println("Server : " + request.getUid() + " , " + request.getName() + " , " + request.getRequestMessage());

        //接收到客户端发来压缩过的图片后，经过解压后写入硬盘中
        byte[] data = GzipUtil.ungzip(request.getAttachment());
        String path = System.getProperty("user.dir") + File.separatorChar + "receive" + File.separatorChar + "6.jpg";
        FileOutputStream out = new FileOutputStream(path);
        out.write(data);
        out.close();

        Res response = new Res();
        response.setUid(request.getUid());
        response.setName("res" + request.getUid());
        response.setResponseMessage("响应信息" + request.getUid());
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}