package com.example.android.booklisting;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements android.support.v4.app.LoaderManager.LoaderCallbacks<List<Book>> {

    // ID of the loader affected to the
    private static final int LOADER_ID = 0;

    private static final String URL = "https://www.googleapis.com/books/v1/" +
            "volumes?q=android&maxResults=40";

    private BookCardViewAdapter mBookCardViewAdapter;

    private RecyclerView mRecyclerView;

    private TextView mIssueTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Declare a new List of books
        List<Book> books = new ArrayList<>();

        // Get the recyclerview
        mRecyclerView = findViewById(R.id.recyclerview);

        // Get the issue_textview in case of issue displaying data (no internet connection
        // or no data provided by server
        mIssueTextView = findViewById(R.id.issue_textview);

        // 1. Create a new layoutManager to help positioning elements within a view
        // 2. Set this layout manager on the recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        // Create a new BookCardViewAdapter passing a new list of book and a listener
        mBookCardViewAdapter = new BookCardViewAdapter(
                this,
                books,
                null);

        // Set the adapter on the recyclerview
        mRecyclerView.setAdapter(mBookCardViewAdapter);




        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connectivityManager =
                (ConnectivityManager)this.getSystemService(MainActivity.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        // If there is a network connection, fetch data
        if (isConnected) {
            // Now we are connected the only chance we can have a issue displaying data
            // is because there is no data returned by the server
            mIssueTextView.setText("No data available.");

            // Prepare the loader.  Either re-connect with an existing one,
            // or start a new one. Basically it calls the onCreateLoader method below
            getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        }
        // Otherwise, display error
        else {
            // If we are not connected to internet we set the message to be "No internet
            // connection"
            mIssueTextView.setText("No internet connection.");
            mIssueTextView.setVisibility(View.VISIBLE);
        }
    }

    // Create and return a new BookLoader with the URL used by the loadInBackground
    // to fetch the data
    @Override
    public android.support.v4.content.Loader<List<Book>> onCreateLoader(
            int id,
            Bundle args) {
        // Create a new loader for the given URL
        return new BookLoader(this, URL);
    }

    // When the background task is finished we pass the data result to the adapter for it to
    // bind the datas with the views
    @Override
    public void onLoadFinished(android.support.v4.content.Loader<List<Book>> loader,
                               List<Book> data) {
        // Clear the adapter of previous book data
        mBookCardViewAdapter.clear();

        // If there is a valid list of data, then add them to the adapter's
        // data set. This will trigger the RecyclerView to update.
        if (data != null && !data.isEmpty()) {
            mIssueTextView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mBookCardViewAdapter.addAll(data);
        }
        else {
            mRecyclerView.setVisibility(View.GONE);
            mIssueTextView.setVisibility(View.VISIBLE);
        }
    }

    // When the job is done, we call onLoadReset to clear the adapter cause the data have
    // already be fetched
    @Override
    public void onLoaderReset(android.support.v4.content.Loader<List<Book>> loader) {
        // Loader reset, so we can clear out our existing data.
        mBookCardViewAdapter.clear();
    }
}