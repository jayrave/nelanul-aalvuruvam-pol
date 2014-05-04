package jay.droid.googleimagesearch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class SingleBigImage extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_big_image);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.history) {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public class PlaceholderFragment extends Fragment {

        ImageView imageView = null;
        boolean action_bar_hide = true;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_single_big_image, container, false);
            imageView = (ImageView) rootView.findViewById(R.id.image);
            getActionBar().hide();
            toggleActionBar(rootView);
            downloadImage();
            return rootView;
        }

        private void toggleActionBar(View rootView) {
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (action_bar_hide) {
                        getActionBar().hide();
                        action_bar_hide = false;
                    }
                    else {
                        getActionBar().show();
                        action_bar_hide = true;
                    }
                }
            });
        }

        private void downloadImage() {
            Intent intent = getActivity().getIntent();
            String urlStringBig = intent.getStringExtra("urlStringBig");
            int position = intent.getIntExtra("position", 1);

            Bitmap bitmap = ImageViewEncapsulator.getLruCache().get(position);
            if (bitmap != null)
                imageView.setImageBitmap(bitmap);
            else
                imageView.setImageResource(R.drawable.placeholder);

            ImageViewEncapsulator imageInfoBig = new ImageViewEncapsulator(imageView, position, urlStringBig, true);
            new DownloadImage().execute(imageInfoBig);
        }
    }

}
