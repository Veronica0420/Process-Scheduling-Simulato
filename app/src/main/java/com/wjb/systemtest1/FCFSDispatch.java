package com.wjb.systemtest1;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * Created by wjb on 2016/6/5.
 * 先来先服务调度算法
 */
public class FCFSDispatch extends ProcessDispatch {

    @Override
    public void startThread(List<Process> l) {

        list = l;
        index = 0;
        time = 0;
        lock = true;
        isRunning = false;

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

                        // 若达到需要时间，则设置状态为完成
                        if (list.get(index).getRunTime() >= list.get(index).getNeedtime()) {
                            list.get(index).setState("完成");
                            list.get(index).setEndTime(time);
                            index++;
                        }

                        handler.sendEmptyMessage(time);
                        // 如果遍历完列表，则结束调度
                        if(index>length){
                            lock=false;
                            time = 0;
                        }
                        try {
                            thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    isRunning = false;
                }
            });
        thread.start();
    }

    @Override
    public void startThread(List<Process> l, int slot) {
        startThread(l);
    }

    // 阻塞线程
    public void pauseThread(){
        // 如果挂起标志suspend为true,则阻塞线程
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
