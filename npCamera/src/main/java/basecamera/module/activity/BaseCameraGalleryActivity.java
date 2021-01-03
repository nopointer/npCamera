package basecamera.module.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import basecamera.module.activity.model.Album;
import basecamera.module.cfg.BaseCameraCfg;
import basecamera.module.cfg.BaseCameraResHelper;
import basecamera.module.lib.R;
import basecamera.module.utils.FileUtils;
import basecamera.module.utils.ImageUtils;
import basecamera.module.utils.StringUtils;
import basecamera.module.views.CameraTitleBar;
import basecamera.module.views.PagerSlidingTabStrip;


/**
 * 相册界面
 */
public class BaseCameraGalleryActivity extends FragmentActivity {

    private Map<String, Album> albums;
    private List<String> paths = new ArrayList<String>();

    PagerSlidingTabStrip tab;
    ViewPager pager;

    private CameraTitleBar cameraTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //沉浸式
        StatusBarHelperUtils.translucent(this);
        StatusBarHelperUtils.setStatusBarLightMode(this);
        setContentView(R.layout.basecamera_activity_album);
        tab = findViewById(R.id.indicator);
        pager = findViewById(R.id.pager);
        albums = ImageUtils.findGalleries(this, paths, 0);
        //ViewPager的adapter
        FragmentPagerAdapter adapter = new TabPageIndicatorAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        tab.setViewPager(pager);

        cameraTitleBar = findViewById(R.id.titleBar);
        cameraTitleBar.setTitle(BaseCameraCfg.galleryTitle);
        cameraTitleBar.setLeftImage(R.mipmap.basecamera_ico_title_back);
        cameraTitleBar.setLeftViewOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initReceiver(true);
    }

    class TabPageIndicatorAdapter extends FragmentPagerAdapter {
        public TabPageIndicatorAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            //新建一个Fragment来展示ViewPager item的内容，并传递参数
            return BaseCameraAlbumFragment.newInstance(albums.get(paths.get(position)).getPhotos());
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Album album = albums.get(paths.get(position));
            if (StringUtils.equalsIgnoreCase(FileUtils.getInst(BaseCameraGalleryActivity.this).getSystemPhotoPath(),
                    album.getAlbumUri())) {
                return BaseCameraResHelper.galleryName;
            } else if (album.getTitle().length() > 13) {
                return album.getTitle().substring(0, 11) + "...";
            }
            return album.getTitle();
        }

        @Override
        public int getCount() {
            return paths.size();
        }
    }


    private void initReceiver(boolean b) {
        try {
            if (b) {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(BaseCameraCfg.exitTakePhotoForApp);
                intentFilter.addAction(BaseCameraCfg.exitTakePhotoForAppWithDisconnected);
                registerReceiver(receiver, intentFilter);
            } else {
                unregisterReceiver(receiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        initReceiver(false);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BaseCameraCfg.exitTakePhotoForApp:
                    if (BaseCameraCfg.autoExitOtherPageByDeviceExit) {
                        setResultCode(RESULT_OK);
                        finish();
                    }
                    break;
                //拍照界面 异常断开 退出拍照界面的提示语
                case BaseCameraCfg.exitTakePhotoForAppWithDisconnected:
                    if (BaseCameraCfg.autoExitOtherPageByDeviceDisconnect) {
                        setResultCode(RESULT_OK);
                        finish();
                    }
                    break;
            }
        }
    };

}
