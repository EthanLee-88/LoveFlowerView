package com.example.loveflowerview;

import android.app.Application;
import android.content.Context;

public class FlowerApplication extends Application {

    private static Context applicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
    }

    public static Context getFlowerApplicationContext(){
        return applicationContext;
    }

}
