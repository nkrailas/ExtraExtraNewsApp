package com.example.android.extraextranewsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import java.util.List;

// Loads a list of articles by using an AsyncTask to perform the network request to the given URL

public class ArticleLoader extends AsyncTaskLoader<List<Article>> {

    // Tag for log messages
    private static final String LOG_TAG = ArticleLoader.class.getName();

    private String uUrl;

    /**
     * Constructs a new ArticleLoader
     *
     * @param context of the activity
     * @param url     to load data from
     */

    public ArticleLoader(Context context, String url) {
        super(context);
        this.uUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    // This is on a background thread.
    @Override
    public List<Article> loadInBackground() {
        if (uUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of articles.
        List<Article> articlesList = QueryUtils.fetchArticleData(uUrl);
        return articlesList;

    }
}
