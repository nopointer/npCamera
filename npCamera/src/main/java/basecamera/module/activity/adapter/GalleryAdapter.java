package basecamera.module.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import java.util.List;

import basecamera.module.activity.model.PhotoItem;
import basecamera.module.lib.R;
import basecamera.module.utils.DistanceUtil;
import me.panpf.sketch.SketchImageView;

/**
 * @author tongqian.ni
 */
public class GalleryAdapter extends BaseAdapter {

    private Context mContext;
    private List<PhotoItem> values;
    public static GalleryHolder holder;


    /**
     * @param values
     */
    public GalleryAdapter(Context context, List<PhotoItem> values) {
        this.mContext = context;
        this.values = values;
    }



    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final GalleryHolder holder;
        int width = DistanceUtil.getCameraAlbumWidth(mContext);
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.basecamera_item_gallery, null);
            holder = new GalleryHolder();
            holder.sample = convertView.findViewById(R.id.gallery_sample_image);
            holder.sample.setLayoutParams(new AbsListView.LayoutParams(width, width));
            convertView.setTag(holder);
        } else {
            holder = (GalleryHolder) convertView.getTag();
        }
        final PhotoItem gallery = (PhotoItem) getItem(position);
        holder.sample.displayContentImage(gallery.getImageUri());
        return convertView;
    }

    class GalleryHolder {
        SketchImageView sample;
    }

}
