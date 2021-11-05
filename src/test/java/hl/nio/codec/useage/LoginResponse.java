package hl.nio.codec.useage;

import hl.nio.codec.field.annotation.Field;
import hl.nio.codec.pack.annotation.Package;

@Package(0x7002)
public class LoginResponse extends AbstractResponse{

    @Field
    private boolean success;

    @Field
    private String sessionId;

    @Field
    private String token;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
