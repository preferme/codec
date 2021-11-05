package hl.nio.codec.useage;

import hl.nio.codec.field.annotation.Field;
import hl.nio.codec.pack.annotation.Package;

import java.io.Serializable;

@Package(0x0001)
public class LoginRequest extends AbstractRequest implements Serializable {

    @Field
    private String loginName;
    @Field
    private String loginPass;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getLoginPass() {
        return loginPass;
    }

    public void setLoginPass(String loginPass) {
        this.loginPass = loginPass;
    }

}
