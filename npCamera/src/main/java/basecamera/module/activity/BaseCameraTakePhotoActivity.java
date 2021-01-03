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
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

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
import basecamera.module.log.NpCameraLog;
import basecamera.module.views.LoadingView;

public class BaseCameraTakePhotoActivity extends Activity {
    private JCameraView jCameraView;

    private boolean isTakePhotoIng = false;

    public static boolean isStartUI = false;

    private LoadingView loadView;


    /**
     * 拍照界面是否点击了退出
     */
    private boolean isClickExit = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if (loadView != null) {
                    loadView.setVisibility(View.GONE);
                }
            }
        }
    };

    /**
     * 是否需要发送广播
     */
    private boolean isNeedSendExitBroadCast = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isStartUI = true;
        isNeedSendExitBroadCast = true;
        //去掉标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.basecamera_activity_camera);
        jCameraView = findViewById(R.id.jcameraview);

        loadView = findViewById(R.id.loadView);

        //设置视频保存路径
        jCameraView.setSaveVideoPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "JCamera");
        jCameraView.setFeatures(JCameraView.BUTTON_STATE_ONLY_CAPTURE);
        jCameraView.setTip("");
        jCameraView.setErrorLisenter(new ErrorListener() {
            @Override
            public void onError() {
                //错误监听
                NpCameraLog.logI("camera error");
                Intent intent = new Intent();
                setResult(103, intent);
                isNeedSendExitBroadCast = true;
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
            public void captureSuccess(final Bitmap bitmap) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String jpegName = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".jpg";
                        String path = FileUtil.saveBitmap(BaseCameraCfg.photoPath, jpegName, bitmap);
                        //获取图片bitmap

                        NpCameraLog.logE("path===>" + path);
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(path))));
                        if (!TextUtils.isEmpty(path)) {
                            CameraSateHelper.getInstance().notifySuccess(path);
                        } else {
                            CameraSateHelper.getInstance().notifyFailure(1);
                        }
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        handler.sendEmptyMessage(0);
                        isTakePhotoIng = false;
                    }
                }).start();
            }

            @Override
            public void recordSuccess(String url, Bitmap firstFrame) {
                //图片名称
                String jpegName = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".jpg";

                //获取视频路径
                String path = FileUtil.saveBitmap("JCamera/videoScreen", jpegName, firstFrame);
                NpCameraLog.logI("url = " + url + ", Bitmap = " + path);
                Intent intent = new Intent();
                intent.putExtra("path", path);
                setResult(101, intent);
                finish();
                isNeedSendExitBroadCast = true;
            }
        });

        jCameraView.setOnCameraSomeStateListener(new JCameraView.OnCameraSomeStateListener() {
            @Override
            public void onBeforeTakePhoto() {
                NpCameraLog.logE("开始拍照");
//                sendBroadcast(new Intent(loadingAction));
                sendBroadcast(new Intent(BaseCameraCfg.takePhotoAction));
            }
        });
        jCameraView.setLeftClickListener(new ClickListener() {
            @Override
            public void onClick() {
                if (!isTakePhotoIng) {
                    isClickExit = true;
                    finish();
                    isNeedSendExitBroadCast = true;
                }
            }
        });
        jCameraView.setRightClickListener(new ClickListener() {
            @Override
            public void onClick() {
                startActivityForResult(new Intent(BaseCameraTakePhotoActivity.this, BaseCameraGalleryActivity.class), 2);
            }
        });
        NpCameraLog.logE("开始进入相机界面");

        initReceiver(true);

        //切换到前置摄像头
//        jCameraView.switchCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        NpCameraLog.logE("onResume");
        initReceiver(true);
        jCameraView.onResume();
        isStartUI = true;
        isTakePhotoIng = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        NpCameraLog.logE("onPause");
        initReceiver(false);
        jCameraView.onPause();
        isStartUI = false;
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
                    if (isClickExit) {
                        NpCameraLog.logE("已经点了退出app的界面了，不接收拍照指令");
                        return;
                    }
                    if (!isStartUI) {
                        NpCameraLog.logE("当前不在拍照界面，不处理指令");
                        return;
                    }
                    if (!isTakePhotoIng) {
                        isTakePhotoIng = true;
                        showLoading();
                        NpCameraLog.logE("开始拍照，显示loading");
                        jCameraView.takePhoto();
                    }
                    break;
                //退出
                case BaseCameraCfg.exitTakePhotoForApp:
                    isNeedSendExitBroadCast = false;
                    if (!isTakePhotoIng) {
                        finish();
                    } else {
                        if (handler == null) {
                            handler = new Handler();
                        }
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isTakePhotoIng = false;
                                finish();
                            }
                        }, BaseCameraCfg.delayExitTime);
                    }
                    break;
                //拍照界面 异常断开 退出拍照界面的提示语
                case BaseCameraCfg.exitTakePhotoForAppWithDisconnected:
                    if (!isTakePhotoIng) {
                        Toast.makeText(BaseCameraTakePhotoActivity.this, BaseCameraCfg.withTakeUIDisconnetedMessage, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        if (handler == null) {
                            handler = new Handler();
                        }
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, BaseCameraCfg.delayExitTime);
                    }
                    break;
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        NpCameraLog.logE("onDestroy");
        if (loadView != null) {
            loadView.setVisibility(View.GONE);
        }
        isStartUI = false;
        if (isNeedSendExitBroadCast) {
            sendExitCamera();
        }
        isTakePhotoIng = false;
    }

    /**
     * 显示加载框
     */
    public void showLoading() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (loadView != null) {
                        loadView.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    //退出相机模式
    public void sendExitCamera() {
        isStartUI = false;
        sendBroadcast(new Intent(BaseCameraCfg.exitTakePhotoForDev));
    }


    /**
     * 双击返回键退出
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isTakePhotoIng) {
                sendExitCamera();
            } else {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK) {
            isTakePhotoIng = false;
            finish();
        }
    }
}
