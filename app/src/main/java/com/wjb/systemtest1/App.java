package com.wjb.systemtest1;

import android.app.Application;
import android.content.Context;

/**
 * Created by wjb on 2016/6/5.
 */
public class App extends Application {

    private static Context context;

    public void App(){
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}
