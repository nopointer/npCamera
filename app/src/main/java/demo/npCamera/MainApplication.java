package demo.npCamera;

import android.app.Application;

import basecamera.module.cfg.BaseCameraCfg;


public class MainApplication extends Application {


    public static MainApplication mainApplication = null;


    @Override
    public void onCreate() {
        super.onCreate();
        mainApplication = this;

        BaseCameraCfg.photoPath="DCIM/DiriFit";


    }

    public static MainApplication getMainApplication() {
        return mainApplication;
    }



}
