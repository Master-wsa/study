package netty.runtime;

import java.io.Serializable;

/**
 * Created by 汪 胜 安 on 2018/4/19.
 */
public class Request implements Serializable {

    private static final long SERIAL_VERSION_UID = 1l;

    private String uid;
    private String name;
    private String requestMessage;
    private byte[] attachment;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }

    public byte[] getAttachment() {
        return attachment;
    }

    public void setAttachment(byte[] attachment) {
        this.attachment = attachment;
    }
}
