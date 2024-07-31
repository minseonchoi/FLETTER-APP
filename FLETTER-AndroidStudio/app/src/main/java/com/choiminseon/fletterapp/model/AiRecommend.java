package com.choiminseon.fletterapp.model;

public class AiRecommend {
    public String reason;
    public String packageType;
    public String combination;

    public AiRecommend(String reason, String packageType, String combination) {
        this.reason = reason;
        this.packageType = packageType;
        this.combination = combination;
    }
}
