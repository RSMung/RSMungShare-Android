package edu.cqu.rsmungshare.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.lang.ref.WeakReference;

public class MyDbFunctions {
    //数据库名
    public static final String DB_NAME = "MsgRecord";
    //数据库版本
    public static final int VERSION = 1;
    private volatile static MyDbFunctions myDbFunctions;
    private SQLiteDatabase db;
    private WeakReference<Context> weakReference;//弱引用方式引入context
    //私有化构造方法,单例模式
    private MyDbFunctions(Context context){
        weakReference = new WeakReference<>(context);
        db = new MyDbHelper(weakReference.get(),DB_NAME,null,VERSION).getWritableDatabase();
    }
    /*双重锁模式*/
    public static MyDbFunctions getInstance(Context context){
        if(myDbFunctions == null){//为了避免不必要的同步
            synchronized (MyDbFunctions.class){
                if(myDbFunctions ==null){//为了在实例为空时才创建实例
                    myDbFunctions = new MyDbFunctions(context);
                }
            }
        }
        return myDbFunctions;
    }
}
