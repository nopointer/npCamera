package basecamera.module.cfg;


public class BaseCameraCfg {


    public static String photoPath = "DCIM";

    /**
     * 拍照指令，相机界面接收该指令，进行拍照
     */
    public static final String takePhotoAction = "com.simpleCamera.takePhoto";

    /**
     * 设备退出相机界面 使app退出相机界面,界面关闭的时候不会发送关闭手环设备相机界面的广播
     */
    public static final String exitTakePhotoForApp = "com.simpleCamera.exitCamera_for_app";

    /**
     * 设备连接断开 app可以选择性是否退出界面（ble连接断开的时候），可以做断连提示
     */
    public static final String exitTakePhotoForAppWithDisconnected = "com.simpleCamera.exitCamera_for_app_with_disconnect";

    /**
     * app端退出相机界面 使设备退出界面界面(通过广播通知需要关闭手环界面)
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


    /**
     * 手环退出拍照界面，是否也退出其他页面
     */
    public static boolean autoExitOtherPageByDeviceExit =false;

    /**
     * 手环断开连接，是否也退出其他页面
     */
    public static boolean autoExitOtherPageByDeviceDisconnect =false;

}
