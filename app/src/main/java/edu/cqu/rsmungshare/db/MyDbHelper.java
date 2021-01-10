package edu.cqu.rsmungshare.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDbHelper extends SQLiteOpenHelper {
    private static final String CREATE_MsgTable =
            "create table Msg ("
                    + "seq integer primary key autoincrement, "//序号
                    + "type text, "//消息类型
                    + "time text,"//时间记录
                    + "source text,"//消息源ip
                    + "target text,"//消息目标ip
                    + "size text, "//消息大小
                    + "tvContent text,"//文本内容
                    + "imagePath text,"//图片路径
                    + "filePath text)";//文件路径
    private Context mContext;
    public MyDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MsgTable);//建表
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
