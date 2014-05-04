package jay.droid.googleimagesearch;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.LruCache;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


public class ImageSearchActivity extends ActionBarActivity {

    public static final String HISTORY_FILENAME = "history";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_search);

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

        AutoCompleteTextView search_box = null;
        GridView search_result = null;
        BingImageLinkRetriever imageRetriever = null;
        ArrayList<String> search_history = null;
        BingImageAdapter bingImageAdapter = null;
        ArrayAdapter<String> search_history_adapter = null;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_image_search, container, false);

            search_box = (AutoCompleteTextView) rootView.findViewById(R.id.search_box);
            search_result = (GridView) rootView.findViewById(R.id.search_result);
            imageRetriever = new BingImageLinkRetriever();
            search_history = new ArrayList<String>();

            setUpSearchBox();
            setUpSearchResult();
            setEndlessScrolling();

            if (getIntent() != null)
                start_from_intent();

            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            setUpSearchHistory();
        }

        @Override
        public void onPause() {
            saveSearchHistory();
            super.onPause();
        }

        private void start_from_intent() {
            Intent intent = getIntent();
            search_box.setText(intent.getStringExtra("query"));
            search_box.setSelection(search_box.getText().length());
        }

        private void setUpSearchBox() {
            bingImageAdapter = (BingImageAdapter) initiateBingImageAdapter();
            search_result.setAdapter(bingImageAdapter);

            search_box.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        clearLruCache();
                        bingImageAdapter.clear();
                        String query = search_box.getText().toString();
                        imageRetriever.updateAdapter(query, bingImageAdapter);
                        add_to_history(query);
                        return true;
                    }
                    return false;
                }
            });

            search_history_adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_dropdown_item_1line, search_history);
            search_box.setAdapter(search_history_adapter);
        }

        private void setUpSearchResult() {
            search_result.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Intent intent = new Intent(getActivity(), SingleBigImage.class);
                    BingImageEntry entry = (BingImageEntry) bingImageAdapter.getItem(position);
                    intent.putExtra("urlStringBig", entry.link);
                    intent.putExtra("position", position);
                    startActivity(intent);
                }
            });
        }

        private void add_to_history(String query) {
            search_history.remove(query);
            search_history.add(0, query);
            search_history_adapter.remove(query);
            search_history_adapter.insert(query, 0);
        }

        private void setUpSearchHistory() {
            BufferedReader bufferedReader = null;
            String line = null;
            try {
                bufferedReader = new BufferedReader(new
                        InputStreamReader(openFileInput(HISTORY_FILENAME)));
                while ((line = bufferedReader.readLine()) != null)
                    add_to_history(line);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void saveSearchHistory() {
            if (search_history.size() > 0) {
                BufferedWriter bufferedWriter = null;
                try {
                    bufferedWriter = new BufferedWriter(new
                            OutputStreamWriter(openFileOutput(HISTORY_FILENAME, Context.MODE_PRIVATE)));
                    for (int i = 0; i < search_history.size(); i++) {
                        bufferedWriter.write(search_history.get(i));
                        bufferedWriter.newLine();
                    }
                    bufferedWriter.flush();
                    bufferedWriter.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void clearLruCache() {
            LruCache lruCache = ImageViewEncapsulator.getLruCache();
            if (lruCache != null)
                lruCache.evictAll();
        }

        private void setEndlessScrolling() {
            search_result.setOnScrollListener(new EndlessScrollListener() {
                @Override
                public void onScrollEnd(AbsListView view, int firstVisibleItem,
                                        int visibleItemCount, int totalItemCount) {
                    if (totalItemCount >= 10)
                        imageRetriever.updateAdapter();
                }
            });
        }

        private ArrayAdapter initiateBingImageAdapter() {
            ArrayList<BingImageEntry> sourceList = new ArrayList<BingImageEntry>();
            BingImageAdapter adapter = new BingImageAdapter(getApplicationContext(),
                    R.layout.single_image, sourceList);
            return adapter;
        }
    }
}
