package edu.cqu.rsmungshare.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.cqu.rsmungshare.MsgAdapter;
import edu.cqu.rsmungshare.MyObjectInputStream;
import edu.cqu.rsmungshare.R;
import edu.cqu.rsmungshare.bean.MsgBean;
import edu.cqu.rsmungshare.bean.MsgCollector;
import edu.cqu.rsmungshare.model.ActivityCollector;
import edu.cqu.rsmungshare.model.BaseActivity;
import edu.cqu.rsmungshare.myview.LoadingAnimation;

public class MainActivity extends BaseActivity {
    private final String TAG = "MainActivity";
    private final int send_port = 1998;//端口号
    private final int receive_port = 1999;
    private ListView listView;
//    private TextMsgAdapter adapter;
    private MsgAdapter adapter;
    private int image_index = 0;//因发送图片时可能有多张,这个变量用来记录发送到第几张了
    private ArrayList<String> images_paths = null;//图片的路径
    private LoadingAnimation loadingAnimation;//加载动画
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dealToolbarBtn();
        inputTextMsgBar();
        initListView();
        startReceiveMsg();
        //选择图片按钮
        ImageView btn_select_image = findViewById(R.id.btn_select_image);
        btn_select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, SelectImageActivity.class), 0);
            }
        });
        loadingAnimation = findViewById(R.id.loading_animation);
    }
 
    /*toolbar的按钮*/
    private void dealToolbarBtn(){
        //返回按钮
        ImageView btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.finishAll();
            }
        });
        //菜单按钮
        ImageView btn_menu = findViewById(R.id.menu);
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                intent.putExtra("FROM_MAIN","TRUE");//表示是从MAIN启动的,就不检查是否直接进入传输界面了
                startActivity(intent);
            }
        });
    }

    /*发送文字消息*/
    private void inputTextMsgBar(){
        final EditText et_msg = findViewById(R.id.input);
        final Button btn_send = findViewById(R.id.btn_send);
        et_msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length() > 0){
                    btn_send.setBackground(getDrawable(R.drawable.round_corner_blue_dark));
                }else{
                    btn_send.setBackground(getDrawable(R.drawable.round_corner_blue_shallow));
                }
            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tvContent = et_msg.getText().toString();
                if(tvContent.length() > 0){
                    sendMsg("text", tvContent, null);
                    et_msg.setText("");//清空输入框
                    //收起软键盘
                    InputMethodManager manager = ((InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE));
                    if (manager != null)
                        manager.hideSoftInputFromWindow(et_msg.getWindowToken(), 0);
                    et_msg.clearFocus();//清除焦点
                }
            }
        });
    }

    /*
     * 向目标ip地址发送消息
     * Params：开始异步任务执行时传入的参数类型；
     * Progress：异步任务执行过程中，返回进度值的类型；
     * Result：异步任务执行完成后，返回的结果类型
     * */
    private static class SendMsgTask extends AsyncTask<MsgBean,Integer,Boolean>{
        private WeakReference<MainActivity> weakReference;
        //构造函数
        public SendMsgTask(MainActivity activity){
            this.weakReference = new WeakReference<>(activity);
        }
        //1.在后台任务开始执行之间调用，在主线程执行
        @Override
        protected void onPreExecute() {
            MainActivity activity = weakReference.get();
            activity.loadingAnimation.start();
            activity.loadingAnimation.setVisibility(View.VISIBLE);
        }

        //2.在子线程中运行，处理耗时任务
        @Override
        protected Boolean doInBackground(MsgBean... msgBeans) {
            MainActivity activity = weakReference.get();
            //通过网络发送消息
            try {
                if(msgBeans[0].getTarget() == null || msgBeans[0].getTarget().equals("NULL")){
                    //目标IP为空
                    return false;
                }
                Socket socket = new Socket(msgBeans[0].getTarget(),activity.send_port);
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject(msgBeans[0]);
                outputStream.flush();
                outputStream.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            MsgCollector.addMsg(msgBeans[0]);
            return true;
        }

        //在这个方法中可以对UI进行操作，在主线程中进行
        @Override
        protected void onProgressUpdate(Integer... integers) {
        }

        //返回的数据会作为参数传递到此方法中，可以利用返回的数据来进行一些UI操作，在主线程中进行
        @Override
        protected void onPostExecute(Boolean result) {
            final MainActivity activity = weakReference.get();
            activity.initListView();
            //开始发送下一个图片
            activity.image_index++;
            if(activity.image_index < activity.images_paths.size()) {
                String[] name_array = activity.images_paths.get(activity.image_index).split("/");
                String name = name_array[name_array.length - 1];
                activity.sendMsg("image", name, activity.images_paths.get(activity.image_index));
            }else{
//                //增强体验,加载动画不会太快消失
//                CountDownTimer countDownTimer = new CountDownTimer(1000,500) {
//                    @Override
//                    public void onTick(long millisUntilFinished) {
//                    }
//
//                    @Override
//                    public void onFinish() {
//                        activity.loadingAnimation.setVisibility(View.GONE);
//                    }
//                };
//                countDownTimer.start();
                activity.loadingAnimation.setVisibility(View.GONE);
                activity.image_index = 0;//不置0下次传输的时候一开始就是5,会报错
            }
        }
    }

    /*接收消息子线程*/
    private static class ReceiveMsgThread extends Thread{
        private WeakReference<Handler> weakReference;
        //构造函数
        public ReceiveMsgThread(Handler handler) {
            this.weakReference = new WeakReference<>(handler);
        }

        @Override
        public void run() {
            super.run();
            int receive_port = 1999;
            final String TAG = "ReceiveMsgThread";
            Handler handler = weakReference.get();
            try {
                while(true) {
                    ServerSocket serverSocket = new ServerSocket(receive_port);
                    Log.w(TAG,"开始阻塞");
                    Socket socket = serverSocket.accept();//阻塞等待
                    Log.w(TAG,"接收新消息");
                    MyObjectInputStream objectInputStream = new MyObjectInputStream(socket.getInputStream());
                    MsgBean msgBean = (MsgBean) objectInputStream.readObject();
                    MsgCollector.addMsg(msgBean);
                    serverSocket.close();
                    socket.close();
                    //通知更新UI
                    Message message = new Message();
                    message.what = 0;
                    message.arg1 = 1;
                    handler.sendMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private static class UpdateUiHandler extends Handler{
        private WeakReference<MainActivity> weakReference;
        UpdateUiHandler(MainActivity activity){
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = weakReference.get();
            if(msg.what == 0 && msg.arg1 == 1){
//                activity.flushListView();
                activity.initListView();
            }
        }
    }

    /*初始化listview显示消息记录*/
    private void initListView(){
        if(listView == null)
            listView = findViewById(R.id.content);
//        adapter = new TextMsgAdapter(this,R.layout.item_text_right,MsgCollector.getList());
        if(adapter == null)
            adapter = new MsgAdapter(this);
        listView.setAdapter(adapter);
        //滑动到底部
        listView.setSelection(listView.getBottom());
    }

    /*开始接收消息*/
    private void startReceiveMsg(){
        new ReceiveMsgThread(new UpdateUiHandler(this)).start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityCollector.finishAll();//本界面返回按钮直接退出应用
    }

    @Override
    protected void onActivityResult(int request_code, int result_code, Intent data){
        if(result_code == RESULT_OK){
//            //单选图片时候的代码
//            String selected_image_path = data.getStringExtra("selected_image_path");
//            Toast.makeText(MainActivity.this,"开始发送图片",Toast.LENGTH_SHORT).show();
//            String[] name_array = selected_image_path.split("/");
//            String name = name_array[name_array.length - 1];
//            sendMsg("image", name, selected_image_path);
            images_paths = data.getStringArrayListExtra("images_paths");
            Toast.makeText(MainActivity.this,"开始发送图片",Toast.LENGTH_SHORT).show();
            String[] name_array = images_paths.get(image_index).split("/");
            String name = name_array[name_array.length - 1];
            sendMsg("image", name, images_paths.get(image_index));
        }
    }

    /*
    * type: 消息类型
    * len： 消息长度
    * tvContent: 消息内容---文本   或者是图片名称、文件名称
    * path: 图片路径或者文件路径
    * */
    private void sendMsg(String type, String tvContent, String path){
        //获取当前时间
        Date date = new Date();
        SimpleDateFormat s = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String time = s.format(date);
        //获取当前IP和目标IP
        SharedPreferences sp_local_ip = getSharedPreferences("local_ip",MODE_PRIVATE);
        String local_ip = sp_local_ip.getString("local_ip","NULL");
        SharedPreferences sp_target_ip = getSharedPreferences("target_ip",MODE_PRIVATE);
        String target_ip = sp_target_ip.getString("target_ip","NULL");
        MsgBean msgBean = null;
        //文本消息
        if(type.equals("text")){
            msgBean = new MsgBean(
                    MsgCollector.size(),
                    type,
                    time,
                    local_ip,
                    target_ip,
                    tvContent.length(),
                    tvContent,
                    null,
                    null
            );
        }else if(type.equals("image")){
            //图片消息
            try{
                FileInputStream fs = new FileInputStream(path);
                int image_data_len = fs.available();
//                Log.w(TAG,"图片大小"+image_data_len);
                byte[] buffer = new byte[ image_data_len];
                fs.read(buffer);
                //新建MsgBean对象
                msgBean = new MsgBean(
                        MsgCollector.size(),
                        type,
                        time,
                        local_ip,
                        target_ip,
                        image_data_len,
                        tvContent,
                        buffer,
                        null
                );
                fs.close();
            }catch (IOException e){
                Log.w(TAG,e);
            }
        }
        if(msgBean != null){
            new SendMsgTask(MainActivity.this).execute(msgBean);
        }else {
            Toast.makeText(MainActivity.this,"准备发送的消息对象为空",Toast.LENGTH_SHORT).show();
        }
    }
}