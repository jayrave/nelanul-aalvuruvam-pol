package jay.droid.googleimagesearch;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Xml;
import android.widget.ArrayAdapter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jay on 2/9/14.
 */
public class BingImageLinkRetriever {

    public static final int TOP_COUNT = 10;
    private int skip_count = 0;
    private XmlPullParser parser = null;
    private InputStream inputStream = null;
    private ArrayAdapter adapter = null;
    private String storedSearchQuery = null;

    public BingImageLinkRetriever() {
        parser = Xml.newPullParser();
        try {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    public void updateAdapter(String searchQuery, ArrayAdapter _adapter) {
        skip_count = 0;
        storedSearchQuery = searchQuery.replaceAll(" ", "%20");
        adapter = _adapter;
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(storedSearchQuery);
    }

    public void updateAdapter() {
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(storedSearchQuery);
    }

    private class DownloadTask extends AsyncTask<String, Void, List> {

        final String accountKey = "UHP/70aZsp3uvJ+s7HFPzJfEi2M90LEU5Kf4PBvyBBc=";
        final String defaultURL = "https://api.datamarket.azure.com/Bing/Search/v1/Image?Query=%27";
        String accountAccessString = null;

        @Override
        protected List doInBackground(String... strings) {
            List readItems = null;

            setUpParser(strings[0]);
            try { readItems = readFeed();
            } catch (IOException e) { e.printStackTrace();
            } catch (XmlPullParserException e) { e.printStackTrace();
            }

            return readItems;
        }

        @Override
        protected void onPostExecute(List l) {
            adapter.addAll((ArrayList<BingImageEntry>) l);
            adapter.notifyDataSetChanged();
        }

        private void setUpParser(String searchText){
            setUpStream(searchText);
            try {
                parser.setInput(inputStream, null);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        }

        private List readFeed() throws IOException, XmlPullParserException {
            ArrayList<BingImageEntry> items = new ArrayList<BingImageEntry>();
            parser.next();
            parser.next();
            while(parser.next() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() != XmlPullParser.START_TAG)
                    continue;
                else {
                    String name = parser.getName();
                    if (name.equals("entry"))
                        items.add(extractEntry());
                    else
                        skip();
                }
            }
            return items;
        }

        private BingImageEntry extractEntry() throws IOException, XmlPullParserException {
            String link = null;
            String thumbLink = null;

            if (goToTag("content")) {
                if (goToTag("m:properties")) {
                    while(parser.next() != XmlPullParser.END_TAG) {
                        if (parser.getEventType() != XmlPullParser.START_TAG)
                            continue;
                        else {
                            String name = parser.getName();
                            if (name.equals("d:MediaUrl")) {
                                parser.next();
                                link = parser.getText();
                                parser.next();
                            }
                            else if (name.equals("d:Thumbnail")) {
                                if (goToTag("d:MediaUrl")) {
                                    parser.next();
                                    thumbLink = parser.getText();
                                    parser.next();
                                }
                            }
                            else
                                skip();
                        }
                    }
                }
            }
            BingImageEntry item = new BingImageEntry(link, thumbLink);
            return item;
        }

        private boolean goToTag(String tagName) throws IOException, XmlPullParserException {
            while(parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG)
                    continue;
                else {
                    String name = parser.getName();
                    if (name.equals(tagName))
                        return true;
                    else
                        skip();
                }
            }
            return false;
        }

        private void skip() throws XmlPullParserException, IOException {
            int depth = 1;
            while (depth != 0) {
                switch (parser.next()) {
                    case XmlPullParser.END_TAG:
                        depth--;
                        break;
                    case XmlPullParser.START_TAG:
                        depth++;
                        break;
                }
            }
        }

        private void setUpStream(String searchText) {
            byte[] accountAccess = ("accountKey:" + accountKey).getBytes();
            byte[] accountAccessBytes = Base64.encode(accountAccess, Base64.DEFAULT);
            accountAccessString = new String(accountAccessBytes);

            try {
                URL url = new URL(defaultURL + searchText + "%27&$top=" + TOP_COUNT +
                        "&$skip=" + skip_count + "&$format=Atom");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Basic " + accountAccessString);
                inputStream = conn.getInputStream();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            skip_count = skip_count + TOP_COUNT;
        }
    }
}

