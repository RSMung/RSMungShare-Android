package edu.cqu.rsmungshare;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import edu.cqu.rsmungshare.bean.MsgBean;
import edu.cqu.rsmungshare.bean.MsgCollector;

public class TextMsgAdapter extends ArrayAdapter<MsgBean> {
    private int resourceId;//用来放置布局文件的id
    private Context context;

    public TextMsgAdapter(@NonNull Context context, int resource, ArrayList<MsgBean> msgBeans) {
        super(context, resource,msgBeans);
        this.context = context;
        this.resourceId = resource;
    }

    static class ViewHolder {
        TextView time;
        TextView text;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        MsgBean msgBean = getItem(position); // 获取当前项的实例
        View view;//子项布局对象
        ViewHolder viewHolder;
        if (convertView == null) {//如果是第一次加载
            view = LayoutInflater.from(context).inflate(resourceId, parent, false);//布局对象化
            viewHolder = new ViewHolder();
            viewHolder.time = view.findViewById(R.id.time);
            viewHolder.text = view.findViewById(R.id.text);
            view.setTag(viewHolder);
        } else {//不是第一次加载，即布局文件已经加载，可以利用
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        if(msgBean!=null && viewHolder!= null){
            viewHolder.time.setText(msgBean.getTime());
            viewHolder.text.setText(msgBean.getTvContent());
        }
        return view;
    }
}
