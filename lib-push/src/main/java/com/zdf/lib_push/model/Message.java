package com.zdf.lib_push.model;

import com.zdf.lib_push.rom.Target;

/**
 * 将消息整合成Message model
 * Created by jiang on 2016/10/8.
 */

public final class Message {
    private int notifyID;  // 这个字段用于通知的消息类型，在透传中都是默认0
    private String messageID; // Umeng消息该字段都为null
    private String title;
    private String message;
    private String custom;
    private String extra;
    private Target target;

    public int getNotifyID() {
        return notifyID;
    }

    public void setNotifyID(int notifyID) {
        this.notifyID = notifyID;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCustom() {
        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        return "Message: " +
                "{ notifyID = " + notifyID +
                ", messageID = " + messageID +
                ", title = " + title +
                ", message = " + message +
                ", custom = " + custom +
                ", extra = " + extra +
                ", target = " + target +
                "}";
    }
}
