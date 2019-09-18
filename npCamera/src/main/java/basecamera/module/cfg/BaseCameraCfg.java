package basecamera.module.cfg;


public class BaseCameraCfg {


    public static final String photoPath ="DCIM";

    /**
     * 拍照指令，相机界面接收该指令，进行拍照
     */
    public static final String takePhotoAction = "com.simpleCamera.takePhoto";

    /**
     * 退出相机界面 app退出界面
     */
    public static final String exitTakePhotoForApp = "com.simpleCamera.exitCamera_for_app";

    /**
     * 退出相机界面 app退出界面
     */
    public static final String exitTakePhotoForAppWithDisconnected = "com.simpleCamera.exitCamera_for_app_with_disconnect";

    /**
     * 退出相机界面 设备退出界面
     */
    public static final String exitTakePhotoForDev = "com.simpleCamera.exitCamera_for_dev";




    /**
     * 在拍照界面，意外断开 退出拍照节目前的提示语
     */
    public static String withTakeUIDisconnetedMessage ="";



    public static String galleryTitle ="Gallery";


}
