package com.example.will.backtasks.api;

public enum AppConstant {

    BROADCAST_ACTION_ONE("com.maguji", "10001", "第一个频道的广播");

    private String mName;
    private String mCode;
    private String mDesc;

    AppConstant(String name, String code, String desc) {
        mName = name;
        mCode = code;
        mDesc = desc;
    }
}
