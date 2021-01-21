package edu.cqu.rsmungshare.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import org.devio.takephoto.app.TakePhoto;
import org.devio.takephoto.app.TakePhotoImpl;
import org.devio.takephoto.model.InvokeParam;
import org.devio.takephoto.model.TContextWrap;
import org.devio.takephoto.model.TImage;
import org.devio.takephoto.model.TResult;
import org.devio.takephoto.permission.InvokeListener;
import org.devio.takephoto.permission.PermissionManager;
import org.devio.takephoto.permission.TakePhotoInvocationHandler;

import java.util.ArrayList;

import edu.cqu.rsmungshare.R;
import edu.cqu.rsmungshare.model.BaseActivity;

public class SelectImageActivity extends BaseActivity implements TakePhoto.TakeResultListener, InvokeListener {
    private static final String TAG = "SelectImageActivity";
    private TakePhoto takePhoto;
    private InvokeParam invokeParam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
//        takePhoto.onPickFromGallery();
        takePhoto.onPickMultiple(20);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this);
    }

    /**
     * 获取TakePhoto实例
     *
     * @return
     */
    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        return takePhoto;
    }

    @Override
    public void takeSuccess(TResult result) {
        ArrayList<TImage> images = result.getImages();
        ArrayList<String> images_paths = new ArrayList<>();
        for(int i=0; i < images.size(); i++){
            images_paths.add(images.get(i).getOriginalPath());
            Log.w(TAG, "takeSuccess：" + images_paths.get(i));
        }
        Intent result_intent = new Intent();
        result_intent.putStringArrayListExtra("images_paths",images_paths);
        setResult(RESULT_OK, result_intent);
        finish();
//        //单选图片时候的代码
//        String selected_image_path = result.getImage().getOriginalPath();
//        Log.w(TAG, "takeSuccess：" + selected_image_path);
//        Intent result_intent = new Intent();
//        result_intent.putExtra("selected_image_path", selected_image_path);
//        setResult(RESULT_OK, result_intent);
//        finish();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        Log.w(TAG, "takeFail:" + msg);
        Toast.makeText(SelectImageActivity.this,"选取照片失败",Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void takeCancel() {
        Log.w(TAG, getResources().getString(R.string.msg_operation_canceled));
        Toast.makeText(SelectImageActivity.this,getResources().getString(R.string.msg_operation_canceled),Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }
}
