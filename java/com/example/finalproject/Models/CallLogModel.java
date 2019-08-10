package com.example.finalproject.Models;

public class CallLogModel {
    private String id;
    private String callNumber;
    private  String callName;
    private String duration;
    private String callType;
    private String callDate;

    public CallLogModel(){}

    public CallLogModel(String callNumber, String callName, String duration, String callType, String callDate) {
        this.callNumber = callNumber;
        this.callName = callName;
        this.duration = duration;
        this.callType = callType;
        this.callDate = callDate;
    }

    public CallLogModel(String id, String callNumber, String callName, String duration, String callType, String callDate) {
        this.id = id;
        this.callNumber = callNumber;
        this.callName = callName;
        this.duration = duration;
        this.callType = callType;
        this.callDate = callDate;
    }

    public String getCallNumber() {
        return callNumber;
    }

    public String getCallName() {
        return callName;
    }

    public String getDuration() {
        return duration;
    }

    public String getCallType() {
        return callType;
    }

    public String getCallDate() {
        return callDate;
    }

}
