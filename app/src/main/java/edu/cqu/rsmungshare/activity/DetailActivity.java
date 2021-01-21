package edu.cqu.rsmungshare.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import edu.cqu.rsmungshare.R;
import edu.cqu.rsmungshare.model.ActivityCollector;
import edu.cqu.rsmungshare.model.BaseActivity;
import edu.cqu.rsmungshare.myview.LoadingAnimation;

public class DetailActivity extends BaseActivity {
    private final String TAG = "DetailActivity";
    private String local_ip = null;
    private LoadingAnimation loadingAnimation;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        checkSw_enterMain();
        super.onCreate(savedInstanceState);
        loadingAnimation = findViewById(R.id.loading_animation);
        displayLocalIP();
        dealToolbarBtn();
        //加载以前存储的目标IP地址
        SharedPreferences sp_target_ip = getSharedPreferences("target_ip",MODE_PRIVATE);
        String target_ip = sp_target_ip.getString("target_ip","NULL");
        EditText et_target_ip = findViewById(R.id.target_ip);
        et_target_ip.setText(target_ip);
        //设置监听事件
        et_target_ip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                SharedPreferences.Editor editor = getSharedPreferences("target_ip",MODE_PRIVATE).edit();
                editor.putString("target_ip",s.toString());
                editor.apply();
            }
        });
    }

    /*检查是否直接进入传输界面*/
    private void checkSw_enterMain(){
        //加载可能已经存在的选项
        SharedPreferences sp = getSharedPreferences("isEnterMainDirect",MODE_PRIVATE);
        boolean isEnterMainDirect = sp.getBoolean("isEnterMainDirect",false);
        Intent intent = getIntent();
        String FROM_MAIN = intent.getStringExtra("FROM_MAIN");
        if(FROM_MAIN == null || !FROM_MAIN.equals("TRUE")){//是TRUE就不直接跳转
            if(isEnterMainDirect){
                startActivity(new Intent(DetailActivity.this,MainActivity.class));//直接跳转
                finish();//当前界面结束
            }
        }

        //正常加载布局
        setContentView(R.layout.activity_detail);

        Switch sw_enterMain = findViewById(R.id.sw_enterMain);
        sw_enterMain.setChecked(isEnterMainDirect);
        //监听事件
        sw_enterMain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor sp_editor = getSharedPreferences("isEnterMainDirect",MODE_PRIVATE).edit();
                if(isChecked){
                    buttonView.setBackground(getDrawable(R.drawable.round_corner_blue_shallow));//更换背景
                    sp_editor.putBoolean("isEnterMainDirect",isChecked);//存储
                }else{
                    buttonView.setBackground(getDrawable(R.drawable.round_corner));
                    sp_editor.clear();//清除
                }
                sp_editor.apply();//生效
            }
        });
    }

    /*显示本机IP地址*/
    private void displayLocalIP(){
        loadingAnimation.start();
        loadingAnimation.setVisibility(View.VISIBLE);
        TextView tv_local_ip = findViewById(R.id.local_ip);
        if(isWifiEnabled()){
            if(local_ip != null && !local_ip.equals("NULL")){
                tv_local_ip.setText(local_ip);
                SharedPreferences.Editor editor = getSharedPreferences("local_ip",MODE_PRIVATE).edit();
                editor.putString("local_ip",local_ip);
                editor.apply();
            }
        }else {
            tv_local_ip.setText("WIFI未连接");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("WIFI未连接");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                    ActivityCollector.finishAll();
                }
            });
            builder.create().show();
        }
        CountDownTimer countDownTimer = new CountDownTimer(1000,500) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                loadingAnimation.setVisibility(View.GONE);
            }
        };
        countDownTimer.start();
    }

    /*判断wifi是否连接,如果已连接还顺便获取IP地址*/
    private boolean isWifiEnabled() {
        Context myContext =getApplicationContext();
        WifiManager wifiManager = (WifiManager) myContext.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            ConnectivityManager connManager =
                    (ConnectivityManager) myContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo =
                    connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            local_ip = intIP_StringIP(wifiInfo.getIpAddress());//得到IPV4地址
            return networkInfo.isConnected();
        } else {
            local_ip = "NULL";
            return false;
        }
    }

    /*将得到的int类型的IP转换为String类型*/
    private String intIP_StringIP(int ip) {
        return (ip & 0xFF) + "." +//低八位0-7    1111   1111
                ((ip >> 8) & 0xFF) + "." +//8-15
                ((ip >> 16) & 0xFF) + "." +//16-23
                (ip >> 24 & 0xFF);//24-31
    }

    /*处理toolbar上的按钮back和refresh*/
    private void dealToolbarBtn(){
        ImageView btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DetailActivity.this,MainActivity.class));
            }
        });
        ImageView btn_refresh = findViewById(R.id.refresh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayLocalIP();
            }
        });
    }
}
