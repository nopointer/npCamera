package basecamera.module.utils;


import android.content.Context;


public class DistanceUtil {

    public static int getCameraAlbumWidth(Context context) {
        return (DisplayHelperUtils.getScreenWidth(context) - dp2px(context, 10)) / 4 - dp2px(context, 4);
    }

    // 相机照片列表高度计算 
    public static int getCameraPhotoAreaHeight(Context context) {
        return getCameraPhotoWidth(context) + dp2px(context, 4);
    }

    public static int getCameraPhotoWidth(Context context) {
        return DisplayHelperUtils.getScreenWidth(context) / 4 - dp2px(context, 2);
    }

    //活动标签页grid图片高度
    public static int getActivityHeight(Context context) {
        return (DisplayHelperUtils.getScreenWidth(context) - dp2px(context, 24)) / 3;
    }


    public static int dp2px(Context context, int dp) {
        return DisplayHelperUtils.dp2px(context, dp);
    }
}
