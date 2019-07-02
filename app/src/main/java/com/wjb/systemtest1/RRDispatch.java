package com.wjb.systemtest1;

import android.os.Handler;
import android.os.Message;

import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * Created by wjb on 2016/6/5.
 * 时间片调度算法
 */
public class RRDispatch extends ProcessDispatch {

    private int slotTimer;
    private boolean isOver;

    // 抽象方法：启动线程
    @Override
    public void startThread(List<Process> l) {
       startThread(l,1);
    }


    // 抽象方法：有时间片的启动线程
    @Override
    public void startThread(List<Process> l, final int slotSize) {
        list = l;
        index = 0;
        time = 0;
        lock = true;
        isRunning = false;
        isOver = false;
        slotTimer =0;

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                isRunning = true;
                while (lock) {
                    // 检查是否阻塞线程
                    pauseThread();
                    length = list.size()-1;
                    time++;

                    list.get(index).setState("进行");
                    list.get(index).setRunTime(list.get(index).getRunTime()+1);

                    slotTimer++;

                    handler.sendEmptyMessage(time);
                    // 如果达到需要时间，则设置进程状态为完成
                    if (list.get(index).getRunTime() >= list.get(index).getNeedtime()) {
                        list.get(index).setState("完成");
                        list.get(index).setEndTime(time);
                        // 寻找下一个运行进程
                        checkProcess();
                    }

                    // 如果到了时间片，则切换进程
                    if(slotTimer>=slotSize){
                        list.get(index).setState("就绪");
                        checkProcess();
                     }

                    try {
                        thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }  //循环结束
                isRunning = false;
            }
        });
        thread.start();
    }

    // 寻找进程列表中的就绪进程
    private void checkProcess(){
        slotTimer = 0;
        isOver = true;
        for(int i = index+1;i<=length;i++){
            if(!list.get(i).getState().equals("完成")){
                isOver = false;
                index = i;
                break;
            }
        }
        if(isOver){
            for(int i = 0;i<=index;i++){
                if(!list.get(i).getState().equals("完成")){
                    isOver = false;
                    index = i;
                    break;
                }
            }
        }
        if(isOver){
            lock = false;
            time = 0;
        }

    }

    // 检查是否在线程
    private void pauseThread(){
        if(suspend){
            synchronized (control) {
                try {
                    control.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            listener.nowTime(msg.what);
        }
    };
}
