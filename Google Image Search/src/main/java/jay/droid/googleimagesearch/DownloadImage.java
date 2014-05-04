package jay.droid.googleimagesearch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by jay on 2/9/14.
 */
public class DownloadImage extends AsyncTask<ImageViewEncapsulator, Void, Bitmap> {

    private ImageViewEncapsulator imageViewEncapsulator = null;

    @Override
    protected Bitmap doInBackground(ImageViewEncapsulator... imageViewEncapsulators) {
        imageViewEncapsulator = imageViewEncapsulators[0];
        return getImage(imageViewEncapsulator.getNewUrlString());
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        imageViewEncapsulator.setImage(bitmap);
    }

    private InputStream getInputStream(String urlString) {
        InputStream inputStream = null;

        try {
            URL url = new URL(urlString);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.connect();
            inputStream = httpConnection.getInputStream();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    private Bitmap getImage(String urlString) {
        InputStream inputStream = getInputStream(urlString);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        return bitmap;
    }
}

