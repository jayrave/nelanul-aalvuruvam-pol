package jay.droid.googleimagesearch;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by jay on 2/9/14.
 */
public class BingImageAdapter extends ArrayAdapter {

    private int resource = 0;
    private Context context = null;
    private LayoutInflater inflater = null;
    private ArrayList<BingImageEntry> objects = null;
    private LruCache<Integer, Bitmap> lruCache = null;

    public BingImageAdapter(Context _context, int _resource, List _objects) {
        super(_context, _resource, _objects);

        context = _context;
        resource = _resource;
        objects = (ArrayList<BingImageEntry>) _objects;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setUpCache();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public void addAll(Collection collection) {
        objects.addAll((ArrayList<BingImageEntry>) collection);
    }

    @Override
    public void clear() {
        objects.clear();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null)
            view = initiateView(parent);
        setImage(position, view);
        return view;
    }

    private void setUpCache() {
        int maxMemory = (int)(Runtime.getRuntime().maxMemory()/1024);
        lruCache = new LruCache<Integer, Bitmap>(maxMemory / 2);
        ImageViewEncapsulator.setLruCache(lruCache);
    }

    private View initiateView(ViewGroup parent) {
        View view = inflater.inflate(resource, parent, false);
        Holder holder = new Holder((ImageView) view.findViewById(R.id.image));
        holder.image.setLayoutParams(new GridView.LayoutParams((int) (parent.getWidth()/2.25),
                (int) (parent.getWidth()/2.25)));
        view.setTag(holder);
        return view;
    }

    private void setImage(int position, View view) {
        Holder holder = (Holder) view.getTag();
        Bitmap bitmap = lruCache.get(position);

        if (bitmap != null)
            holder.image.setImageBitmap(bitmap);
        else {
            holder.image.setImageResource(R.drawable.placeholder);
            ImageViewEncapsulator imageInfo = new ImageViewEncapsulator(holder.image,
                    position, objects.get(position).thumbLink);
            new DownloadImage().execute(imageInfo);
        }
    }

    private class Holder {
        public ImageView image = null;

        public Holder(ImageView i) {
            image = i;
        }
    }
}
