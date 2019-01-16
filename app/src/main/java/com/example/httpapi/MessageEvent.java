package com.example.httpapi;

public class MessageEvent {
    // code = 0, 网络连接失败
    // code = 1，数据库用户不存在
    // code = 2，更新Adpater
    // code = 3，用于测试图片
    public final int code;
    public MessageEvent(int code) {
        this.code = code;
    }
}
