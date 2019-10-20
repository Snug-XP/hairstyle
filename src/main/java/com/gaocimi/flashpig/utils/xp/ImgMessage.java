package com.gaocimi.flashpig.utils.xp;

import java.util.Map;

public class ImgMessage {
    private String status;
    private Map<String,Object> data;
 
    public ImgMessage(String status, Map<String, Object> data) {
        this.status = status;
        this.data = data;
    }
 
    public ImgMessage() {
    }
 
    public String getStatus() {
        return status;
    }
 
    public void setStatus(String status) {
        this.status = status;
    }
 
    public Map<String, Object> getData() {
        return data;
    }
 
    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
