package com.bxtdata.interview.interview.task1.entity;


import java.util.List;

public class Sample {
    private String id;
    private String taskId;
    private List<Response> data;

    public Sample() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public List<Response> getData() {
        return data;
    }

    public void setData(List<Response> data) {
        this.data = data;
    }
}
