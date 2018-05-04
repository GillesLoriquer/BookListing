package com.example.android.booklisting;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Loads a list of earthquakes by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class BookLoader extends android.support.v4.content.AsyncTaskLoader<List<Book>> {
    /** Query URL */
    private String mURL;

    /**
     * Constructs a new {@link BookLoader}
     *
     * @param context of the activity
     * @param url to load data from
     */
    public BookLoader(Context context, String url) {
        super(context);
        this.mURL = url;
    }

    /**
     * forceload() forces the call to {@link #loadInBackground()} method
     */
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<Book> loadInBackground() {
        if (mURL == null) return null;
        else return QueryUtils.fetchBookData(mURL);
    }
}
