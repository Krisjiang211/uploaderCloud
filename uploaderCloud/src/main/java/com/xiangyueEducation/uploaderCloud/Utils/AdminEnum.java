package com.xiangyueEducation.uploaderCloud.Utils;

public enum AdminEnum {

    SUPER_ADMIN("SUPER_ADMIN","超级管理员"),;


    private String msg;
    private String content;

    private AdminEnum(String msg, String content) {
        this.msg = msg;
        this.content = content;
    }

    public String getMsg() {
        return msg;
    }

    public String getContent() {
        return content;
    }

}
