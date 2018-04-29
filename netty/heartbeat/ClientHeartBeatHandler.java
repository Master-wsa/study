package netty.heartbeat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by 汪 胜 安 on 2018/4/23.
 */
public class ClientHeartBeatHandler extends ChannelInboundHandlerAdapter {

    private ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> heartbeat;
    //主动向服务端发送认证消息
    private InetAddress addr;

    private static final String SUCCESS_KEY = "auth_success_key";

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        addr = InetAddress.getLocalHost();
        String ip = addr.getHostAddress();
        String key = "1234";
        String auth = ip + "," + key;
        ctx.writeAndFlush(auth);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if(msg instanceof String){
                String ret = (String) msg;
                if(SUCCESS_KEY.equals(ret)){
                    System.out.println(".....");
                    //握手成功，主动发送心跳信息
                    this.heartbeat = this.scheduled.scheduleWithFixedDelay(new HeartBeatTask(ctx),0,5, TimeUnit.SECONDS);
                    System.out.println(msg);
                }else{
                    System.out.println(msg);
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private class HeartBeatTask implements Runnable {

        private final ChannelHandlerContext ctx;

        public HeartBeatTask(final ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            try {
                System.out.println("/////");
                RequestInfo info = new RequestInfo();
                Sigar sigar = new Sigar();
                //ip
                info.setIp(addr.getHostAddress());
                //cpu prec
                CpuPerc cpuPerc = sigar.getCpuPerc();
                HashMap<String,Object> cpuPercMap = new HashMap<>();
                cpuPercMap.put("combined",cpuPerc.getCombined());
                cpuPercMap.put("user",cpuPerc.getUser());
                cpuPercMap.put("sys",cpuPerc.getSys());
                cpuPercMap.put("wait",cpuPerc.getWait());
                cpuPercMap.put("idle",cpuPerc.getIdle());
                //memory
                Mem mem = sigar.getMem();
                HashMap<String,Object> memoryMap = new HashMap<>();
                memoryMap.put("total",mem.getTotal() / 1024L);
                memoryMap.put("used",mem.getUsed() / 1024L);
                memoryMap.put("free",mem.getFree() / 1024L);
                info.setCpuPercMap(cpuPercMap);
                info.setMemoryMap(memoryMap);

                ctx.writeAndFlush(info);
            } catch (SigarException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        if(this.heartbeat != null){
            this.heartbeat.cancel(true);
            this.heartbeat = null;
        }
    }
}
