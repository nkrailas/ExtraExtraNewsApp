package com.example.android.extraextranewsapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<Article>> {

    private static final String LOG_TAG = MainActivity.class.getName();

    // Constant value for article loader ID.
    public static final int ARTICLE_LOADER_ID = 1;
    // URL for article data from the Guardian API.
    private static final String GUARDIAN_REQUEST_URL =
            "https://content.guardianapis.com/search?api-key=863cbb53-872a-4af3-9897-dadd1eb75a01&" +
                    "show-tags=article,contributor&show-fields=headline,byline";
    // Adapter for the list of articles.
    private ArticleAdapter articleAdapter;

    // TextView that is displayed when the list is empty.
    private TextView emptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the ListView in the layout.
        ListView articleListView = findViewById(R.id.article_list);

        // Set an empty state TextView onto the ListView.
        emptyStateTextView = findViewById(R.id.empty_view);
        articleListView.setEmptyView(emptyStateTextView);

        // Create a new adapter that takes an empty list of news articles as input.
        articleAdapter = new ArticleAdapter(this, new ArrayList<Article>());

        // Set the adapter on the ListView so the list can be populated in the UI.
        articleListView.setAdapter(articleAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser to
        // open a website with more information about the selected article.
        articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                // Get the url from the current article that was clicked on.
                Article currentArticle = articleAdapter.getItem(position);

                // Convert the String url into a URI object (to pass into the Intent constructor).
                Uri articleUri = Uri.parse(currentArticle.getArticleUrl());

                // Create a new intent that specifies an action to view the article URI.
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, articleUri);

                // Send the intent to launch a new activity.
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the Connectivity Manager to check state of network connectivity.
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default area network.
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data.
        if (networkInfo != null && networkInfo.isConnected()) {

            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.VISIBLE);

            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter.

            loaderManager.initLoader(ARTICLE_LOADER_ID, null, this);

        } else {
            // Otherwise, hide loading indicator and display error message.
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
        }
        // Update empty state TextView to display message.
        emptyStateTextView.setText(R.string.no_internet_connection);
    }

    @Override
    public Loader<List<Article>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        return new ArticleLoader(this, GUARDIAN_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> articles) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display message.
        emptyStateTextView.setText(R.string.no_articles_found);

        // Clear the adapter of previous data
        articleAdapter.clear();

        // If there is a valid list of Articles, then add to adapter's data set. This will
        // trigger the ListView to update.
        if (articles != null && !articles.isEmpty()) {
            articleAdapter.addAll(articles);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        // Reset loader to clear existing data.
        articleAdapter.clear();
    }

}





