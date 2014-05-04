package jay.droid.googleimagesearch;

import android.graphics.Bitmap;
import android.util.LruCache;
import android.widget.ImageView;

/**
 * Created by jay on 2/9/14.
 */
public class ImageViewEncapsulator {
    private ImageView imageView = null;
    private int position = 0;
    private String newUrlString = null;
    private boolean bigImage = false;

    private static LruCache<Integer, Bitmap> lruCache = null;

    public ImageViewEncapsulator(ImageView iv, int pos, String url) {
        init(iv, pos, url);
    }

    public ImageViewEncapsulator(ImageView iv, int pos, String url, boolean big) {
        init(iv, pos, url);
        bigImage = true;
    }

    public void init(ImageView _imageView, int _position, String _newUrlString) {
        imageView = _imageView;
        position = _position;
        newUrlString = _newUrlString;
    }

    public String getNewUrlString() {
        return newUrlString;
    }

    public static LruCache<Integer, Bitmap> getLruCache() {
        return lruCache;
    }

    public static void setLruCache(LruCache<Integer, Bitmap> lruCache) {
        ImageViewEncapsulator.lruCache = lruCache;
    }

    public void setImage(Bitmap bitmap) {
        if (bigImage == true)
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setImageBitmap(bitmap);
        lruCache.put(position, bitmap);
    }


}
