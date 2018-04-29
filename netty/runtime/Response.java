package netty.runtime;

import java.io.Serializable;

/**
 * Created by 汪 胜 安 on 2018/4/19.
 */
public class Response implements Serializable {

    private static final long SERIAL_VERSION_UID = 1l;

    private String uid;
    private String name;
    private String responseMessage;

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

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}
