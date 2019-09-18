package basecamera.module.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import basecamera.module.activity.adapter.GalleryAdapter;
import basecamera.module.activity.model.PhotoItem;
import basecamera.module.functionModule.imagePreview.style.index.NumberIndexIndicator;
import basecamera.module.functionModule.imagePreview.style.progress.ProgressBarIndicator;
import basecamera.module.functionModule.imagePreview.transfer.TransferConfig;
import basecamera.module.functionModule.imagePreview.transfer.Transferee;
import basecamera.module.lib.R;

/**
 * @author tongqian.ni
 */
public class BaseCameraAlbumFragment extends Fragment {
    private ArrayList<PhotoItem> photos = new ArrayList<PhotoItem>();

    public BaseCameraAlbumFragment() {
        super();
    }

    private GridView albums;

    //预览工具
    protected Transferee transferee;
    protected TransferConfig config;
    private List<String> pathList = new ArrayList<>();


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public static Fragment newInstance(ArrayList<PhotoItem> photos) {
        Fragment fragment = new BaseCameraAlbumFragment();
        Bundle args = new Bundle();
        args.putSerializable("photos", photos);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.basecamera_fragment_album, null);
        photos = (ArrayList<PhotoItem>) getArguments().getSerializable("photos");
        albums = (GridView) root.findViewById(R.id.albums);
        albums.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                config.setNowThumbnailIndex(position);
                transferee.apply(config).show();
            }
        });
        transferee = Transferee.getDefault(getContext());
        for (PhotoItem photoItem : photos) {
            pathList.add("file:/" + photoItem.getImageUri());
        }
        initTransfereeConfig();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        albums.setAdapter(new GalleryAdapter(getActivity(), photos));
    }


    private void initTransfereeConfig() {
        config = TransferConfig.build()
                .setSourceImageList(pathList)
                .setThumbnailImageList(pathList)
                .setMissPlaceHolder(R.drawable.ic_empty_photo)
                .setErrorPlaceHolder(R.drawable.ic_empty_photo)
                .setProgressIndicator(new ProgressBarIndicator())
                .setIndexIndicator(new NumberIndexIndicator())
                .setJustLoadHitImage(true)
                .bindListView(albums, R.id.gallery_sample_image);
    }
}
