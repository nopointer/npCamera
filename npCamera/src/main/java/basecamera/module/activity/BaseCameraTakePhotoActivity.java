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
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

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
import me.panpf.sketch.SketchImageView;

public class BaseCameraTakePhotoActivity extends Activity implements CameraExiter.Callback {
    private JCameraView jCameraView;

    private boolean isTakePhotoIng = false;

    public static boolean isStartUI = false;

    private View maskView;

    private View rl_preview;
    private SketchImageView siv_preview;
    private ImageView iv_close;


    /**
     * 拍照界面是否点击了退出
     */
    private boolean isClickExit = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if (maskView != null) {
                    maskView.setVisibility(View.GONE);
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
        CameraExiter.getInstance().registerCallback(this);
        isStartUI = true;
        isNeedSendExitBroadCast = true;
        //去掉标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.basecamera_activity_camera);
        jCameraView = findViewById(R.id.jcameraview);

        maskView = findViewById(R.id.maskView);
        siv_preview = findViewById(R.id.siv_preview);
        iv_close = findViewById(R.id.iv_close);
        rl_preview = findViewById(R.id.rl_preview);


        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_preview.setVisibility(View.GONE);
            }
        });

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
                        final String path = FileUtil.saveBitmap(BaseCameraCfg.photoDir, jpegName, bitmap);
                        //获取图片bitmap

                        NpCameraLog.logE("path===>" + path);
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(path))));
                        if (!TextUtils.isEmpty(path)) {
                            CameraSateHelper.getInstance().notifySuccess(path);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    siv_preview.displayImage(path);
                                    rl_preview.setVisibility(View.VISIBLE);
                                }
                            });
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
                if (!isStartUI) {
                    NpCameraLog.logE("相机已经onPause，不处理");
                    return;
                }
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
                startActivity(new Intent(BaseCameraTakePhotoActivity.this, BaseCameraGalleryActivity.class));
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
        jCameraView.onResume();
        isStartUI = true;
        isTakePhotoIng = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        NpCameraLog.logE("onPause");
        isStartUI = false;
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
        if (maskView != null) {
            maskView.setVisibility(View.GONE);
        }
        if (isNeedSendExitBroadCast) {
            sendExitCamera();
        }
        isStartUI = false;
        isTakePhotoIng = false;
        CameraExiter.getInstance().unRegisterCallback(this);
        initReceiver(false);
    }

    /**
     * 显示加载框
     */
    public void showLoading() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (maskView != null) {
                        maskView.setVisibility(View.VISIBLE);
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
    public void onFinish() {
        isTakePhotoIng = false;
        finish();
    }
}
