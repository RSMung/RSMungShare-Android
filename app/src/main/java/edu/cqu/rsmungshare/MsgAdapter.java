package edu.cqu.rsmungshare;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.cqu.rsmungshare.bean.MsgBean;
import edu.cqu.rsmungshare.bean.MsgCollector;

public class MsgAdapter extends BaseAdapter {
    private final String TAG = "MsgAdapter";
    //布局类型
    public static final int TYPE_TEXT_LEFT = 0;
    public static final int TYPE_TEXT_RIGHT = 1;
    public static final int TYPE_IMAGE_LEFT = 2;
    public static final int TYPE_IMAGE_RIGHT = 3;
    public static final int TYPE_FILE_LEFT = 4;
    public static final int TYPE_FILE_RIGHT = 5;
    private Context context;
    private ArrayList<MsgBean> data;
    private String local_ip;
    //构造函数
    public MsgAdapter(Context context,ArrayList<MsgBean> data){
        this.data = data;
        this.context = context;
//        this.data = list;
        //获取本机IP地址
        SharedPreferences sp_local_ip = context.getSharedPreferences("local_ip",Context.MODE_PRIVATE);
        local_ip = sp_local_ip.getString("local_ip","NULL");
    }
    /*返回数据的总数量*/
    @Override
    public int getCount() {
        return MsgCollector.size();
    }

    /*返回指定下标对应的数据对象*/
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    /*指定的索引对应的数据项ID*/
    @Override
    public long getItemId(int position) {
        return position;
    }

    /*返回当前Item布局id
    * getViewTypeCount() 的值必须大于 getItemViewType() 的值*/
    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    /*返回布局种类的数量*/
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    private int typeToResourceId(int type){
        switch (type){
            case TYPE_TEXT_LEFT:
                return R.layout.item_text_left;
            case TYPE_TEXT_RIGHT:
                return R.layout.item_text_right;
        }
        return R.layout.item_text_right;
    }

    /*每个Item所显示的内容*/
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;
        MsgBean msg = (MsgBean) getItem(position);
        if(convertView != null){//复用缓存
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }else {//新加载
            //获取布局id
            int type = TYPE_TEXT_RIGHT;
            String source = "NULL";
            if(msg != null){
                source = msg.getSource();
            }else {
                Log.w(TAG,"获取到的MsgBean对象为空,position可能没有通过数组下标越界检查");
            }
            Log.w(TAG,"source:"+source+" local_ip:"+local_ip);
            if(!source.equals("NULL") && !source.equals(local_ip)){
                //不是本机发送的文本消息,是接收到的文本消息
                type = TYPE_TEXT_LEFT;
            }
            view = LayoutInflater.from(context).inflate(typeToResourceId(type),parent,false);
            viewHolder = new ViewHolder();
            viewHolder.time = view.findViewById(R.id.time);
            viewHolder.text = view.findViewById(R.id.text);
            view.setTag(viewHolder);
        }
        //把数据加载进布局
        if(msg != null && viewHolder != null){
            viewHolder.time.setText(msg.getTime());
            viewHolder.text.setText(msg.getTvContent());
        }
        return view;
    }

    static class ViewHolder{
        TextView time;
        TextView text;
    }
}
