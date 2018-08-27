package com.example.android.extraextranewsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

    // Tag for log messages.
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    // Create private constructor for QueryUtils, a class meant to hold static variables and methods.
    private QueryUtils() {
    }

    // Query Guardian API and return a list of news articles.
    public static List<Article> fetchArticleData(String requestUrl) {

        // Indicate data loading in the background
        // Added 8/15/2018, credit to Matthew Bailey, https://www.youtube.com/watch?v=Ai4JhyyFxcQ&feature=youtu.be
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
            Log.e(LOG_TAG, "fetchArticleData: Interrupted");
        }

        // Create URL object
        URL articleUrl = createUrl(requestUrl);

        //Perform HTTP request to URL and receive a JSON response
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(articleUrl);
        } catch (IOException e) {
            Log.e(LOG_TAG, "fetchArticleData: Problem making HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of Articles
        List<Article> articles = extractFieldFromJson(jsonResponse);

        // Return list of Articles.
        return articles;
    }

    // Returns new URL object from the given string URL.
    private static URL createUrl(String requestUrl) {
        URL url = null;
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException mue) {
            Log.e(LOG_TAG, "createUrl: Problem building the URL", mue);
        }
        return url;
    }

    // Make an HTTP request to the given URL and return a String as the response.
    private static String makeHttpRequest(URL articleUrl) throws IOException {
        String jsonResponse = "";

        // Check if URL is null
        if (articleUrl == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) articleUrl.openConnection();
            urlConnection.setReadTimeout(10000); // milliseconds
            urlConnection.setConnectTimeout(15000); // milliseconds
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the HTTP request was successful, then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "makeHttpRequest: Error code" + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "makeHttpRequest: Problem retrieving JSON results ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    // Convert the InputStream into a String which contains the whole JSON response from the server.
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    // Return a list of Article objects that has been built up from parsing the given JSON response.
    private static List<Article> extractFieldFromJson(String articleJSON) {
        String section;
        String title;
        String author;
        String date;
        String url;

        // Check if JSON string is empty or null
        if (TextUtils.isEmpty(articleJSON)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding articles to
        List<Article> articles = new ArrayList<>();

        // Try to parse the JSON response string
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJSONResponse = new JSONObject(articleJSON);
            JSONObject baseJSONResponseResult = baseJSONResponse.getJSONObject("response");

            // Extract JSONArray
            JSONArray articleArray = baseJSONResponseResult.getJSONArray("results");

            // Loop through each item in the articleArray
            for (int i = 0; i < articleArray.length(); i++) {

                // Get a single article at position i within list of articles
                JSONObject currentArticle = articleArray.getJSONObject(i);

                // For a given article, get section, title, and url
                section = currentArticle.getString("pillarName");
                title = currentArticle.getString("webTitle");
                url = currentArticle.getString("webUrl");

                // For a given article, get author. Check length and if more than one, add et al (TODO)
                author = currentArticle.getString("byline");

                // For a given article, get date (YYYY-MM-DDTHH:MM:SSZ) and reformat (TODO)
                date = currentArticle.getString("webPublicationDate");
            }

        } catch (JSONException je) {
            Log.e(LOG_TAG, "extractFieldFromJSON: Problem parsing JSON results", je);
        }

        // Return the list of articles
        return articles;

    }

}



