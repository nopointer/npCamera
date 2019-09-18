package basecamera.module.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import basecamera.module.cfg.BaseCameraCfg;
import basecamera.module.cfg.CameraSateHelper;
import basecamera.module.lib.JCameraView;
import basecamera.module.lib.R;
import basecamera.module.lib.listener.ClickListener;
import basecamera.module.lib.listener.ErrorListener;
import basecamera.module.lib.listener.JCameraListener;
import basecamera.module.lib.util.FileUtil;
import basecamera.module.lib.util.LogUtil;

public class BaseCameraTakePhotoActivity extends Activity {
    private JCameraView jCameraView;

    private boolean isTakePhotoIng = false;


    //进度框
    private QMUITipDialog.Builder builder;
    private QMUITipDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.basecamera_activity_camera);
        jCameraView = findViewById(R.id.jcameraview);
        //设置视频保存路径
        jCameraView.setSaveVideoPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "JCamera");
        jCameraView.setFeatures(JCameraView.BUTTON_STATE_ONLY_CAPTURE);
        jCameraView.setTip("");
        jCameraView.setErrorLisenter(new ErrorListener() {
            @Override
            public void onError() {
                //错误监听
                Log.i("CJT", "camera error");
                Intent intent = new Intent();
                setResult(103, intent);
                finish();
            }

            @Override
            public void AudioPermissionError() {
                Toast.makeText(BaseCameraTakePhotoActivity.this, "给点录音权限可以?", Toast.LENGTH_SHORT).show();
            }
        });
        //JCameraView监听
        jCameraView.setJCameraLisenter(new JCameraListener() {
            @Override
            public void captureSuccess(Bitmap bitmap) {
                String jpegName = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".jpg";
                String path = FileUtil.saveBitmap(BaseCameraCfg.photoPath, jpegName, bitmap);
                //获取图片bitmap
                isTakePhotoIng = false;
                LogUtil.e("path===>" + path);
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(path))));
                if (!TextUtils.isEmpty(path)) {
                    CameraSateHelper.getInstance().notifySuccess(path);
                } else {
                    CameraSateHelper.getInstance().notifyFailure(1);
                }
                if (loadingDialog != null) {
                    if (loadingDialog.isShowing()) {
                        loadingDialog.cancel();
                    }
                }
            }

            @Override
            public void recordSuccess(String url, Bitmap firstFrame) {
                //图片名称
                String jpegName = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".jpg";

                //获取视频路径
                String path = FileUtil.saveBitmap("JCamera/videoScreen", jpegName, firstFrame);
                Log.i("CJT", "url = " + url + ", Bitmap = " + path);
                Intent intent = new Intent();
                intent.putExtra("path", path);
                setResult(101, intent);
                sendExitCamera();
                finish();
            }
        });

        jCameraView.setLeftClickListener(new ClickListener() {
            @Override
            public void onClick() {
                BaseCameraTakePhotoActivity.this.finish();
            }
        });
        jCameraView.setRightClickListener(new ClickListener() {
            @Override
            public void onClick() {
                startActivity(new Intent(BaseCameraTakePhotoActivity.this, BaseCameraGalleryActivity.class));
            }
        });

        initReceiver(true);

        //切换到前置摄像头
//        jCameraView.switchCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        jCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        jCameraView.onPause();
    }


    private void initReceiver(boolean b) {
        try {
            if (b) {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(BaseCameraCfg.takePhotoAction);
                intentFilter.addAction(BaseCameraCfg.exitTakePhotoForApp);
                intentFilter.addAction(BaseCameraCfg.exitTakePhotoForAppWithDisconnected);
                registerReceiver(receiver, intentFilter);
            } else {
                unregisterReceiver(receiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                //拍照
                case BaseCameraCfg.takePhotoAction:
                    if (!isTakePhotoIng) {
                        isTakePhotoIng = true;
                        showLoadingDialog("");
                        jCameraView.takePhoto();
                    }
                    break;
                //退出
                case BaseCameraCfg.exitTakePhotoForApp:
                    finish();
                    break;
                //拍照界面 异常断开 退出拍照界面的提示语
                case BaseCameraCfg.exitTakePhotoForAppWithDisconnected:
                    Toast.makeText(BaseCameraTakePhotoActivity.this, BaseCameraCfg.withTakeUIDisconnetedMessage, Toast.LENGTH_SHORT).show();
                    finish();
                    break;
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        initReceiver(false);
        sendExitCamera();
        isTakePhotoIng = false;
    }

    /**
     * 显示加载框
     */
    public void showLoadingDialog(final String argMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (loadingDialog == null || !loadingDialog.isShowing()) {
                        builder = new QMUITipDialog.Builder(BaseCameraTakePhotoActivity.this).setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING);
                        loadingDialog = builder.setTipWord(argMessage).create();
                        loadingDialog.show();
                        loadingDialog.setCanceledOnTouchOutside(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    //退出相机模式
    public void sendExitCamera() {
        sendBroadcast(new Intent(BaseCameraCfg.exitTakePhotoForDev));
    }


    /**
     * 双击返回键退出
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            sendExitCamera();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
