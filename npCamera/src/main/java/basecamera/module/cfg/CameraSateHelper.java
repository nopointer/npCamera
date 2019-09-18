package basecamera.module.cfg;

import java.util.HashSet;

/**
 * 相机助手 之所以用观察者模式 是因为不同的项目有国际化 不想动项目模块里面的任何东西
 */
public class CameraSateHelper {
    private static final CameraSateHelper ourInstance = new CameraSateHelper();

    public static CameraSateHelper getInstance() {
        return ourInstance;
    }

    private CameraSateHelper() {
    }


    private HashSet<CameraCallback> cameraCallbackHashSet = new HashSet<>();


    public void registerCallback(CameraCallback cameraCallback) {
        if (!cameraCallbackHashSet.contains(cameraCallback)) {
            cameraCallbackHashSet.add(cameraCallback);
        }
    }

    public void unRegisterCallback(CameraCallback cameraCallback) {
        if (cameraCallbackHashSet.contains(cameraCallback)) {
            cameraCallbackHashSet.remove(cameraCallback);
        }
    }

    public void notifySuccess(String path) {
        for (CameraCallback cameraCallback : cameraCallbackHashSet) {
            cameraCallback.onTakePhotoSuccess(path);
        }
    }

    /**
     * @param code 1 拍照失败，请稍后重试！2切换摄像头失败
     */
    public void notifyFailure(int code) {
        for (CameraCallback cameraCallback : cameraCallbackHashSet) {
            cameraCallback.onTakePhotoFailure(code);
        }
    }


    public interface CameraCallback {
        void onTakePhotoSuccess(String path);

        void onTakePhotoFailure(int code);
    }


}
