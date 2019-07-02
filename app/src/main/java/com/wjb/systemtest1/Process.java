package com.wjb.systemtest1;

/**
 * Created by wjb on 2016/6/4.
 * PCB信息对象类
 */
public class Process implements Cloneable{

    private String NAME="未命名";  //进程标志符
    private int PRIO=10;  //进程优先数
    private int NEEDTIME=0;  //进程到完成还要的CPU时间
    private int CPUTIME=0;  //进程占用CPU时间
    private int startTime = 0;
    private int endTime = 0;
    private String STATE="就绪";

    public Process(String NAME,int PRIO,int NEEDTIME){
        this.NAME = NAME;
        this.PRIO = PRIO;
        this.NEEDTIME = NEEDTIME;
    }


    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public int getPRIO() {
        return PRIO;
    }

    public void setPRIO(int PRIO) {
        this.PRIO = PRIO;
    }

    public int getNEEDTIME() {
        return NEEDTIME;
    }

    public void setNEEDTIME(int NEEDTIME) {
        this.NEEDTIME = NEEDTIME;
    }

    public int getCPUTIME() {
        return CPUTIME;
    }

    public void setCPUTIME(int CPUTIME) {
        this.CPUTIME = CPUTIME;
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
            p.NAME = this.NAME;
            p.PRIO = this.PRIO;
            p.NEEDTIME = this.NEEDTIME;
            p.CPUTIME = this.CPUTIME;
            p.STATE = this.STATE;
            p.startTime = this.startTime;
            p.endTime = this.endTime;
            return p;
        }catch (Exception e){

        }
        return null;
    }


}
