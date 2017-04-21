package com.zdf.lib_push.model;

import com.zdf.lib_push.rom.Target;

/**
 * Created by xiaofeng on 2017/4/19.
 */

public class TokenModel {
    private String mToken;
    private Target mTarget;

    public String getToken() {
        return mToken;
    }

    public void setToken(String mToken) {
        this.mToken = mToken;
    }

    public Target getTarget() {
        return mTarget;
    }

    public void setTarget(Target mTarget) {
        this.mTarget = mTarget;
    }
}
