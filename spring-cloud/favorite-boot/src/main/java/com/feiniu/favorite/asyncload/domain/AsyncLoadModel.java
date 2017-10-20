package com.feiniu.favorite.asyncload.domain;

import java.io.Serializable;

/**
 * 一个asyncLoad的对象model，返回的对象
 * 
 */
public class AsyncLoadModel implements Serializable {

    private static final long serialVersionUID = -5410019316926096126L;

    public AsyncLoadModel(int id, String name, String detail){
        this.id = id;
        this.name = name;
        this.detail = detail;
    }
    
    public AsyncLoadModel(){
        this.id = 0;
        this.name = "0";
        this.detail = "0";
    }
    public int    id;
    public String name;
    public String detail;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "AsyncLoadModel [detail=" + detail + ", id=" + id + ", name=" + name + "]";
    }
    
    public static void main(String[] args) {
        AsyncLoadModel asyncLoadModel = new AsyncLoadModel();
        System.out.println(asyncLoadModel.getDetail());
    }
}
