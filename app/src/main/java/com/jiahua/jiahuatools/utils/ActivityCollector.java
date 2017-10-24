package com.jiahua.jiahuatools.utils;
/*
 * Created by ZhiPeng Huang on 2017-10-23.
 */

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityCollector {
    public static List<Activity> activityList = new ArrayList<>();
    public static void addActivity(Activity activity){
        activityList.add(activity);
    }

    public static void removeActivity(Activity activity){
        activityList.remove(activity);
    }

    public static void finishAll(){
        for(Activity activity:activityList){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
