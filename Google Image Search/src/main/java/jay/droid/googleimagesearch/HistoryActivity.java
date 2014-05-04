package jay.droid.googleimagesearch;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class HistoryActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

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

        ArrayAdapter<String> history_list_adapter = null;
        ListView history_list = null;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_history, container, false);
            history_list_adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, new ArrayList<String>());
            history_list = (ListView) rootView.findViewById(R.id.history_list);
            setUpHistoryListView();

            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            setUpSearchHistory();
        }

        private void setUpHistoryListView() {
            history_list.setAdapter(history_list_adapter);
            history_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    TextView textView = (TextView) view.findViewById(android.R.id.text1);
                    Intent intent = new Intent(getActivity(), ImageSearchActivity.class);
                    intent.putExtra("query", textView.getText().toString());
                    startActivity(intent);
                }
            });
        }

        private void setUpSearchHistory() {
            BufferedReader bufferedReader = null;
            ArrayList<String> tempArrayList = new ArrayList<String>();
            String line = null;
            try {
                bufferedReader = new BufferedReader(new
                        InputStreamReader(openFileInput(ImageSearchActivity.HISTORY_FILENAME)));
                while ((line = bufferedReader.readLine()) != null)
                    tempArrayList.add(line);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            history_list_adapter.addAll(tempArrayList);
            history_list_adapter.notifyDataSetChanged();
        }
    }
}
