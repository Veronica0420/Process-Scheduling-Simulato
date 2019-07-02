package com.wjb.systemtest1;


import java.util.List;

/**
 * Created by wjb on 2016/6/5.
 * 调度算法的抽象父类
 */
public abstract class ProcessDispatch  {

    protected DispatchListener listener;  //回调
    protected Thread thread = null;
    protected boolean suspend = false;   //true  阻塞
    protected String control = "";
    protected boolean isRunning = false;
    protected boolean lock = true;  //lock 终止进程
    protected int index;
    protected int length;
    protected int time;
    protected List<Process> list;

    // 抽象方法：启动线程
    public abstract void startThread(List<Process> l);
   // 当子类继承抽象类时，必须要将抽象类中的抽象方法全部实现（或者称为重写）
    //抽象类的特点：必须有abstract关键字修饰，不可以通过new来创建对象，抽象方法不可以写函数体（非抽象方法必须写函数体）

    // 抽象方法：有时间片的启动线程
    public abstract void startThread(List<Process> l,int slot);

    // 注册监听器
    public void setDispatchListener(DispatchListener listener){
        this.listener = listener;
    }

    // 检查是否线程是否挂起
    public boolean isSuspend(){
        return suspend;
    }

    // 阻塞线程
    public void pause(){
        suspend = true;
    }

    // 启动线程
    public void start(){
        if(suspend){
            synchronized (control) {
                control.notify();
                suspend = false;
            }
        }
    }

    // 检查线程是否存在
    public boolean isRunning(){
        return isRunning;
    }

    // 终止线程
    public void stop(){
        if(isRunning()){
            lock = false;
        }
    }

    // 被优先级调度算法覆盖的动态插入进程算法
    public void InsertProcess(Process p){
        return ;
    }
}
