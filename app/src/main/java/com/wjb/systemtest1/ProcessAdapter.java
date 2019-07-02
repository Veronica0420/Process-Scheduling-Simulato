package com.wjb.systemtest1;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by wjb on 2016/6/4.
 * 进程列表设配器
 */
public class ProcessAdapter extends BaseAdapter{

    private Context context;
    private List<Process> processList;

    // 通过构造方法将数据源与数据适配器关联起来
    // context:要使用当前的Adapter的界面对象
    public ProcessAdapter(Context context,List<Process> list){

        this.context = context;
        processList = list;

    }
    @Override
    //ListView需要显示的数据数量
    public int getCount() {
        return processList.size();
    }

    @Override
    //指定的索引对应的数据项
    public Object getItem(int i) {
        return processList.get(i);
    }

    @Override
    //指定的索引对应的数据项ID
    public long getItemId(int i) {
        return i;
    }

    @Override
    //返回每一项的显示内容
    public View getView(int i, View view, ViewGroup viewGroup) {
        //将布局文件转化为View对象
        View root = LayoutInflater.from(context).inflate(R.layout.list_item,null);
        TextView tvName = (TextView) root.findViewById(R.id.item_name);
        TextView tvPriority = (TextView) root.findViewById(R.id.item_priority);
        TextView tvNeedTime = (TextView) root.findViewById(R.id.item_need_time);
        TextView tvRuntime = (TextView) root.findViewById(R.id.item_runtime);
        TextView tvState = (TextView) root.findViewById(R.id.item_state);

        tvName.setText(processList.get(i).getName());
        tvPriority.setText(processList.get(i).getPriority()+"");
        tvNeedTime.setText(processList.get(i).getNeedtime()+"s");
        tvRuntime.setText(processList.get(i).getRunTime()+"s");
        tvState.setText(processList.get(i).getState());

        if(processList.get(i).getState().equals("进行")){
            root.setBackgroundColor(Color.parseColor("#D1EEEE"));
        }
        if(processList.get(i).getState().equals("就绪")
                ||processList.get(i).getState().equals("完成")){
            root.setBackgroundColor(Color.WHITE);
        }

        return root;
    }
}
