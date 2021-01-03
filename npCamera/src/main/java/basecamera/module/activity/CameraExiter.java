package basecamera.module.activity;

import java.util.HashSet;

public class CameraExiter {
    private static final CameraExiter ourInstance = new CameraExiter();

    public static CameraExiter getInstance() {
        return ourInstance;
    }

    private CameraExiter() {
    }


    private HashSet<Callback> cameraCallbackHashSet = new HashSet<>();


    public void registerCallback(Callback cameraCallback) {
        if (!cameraCallbackHashSet.contains(cameraCallback)) {
            cameraCallbackHashSet.add(cameraCallback);
        }
    }

    public void unRegisterCallback(Callback cameraCallback) {
        if (cameraCallbackHashSet.contains(cameraCallback)) {
            cameraCallbackHashSet.remove(cameraCallback);
        }
    }

    public void notifyFinish() {
        for (Callback cameraCallback : cameraCallbackHashSet) {
            cameraCallback.onFinish();
        }
    }

    public interface Callback {
        void onFinish();
    }
}

