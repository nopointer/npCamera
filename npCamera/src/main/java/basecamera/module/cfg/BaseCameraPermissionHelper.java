package basecamera.module.cfg;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

import java.util.HashSet;

public class BaseCameraPermissionHelper {
    private static final BaseCameraPermissionHelper ourInstance = new BaseCameraPermissionHelper();

    public static BaseCameraPermissionHelper getInstance() {
        return ourInstance;
    }

    private BaseCameraPermissionHelper() {
    }


    private HashSet<PermissionCallback> permissionCallbacks = new HashSet<>();


    public void registerCallback(PermissionCallback callback) {
        if (callback == null) return;
        if (permissionCallbacks == null) return;
        permissionCallbacks.add(callback);
    }

    public void unRegisterCallback(PermissionCallback callback) {
        if (callback == null) return;
        if (permissionCallbacks == null) return;
        permissionCallbacks.remove(callback);
    }

    public void onNotPermission(String... prems) {
        for (PermissionCallback callback : permissionCallbacks) {
            callback.notPermission(prems);
        }
    }

    public interface PermissionCallback {
        void notPermission(String... prems);
    }



    /**
     * 判断是否有该项或者多项权限
     *
     * @param context
     * @param perms
     * @return
     */
    public static boolean hasPermissions(Context context, String... perms) {
        // Always return true for SDK < M, let the system deal with the permissions
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for (String perm : perms) {
            boolean hasPerm = (ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED);
            if (!hasPerm) {
                return false;
            }
        }
        return true;
    }

}
