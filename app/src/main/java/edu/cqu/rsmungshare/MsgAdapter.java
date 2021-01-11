package edu.cqu.rsmungshare;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import edu.cqu.rsmungshare.bean.MsgBean;
import edu.cqu.rsmungshare.bean.MsgCollector;

public class MsgAdapter extends BaseAdapter {
    private final String TAG = "MsgAdapter";
    //布局类型
    public static final int TYPE_TEXT_Pc2App_LEFT = 0;//PC给APP的消息
    public static final int TYPE_TEXT_App2Pc_RIGHT = 1;//APP给PC的消息
    public static final int TYPE_IMAGE_LEFT = 2;
    public static final int TYPE_IMAGE_RIGHT = 3;
    public static final int TYPE_FILE_LEFT = 4;
    public static final int TYPE_FILE_RIGHT = 5;
    private Context context;
    private String local_ip;
    //构造函数
    public MsgAdapter(Context context){
        this.context = context;
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
        return MsgCollector.getMsg(position);
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
        String source = MsgCollector.getMsg(position).getSource();
        if(!source.equals("NULL") && !source.equals(local_ip)){
            //不是本机发送的文本消息,是接收到的文本消息
            return TYPE_TEXT_Pc2App_LEFT;
        }
        return TYPE_TEXT_App2Pc_RIGHT;
    }

    /*返回布局种类的数量*/
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    private int type2ResourceId(int type){
        switch (type){
            case TYPE_TEXT_Pc2App_LEFT:
                return R.layout.item_text_left;
            case TYPE_TEXT_App2Pc_RIGHT:
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
        //获取消息类型
        int type = getItemViewType(position);
        if(convertView != null){//复用缓存
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }else {//新加载
            //获取布局id
            view = LayoutInflater.from(context).inflate(type2ResourceId(type),parent,false);
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
