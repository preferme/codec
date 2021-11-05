package hl.nio.codec.useage;

import hl.nio.codec.field.annotation.Field;
import hl.nio.codec.pack.annotation.Inherited;

@Inherited
public abstract class AbstractRequest {

    @Field
    private String sessionId;
    @Field
    private String token;

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
