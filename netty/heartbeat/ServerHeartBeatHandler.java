package netty.heartbeat;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.HashMap;

/**
 * Created by 汪 胜 安 on 2018/4/23.
 */
public class ServerHeartBeatHandler extends ChannelInboundHandlerAdapter {

    /** key:ip  value:auth**/
    private static HashMap<String,String> AUTH_IP_MAP = new HashMap<>();
    private static final String SUCCESS_KEY = "auth_success_key";

    static{
        AUTH_IP_MAP.put("10.103.1.198","1234");
    }

    private boolean auth(ChannelHandlerContext ctx,Object msg){
        String []ret = ((String)msg).split(",");
        String auth = AUTH_IP_MAP.get(ret[0]);
        if(auth != null && auth.equals(ret[1])){
            ctx.writeAndFlush(SUCCESS_KEY);
            return true;
        }else{
            ctx.writeAndFlush("auth failure !").addListener(ChannelFutureListener.CLOSE);
            return false;
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(msg);
        if(msg instanceof String){
            auth(ctx,msg);
        }else if(msg instanceof RequestInfo) {
            RequestInfo info = (RequestInfo) msg;
            System.out.println("---------------------------------");
            System.out.println("当前主机IP为：" + info.getIp());
            System.out.println("当前主机cpu情况：");
            HashMap<String,Object> cpuPercMap = info.getCpuPercMap();
            System.out.println("总使用率：" + cpuPercMap.get("combined"));
            System.out.println("用户使用率：" + cpuPercMap.get("user"));
            System.out.println("系统使用率：" + cpuPercMap.get("sys"));
            System.out.println("等待率：" + cpuPercMap.get("wait"));
            System.out.println("空闲率：" + cpuPercMap.get("idle"));
            System.out.println("当前主机memory情况：");
            HashMap<String,Object> memory = info.getMemoryMap();
            System.out.println("内存总量：" + memory.get("total"));
            System.out.println("当前内存使用量：" + memory.get("used"));
            System.out.println("当前内存剩余量：" + memory.get("free"));
            System.out.println("---------------------------------");

            ctx.writeAndFlush("info received!");
        }else{
            ctx.writeAndFlush("connect failure!").addListener(ChannelFutureListener.CLOSE);
        }
    }
}
