package demo.npCamera;

import android.app.Application;

import basecamera.module.cfg.BaseCameraCfg;
import basecamera.module.lib.util.FileUtil;


public class MainApplication extends Application {


    public static MainApplication mainApplication = null;


    @Override
    public void onCreate() {
        super.onCreate();
        mainApplication = this;

        BaseCameraCfg.photoDir="DCIM/DiriFit";

        FileUtil.init(this);


    }

    public static MainApplication getMainApplication() {
        return mainApplication;
    }



}
