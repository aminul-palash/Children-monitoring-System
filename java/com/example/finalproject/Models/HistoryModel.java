package com.example.finalproject.Models;

public class HistoryModel{
    private  String id;
    private  String domain;
    private String url;



    public HistoryModel(){}
    /// public History(ShowHistory showHistory, List<History> historyList) {}

    public HistoryModel(String id,String domain, String url) {
        this.domain = domain;
        this.url = url;
        this.id = id;
    }

    public HistoryModel(String domain, String url) {
        this.domain = domain;
        this.url = url;
    }

    public String getDomain() {
        return domain;
    }

    public String getUrl() {
        return url;
    }
    public  String getId(){
        return  id;
    }

}

