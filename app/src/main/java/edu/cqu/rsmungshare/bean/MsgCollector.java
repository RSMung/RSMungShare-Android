package edu.cqu.rsmungshare.bean;

import android.util.Log;

import java.util.ArrayList;

public class MsgCollector {
    private static final String TAG = "MsgCollector";
    private static ArrayList<MsgBean> list = new ArrayList<>();
    public static int size(){
        return list.size();
    }
    public static void addMsg(MsgBean msgBean){
        list.add(msgBean);
    }
    public static MsgBean getMsg(int index){
        if(index >= list.size()){
            Log.w(TAG,"数组下标越界!");
            throw new RuntimeException("数组下标越界!");
        }
        return list.get(index);
    }
    public static ArrayList<MsgBean> getList(){
        return list;
    }
}
