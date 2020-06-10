package basecamera.module.cfg;


public class BaseCameraCfg {


    public static String photoPath = "DCIM";

    /**
     * 拍照指令，相机界面接收该指令，进行拍照
     */
    public static final String takePhotoAction = "com.simpleCamera.takePhoto";

    /**
     * 退出相机界面 app退出界面
     */
    public static final String exitTakePhotoForApp = "com.simpleCamera.exitCamera_for_app";

    /**
     * 退出相机界面 app退出界面（ble连接断开的时候）
     */
    public static final String exitTakePhotoForAppWithDisconnected = "com.simpleCamera.exitCamera_for_app_with_disconnect";

    /**
     * 退出相机界面 设备退出界面
     */
    public static final String exitTakePhotoForDev = "com.simpleCamera.exitCamera_for_dev";


    /**
     * 在拍照界面，意外断开 退出拍照界面前的提示语
     */
    public static String withTakeUIDisconnetedMessage = "";


    public static String galleryTitle = "Gallery";

    /**
     * 退出相机模式的延时
     */
    public static int delayExitTime = 500;

}
