package jay.droid.googleimagesearch;

import android.widget.AbsListView;

/**
 * Created by jay on 2/11/14.
 */
public abstract class EndlessScrollListener implements AbsListView.OnScrollListener {

    private boolean loading = true;
    private int previousTotal = 0;
    private int visibleThreshold = 4;

    public EndlessScrollListener() {
    }

    public EndlessScrollListener(int _visibleThreshold) {
        visibleThreshold = _visibleThreshold;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (totalItemCount < previousTotal)
            previousTotal = 0;
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        else if (!loading && ((totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold))) {
            loading = true;
            onScrollEnd(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    public abstract void onScrollEnd(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount);
}
