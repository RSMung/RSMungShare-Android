package edu.cqu.rsmungshare.mythread;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.ServerSocket;
import java.net.Socket;

import edu.cqu.rsmungshare.MyObjectInputStream;
import edu.cqu.rsmungshare.activity.MainActivity;
import edu.cqu.rsmungshare.bean.MsgBean;
import edu.cqu.rsmungshare.bean.MsgCollector;

public class ReceiveThread extends Thread {
    private int port = 1999;//接收信息端口号
    private WeakReference<MainActivity> weakReference;
    public ReceiveThread(MainActivity activity){
        this.weakReference = new WeakReference<>(activity);
    }

    @Override
    public void run() {
        super.run();
        while(true){
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                Socket socket = serverSocket.accept();//阻塞等待
                MyObjectInputStream objectInputStream = new MyObjectInputStream(socket.getInputStream());
                MsgBean msgBean = (MsgBean) objectInputStream.readObject();
                MsgCollector.addMsg(msgBean);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
