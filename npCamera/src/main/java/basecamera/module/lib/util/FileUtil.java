package basecamera.module.lib.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import basecamera.module.log.NpCameraLog;

/**
 * =====================================
 * 作    者: 陈嘉桐
 * 版    本：1.1.4
 * 创建日期：2017/4/25
 * 描    述：
 * =====================================
 */
public class FileUtil {

    private static Context mContext;
    private static final String TAG = "CJT";
    private static File parentPath = Environment.getExternalStorageDirectory();

    public static void init(Context context) {
        mContext = context;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            parentPath = Environment.getExternalStorageDirectory();
        } else {
            parentPath = Environment.getExternalStorageDirectory();
        }
    }


    public static String saveBitmap(String dir, String jpegName, Bitmap b) {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            saveToMedia(dir + File.separator + jpegName);
        }

        File fileDir = new File(parentPath, dir);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        //图片路径
        File file = new File(fileDir, jpegName);

        NpCameraLog.logE("保存的本地路径:" + file.getPath());
        try {
            FileOutputStream fout = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            return file.getPath();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static void saveToMedia(String fileName) {
        ContentValues values = new ContentValues();
        NpCameraLog.logE("29后的路径:" + fileName);
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/*");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);
        ContentResolver contentResolver = mContext.getContentResolver();
        Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }


    public static boolean deleteFile(String url) {
        boolean result = false;
        File file = new File(url);
        if (file.exists()) {
            result = file.delete();
        }
        return result;
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
