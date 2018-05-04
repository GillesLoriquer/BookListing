package com.example.android.booklisting;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements android.support.v4.app.LoaderManager.LoaderCallbacks<List<Book>> {

    // Define a new LOG_TAG final string to store the MainActivity class name
    public static final String LOG_TAG = MainActivity.class.getName();

    // ID of the loader affected to the
    private static final int LOADER_ID = 0;

    // First common URL part that needs to be completed
    private static final String BOOKS_URL_SIMPLE = "https://www.googleapis.com/books/v1/volumes?q=";

    // This one will be updated every time a new search is done
    private static String mBooksUrl;

    // Defining a variable to store the context of the activity
    private Context mContext;

    // The callbacks through which we will interact with the LoaderManager.
    private LoaderManager.LoaderCallbacks<List<Book>> mCallbacks;

    // Store a BookCardViewAdapter
    private BookCardViewAdapter mBookCardViewAdapter;

    // Store the RecyclerView
    private RecyclerView mRecyclerView;

    // Store the IssueTextView to set for every case we encounter
    private TextView mIssueTextView;

    // Store the IssueTextView to set for every case we encounter
    private ProgressBar mProgressBar;

    /**
     * onCreate method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hold the context of the activity
        mContext = this;

        // The callbacks through which we will interact with the LoaderManager.
        mCallbacks = this;

        // Declare a new List of books
        final List<Book> books = new ArrayList<>();

        // Because the MainActivity is set to singleTop launch mode we need to handle
        // intents from the onCreate method and the onNewIntent method
        handleIntent(getIntent());

        // Get the recyclerview
        mRecyclerView = findViewById(R.id.recyclerview);

        // Get the issue_textview in case of issue displaying data (no internet connection
        // or no data provided by server
        mIssueTextView = findViewById(R.id.issue_textview);

        // Get the progress bar view to use when a fetch is performed
        mProgressBar = findViewById(R.id.progressbar_view);

        // 1. Create a new layoutManager to help positioning elements within a view
        // 2. Set this layout manager on the recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        // Create a new BookCardViewAdapter passing a new list of book and a listener
        mBookCardViewAdapter = new BookCardViewAdapter(
                this,
                books,
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Book currentBook = (books.get(position));
                        Intent intent = new Intent(mContext, BookDetail.class);
                        intent.putExtra("book", currentBook);
                        startActivity(intent);
                    }
                });

        // Set the adapter on the recyclerview
        mRecyclerView.setAdapter(mBookCardViewAdapter);
    }

    /**
     * onCreateOptionsMenu
     * This method is used to inflate the menu options to display the search bar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        final android.support.v7.widget.SearchView searchView =
                (android.support.v7.widget.SearchView)
                        menu.findItem(R.id.id_search).getActionView();

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        // Set a listener to know when text is submited of changed
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Reset SearchView
                searchView.clearFocus();
                searchView.setQuery("", false);
                searchView.setIconified(true);
                // Set activity title to search query
                MainActivity.this.setTitle(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    /**
     * onNewIntent
     * Set an intent et call handleIntent
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        // Because the MainActivity is set to singleTop launch mode we need to handle
        // intents from the onCreate method and the onNewIntent method
        setIntent(intent);
        handleIntent(getIntent());
    }

    /**
     * handleIntent
     * Perform the setting of the query to fetch
     * Check the connectivity and set the right view to display depending on the result
     * Fetch the data based on the URL
     * @param intent
     */
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            // This is the first common part of the URL
            mBooksUrl = BOOKS_URL_SIMPLE;

            // Where we are splitting the user search text to make a right url
            // We also add the maxResults parameter with a value of 40
            mBooksUrl += Utils.getSplittedFromSpacesString(query) + "&maxResults=40";

            // Get a reference to the ConnectivityManager to check state of network connectivity
            ConnectivityManager connectivityManager =
                    (ConnectivityManager)mContext
                            .getSystemService(MainActivity.CONNECTIVITY_SERVICE);

            // Get details on the currently active default data network
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            // If there is a network connection, fetch data
            if (isConnected) {
                // Now we are connected the only chance we can have a issue displaying data
                // is because there is no data returned by the server
                mIssueTextView.setText(R.string.no_data_available);

                // Prepare the loader.  Either re-connect with an existing one,
                // or start a new one. Basically it calls the onCreateLoader method below
                // Added the restartLoader cause if a first search has been done, no new
                // search was working...
                getSupportLoaderManager().restartLoader(LOADER_ID, null, mCallbacks);
                getSupportLoaderManager().initLoader(LOADER_ID, null, mCallbacks);
                mIssueTextView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
            }
            // Otherwise, display error
            else {
                // If we are not connected to internet we set the message to be "No internet
                // connection"
                mIssueTextView.setText(R.string.no_internet_connection);
                mIssueTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * onCreateLoader
     * Create and return a new BookLoader with the URL used by the loadInBackground
     * to fetch the data
     * @param id
     * @param args
     * @return
     */
    @Override
    public android.support.v4.content.Loader<List<Book>> onCreateLoader(
            int id,
            Bundle args) {
        // Create a new loader for the given URL
        return new BookLoader(this, mBooksUrl);
    }

    /**
     * onLoadFinished
     * When the background task is finished we pass the data result to the adapter for it to
     * bind the datas with the views
     * @param loader
     * @param data
     */
    @Override
    public void onLoadFinished(android.support.v4.content.Loader<List<Book>> loader,
                               List<Book> data) {
        // Clear the adapter of previous book data
        mBookCardViewAdapter.clear();

        // If there is a valid list of data, then add them to the adapter's
        // data set. This will trigger the RecyclerView to update.
        if (data != null && !data.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            mIssueTextView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mBookCardViewAdapter.addAll(data);
        }
        else {
            mProgressBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
            mIssueTextView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * onLoaderReset
     * When the job is done, we call onLoadReset to clear the adapter cause the data have
     * already be fetched and displayed
     * @param loader
     */
    @Override
    public void onLoaderReset(android.support.v4.content.Loader<List<Book>> loader) {
        // Loader reset, so we can clear out our existing data.
        mBookCardViewAdapter.clear();
    }
}