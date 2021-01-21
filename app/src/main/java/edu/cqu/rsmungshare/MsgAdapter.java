package edu.cqu.rsmungshare;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;

import edu.cqu.rsmungshare.bean.MsgBean;
import edu.cqu.rsmungshare.bean.MsgCollector;

public class MsgAdapter extends BaseAdapter {
    private final String TAG = "MsgAdapter";
    //布局类型
    public static final int TYPE_TEXT_Pc2App_LEFT = 0;//PC给APP的消息
    public static final int TYPE_TEXT_App2Pc_RIGHT = 1;//APP给PC的消息
    public static final int TYPE_IMAGE_Pc2App_LEFT = 2;
    public static final int TYPE_IMAGE_App2Pc_RIGHT = 3;
    public static final int TYPE_FILE_Pc2App_LEFT = 4;
    public static final int TYPE_FILE_App2Pc_RIGHT = 5;
    private Context context;
    private String local_ip;
    private String type;
    private int screenWidth = -1;
    //构造函数
    public MsgAdapter(Context context){
        this.context = context;
        //获取本机IP地址
        SharedPreferences sp_local_ip = context.getSharedPreferences("local_ip",Context.MODE_PRIVATE);
        local_ip = sp_local_ip.getString("local_ip","NULL");
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
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
        MsgBean msgBean = MsgCollector.getMsg(position);
        String source = msgBean.getSource();
        String type = msgBean.getType();
        if(type.equals("text")){
            if(!source.equals("NULL") && !source.equals(local_ip)){
                //不是本机发送的文本消息,是接收到的文本消息
                return TYPE_TEXT_Pc2App_LEFT;
            }
            return TYPE_TEXT_App2Pc_RIGHT;
        }else if(type.equals("image")){
            if(!source.equals("NULL") && !source.equals(local_ip)){
                //不是本机发送的图片消息,是接收到的图片消息
                return TYPE_IMAGE_Pc2App_LEFT;
            }
            return TYPE_IMAGE_App2Pc_RIGHT;
        }else {
            if(!source.equals("NULL") && !source.equals(local_ip)){
                //不是本机发送的文件消息,是接收到的文件消息
                return TYPE_FILE_Pc2App_LEFT;
            }
            return TYPE_FILE_App2Pc_RIGHT;
        }
    }

    /*返回布局种类的数量*/
    @Override
    public int getViewTypeCount() {
        return 6;
    }

    private int type2ResourceId(int type){
        switch (type){
            //文本
            case TYPE_TEXT_Pc2App_LEFT:
                return R.layout.item_text_left;
            case TYPE_TEXT_App2Pc_RIGHT:
                return R.layout.item_text_right;
                //图片
            case TYPE_IMAGE_Pc2App_LEFT:
                return R.layout.item_image_left;
            case TYPE_IMAGE_App2Pc_RIGHT:
                return R.layout.item_image_right;
                //文件
            case TYPE_FILE_Pc2App_LEFT:
                return R.layout.item_file_right;
            case TYPE_FILE_App2Pc_RIGHT:
                return R.layout.item_file_left;
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
            if(type == TYPE_TEXT_App2Pc_RIGHT || type == TYPE_TEXT_Pc2App_LEFT){
                //文本
                viewHolder.tvContent = view.findViewById(R.id.text);
            }else if(type == TYPE_IMAGE_App2Pc_RIGHT || type == TYPE_IMAGE_Pc2App_LEFT){
                //图片
                viewHolder.image = view.findViewById(R.id.image);
            }else{
                //文件
                viewHolder.file_name = view.findViewById(R.id.file_name);
                viewHolder.file_size = view.findViewById(R.id.file_size);
            }
            view.setTag(viewHolder);
        }
        //把数据加载进布局
        if(msg != null && viewHolder != null){
            viewHolder.time.setText(msg.getTime());
            if(type == TYPE_TEXT_App2Pc_RIGHT || type == TYPE_TEXT_Pc2App_LEFT){
                //文本
                viewHolder.tvContent.setText(msg.getTvContent());
            }else if(type == TYPE_IMAGE_App2Pc_RIGHT || type == TYPE_IMAGE_Pc2App_LEFT){
                //图片
                //不卡顿,宽度在布局中写死了为250dp
                //原图显示
                Glide.with(context).load(Uri.fromFile(new File(msg.getTvContent()))).into(viewHolder.image);
//                //缩放显示,卡顿
//                final ImageView i = viewHolder.image;
//                Glide.with(context).load(Uri.fromFile(new File(msg.getTvContent()))).into(
//                        new SimpleTarget<Drawable>() {
//                            @Override
//                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                                int image_w = resource.getIntrinsicWidth();
//                                int image_h = resource.getIntrinsicHeight();
//                                int new_w = screenWidth / 2;//图片的宽度设置为屏幕宽度的一半
//                                //缩放比例
//                                float scale_ratio = (float)new_w / image_w;
//                                ViewGroup.LayoutParams params = i.getLayoutParams();
//                                params.width = new_w;
//                                params.height = (int) (image_h * scale_ratio);
//                                i.setLayoutParams(params);
//                                i.setImageDrawable(resource);
//                            }
//                        }
//                );
//                //卡顿
//                Bitmap bitmap = BitmapFactory.decodeFile(msg.getTvContent());
//                if(bitmap != null){
//                    viewHolder.image.setImageBitmap(bitmap);
//                }else{
//                    Log.w(TAG,"在MsgAdapter中获取的要显示的图片为空");
//                }
            }else{
                //文件
            }
        }
        return view;
    }

    static class ViewHolder{
        TextView time;
        TextView tvContent;
        ImageView image;
        TextView file_name;
        TextView file_size;
    }
}
