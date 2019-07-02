package com.wjb.systemtest1;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener{

    // 状态码
    private final static int FCFS = 0;
    private final static int PRIORITY = 1;
    private final static int RR = 2;

    private ProcessAdapter processAdapter = null;
    private List<Process> processList = new ArrayList<Process>();
    //选择算法
    private final String[] items = {"先来先服务调度","优先级调度","时间片调度"};
    private int dispatch = FCFS;
    private ProcessDispatch dispatchMathod = new FCFSDispatch();

    private EditText etSlot = null;  //输入时间片长度
    private TextView tvRuntime = null;  //已运行
    private int nowTime = 0;
    private List<Process> copyList = new ArrayList<Process>();
    private Button btnStart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etSlot = (EditText) findViewById(R.id.et_slot);
        tvRuntime = (TextView) findViewById(R.id.tv_runtime);

        Button btnChoice = (Button) findViewById(R.id.btn_choice);
        Button btnReset = (Button) findViewById(R.id.btn_reset);

        ListView lvProcess = (ListView) findViewById(R.id.lv_process);

        Button btnAdd = (Button) findViewById(R.id.btn_add);
        btnStart = (Button) findViewById(R.id.btn_start);

        //注册监听
        btnChoice.setOnClickListener(this);
        btnReset.setOnClickListener(this);

        btnAdd.setOnClickListener(this);
        btnStart.setOnClickListener(this);


        initData();
        processAdapter = new ProcessAdapter(this,processList);
        lvProcess.setAdapter(processAdapter);
        lvProcess.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_choice:
                // 选择进程调度算法
                AlertDialog choiceDialog = new AlertDialog.Builder(this)
                        .setTitle("调度算法选择")
                        .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                TextView tv_dispatch = (TextView) findViewById(R.id.tv_dispatch);
                                tv_dispatch.setText(items[i]);
                                dispatch = i;

                                // 策略模式指定调度算法，依赖抽象类
                                if(dispatch==RR){
                                    etSlot.setEnabled(true);
                                    dispatchMathod = new RRDispatch();
                                }
                                if (dispatch==FCFS){
                                    dispatchMathod = new FCFSDispatch();
                                    etSlot.setEnabled(false);
                                }
                                if(dispatch==PRIORITY){
                                    dispatchMathod = new PriorityDispatch();
                                    etSlot.setEnabled(false);
                                }
                            }
                        })
                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .create();
                choiceDialog.show();
                break;
            case R.id.btn_add:
                // 添加进程时阻塞线程
                if(dispatchMathod.isRunning()){
                    dispatchMathod.pause();
                }
                View root = LayoutInflater.from(this).inflate(R.layout.dialog_add,null);
                final EditText etDialogName = (EditText) root.findViewById(R.id.et_dialog_name);
                final EditText etDialogPriority = (EditText) root.findViewById(R.id.et_dialog_priority);
                final EditText etDialogNeedtime = (EditText) root.findViewById(R.id.et_dialog_needtime);
                Button btnDialogSure = (Button) root.findViewById(R.id.btn_dialog_sure);

                final AlertDialog addDialog = new AlertDialog.Builder(this)
                        .setView(root)
                        .create();
                addDialog.show();
                btnDialogSure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = etDialogName.getText().toString();

                        // 检查输入信息正确性
                        if(name.equals("")|| etDialogPriority.getText().toString().equals("")
                                ||etDialogNeedtime.getText().toString().equals("")){
                            Toast.makeText(MainActivity.this,"请输入完整信息",Toast.LENGTH_SHORT).show();
                            return ;
                        }
                        int priority = Integer.valueOf(etDialogPriority.getText().toString());
                        if(priority<1||priority>10){
                            Toast.makeText(MainActivity.this,"优先级范围为1-10",Toast.LENGTH_SHORT).show();
                            return ;
                        }
                        int needTime = Integer.valueOf(etDialogNeedtime.getText().toString());
                        if(needTime<=0){
                            Toast.makeText(MainActivity.this,"运行时间不能小于0",Toast.LENGTH_SHORT).show();
                            return ;
                        }

                        Process process = new Process(name,priority,needTime);
                        process.setStartTime(nowTime);
                        processList.add(process);
                        processAdapter.notifyDataSetChanged();
                        addDialog.dismiss();

                        // 若已备份初始进程信息，插入新来的进程
                        if(copyList.size()>0){
                            try {
                                copyList.add(process.clone());
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }
                        }

                        // 若是优先级调度，需调整优先级列表
                        if(dispatch==PRIORITY){
                            dispatchMathod.InsertProcess(process);
                        }
                        // 如果线程还存在，则唤醒线程
                        if(dispatchMathod.isRunning()){
                            dispatchMathod.start();
                        }
                    }
                });
                break;
            case R.id.btn_start:
                // 线程未启动时，执行方法体，避免创建多个进程
                if(!dispatchMathod.isRunning()) {
                    // 先备份进程列表数据
                    copyList();
                    // 注册监听器
                    dispatchMathod.setDispatchListener(new DispatchListener() {
                        @Override
                        public void nowTime(int s) {
                            nowTime = s;
                            tvRuntime.setText(s+" 秒");
                            processAdapter.notifyDataSetChanged();
                        }
                    });

                    // 启动线程
                    int slot = checkSlot();
                    dispatchMathod.startThread(processList,slot);
                    btnStart.setEnabled(false);
                }
                break;
            case R.id.btn_reset:
                // 先终止线程
                dispatchMathod.stop();
                // 如果有备份，则恢复原始信息
                if(copyList.size()>0) {
                    processList.clear();
                    for (Process p : copyList) {
                        processList.add(p);
                    }

                    processAdapter.notifyDataSetChanged();
                    tvRuntime.setText("0 秒");
                    nowTime = 0;
                    btnStart.setEnabled(true);
                }
                break;
        }
    }

    // 开始时数据
    private void initData(){
        //process队列,初始5个就绪
        Process p = new Process("a",5,3);
        processList.add(p);
        p = new Process("b",3,4);
        processList.add(p);
        p = new Process("c",6,3);
        processList.add(p);
        p = new Process("d",1,5);
        processList.add(p);
        p = new Process("e",3,2);
        processList.add(p);
    }

    // 备份数据
    private void copyList(){
        copyList.clear();
        for(Process p:processList){
            try {
                copyList.add(p.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
    }

    // 检查时间片后，返回时间片大小
    private int checkSlot(){
        int slot = 0;
        if(!etSlot.getText().toString().equals("")){
            slot = Integer.valueOf(etSlot.getText().toString());
        }else{
            // 若不输入时间片大小，时间片调度算法时间片默认大小为2
            if(dispatch == RR){
                slot = 2;
            }
        }
        return slot;
    }

    // 监听删除检查操作
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("删除提示")
                .setMessage("删除该进程？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        processList.remove(i);
                        processAdapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("取消",null)
                .create();
        dialog.show();
    }
}
