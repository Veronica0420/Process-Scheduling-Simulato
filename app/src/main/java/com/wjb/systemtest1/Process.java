package com.wjb.systemtest1;

/**
 * Created by wjb on 2016/6/4.
 * PCB信息对象类
 */
public class Process implements Cloneable{

    private String name="未命名";
    private int priority=10;
    private int needTime=0;  //需要的时间
    private int runTime=0;
    private int startTime = 0;
    private int endTime = 0;
    private String state="就绪";

    public Process(String name,int priority,int needTime){
        this.name = name;
        this.priority = priority;
        this.needTime = needTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getRunTime() {
        return runTime;
    }

    public void setRunTime(int runTime) {
        this.runTime = runTime;
    }

    public int getNeedtime() {
        return needTime;
    }

    public void setNeedtime(int needTime) {
        this.needTime = needTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    @Override
    protected Process clone() throws CloneNotSupportedException {
        try {
            Process p = (Process) super.clone();  //浅度克隆  地址改变，但时内容不变
            p.name = this.name;
            p.priority = this.priority;
            p.needTime = this.needTime;
            p.runTime = this.runTime;
            p.state = this.state;
            p.startTime = this.startTime;
            p.endTime = this.endTime;
            return p;
        }catch (Exception e){

        }
        return null;
    }



}
