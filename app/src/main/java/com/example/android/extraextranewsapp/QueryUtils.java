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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;


// Helper methods for requesting and receiving article data from The Guardian.

public class QueryUtils {

    // Tag for log messages.
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    // Create private constructor for QueryUtils. This class is meant to hold static variables and methods.
    private QueryUtils() {
    }

    // Return Article objects resulting from parsing a JSON response.

    public static List<Article> fetchArticleData(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        //Perform HTTP request to the URL and receive a JSON response
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of Articles
        List<Article> articlesList = extractFieldFromJson(jsonResponse);

        // Return list of Articles.
        return articlesList;
    }

    // Returns new URL object from the given string URL.
    private static URL createUrl(String requestUrl) {
        URL url = null;
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL", e);
        }
        return url;
    }

    // Make an HTTP request to the given URL and return a String as the response.

    private static String makeHttpRequest(URL articleUrl) throws IOException {
        String jsonResponse = "";

        // Check if URL is null.
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
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the article JSON results ", e);
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

        // Check if JSON string is empty or null.
        if (TextUtils.isEmpty(articleJSON)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding articles to
        List<Article> articles = new ArrayList<>();

        // Try to parse the JSON response string. If there'ss a problem, a JSONException will be thrown.
        // Catch the exception so the app doesn't crash and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string.
            JSONObject baseJSONResponse = new JSONObject(articleJSON);

            // Extract the JSONArray associated with the key called "response."
            JSONObject baseJSONResponseResult = baseJSONResponse.getJSONObject("response");

            // Extract JSONArray associated with the key called "results."
            JSONArray articleArray = baseJSONResponseResult.getJSONArray("results");

            // Loop through each item in the articleArray and create a Article object.
            for (int i = 0; i < articleArray.length(); i++) {

                // Get a single article at position i within list of articles
                JSONObject currentArticle = articleArray.getJSONObject(i);

                // For a given article, get the value for the keys related to section, title, and url
                String sectionName = currentArticle.getString("sectionName");
                String articleTitle = currentArticle.getString("webTitle");
                String articleUrl = currentArticle.getString("webUrl");

                // For a given article, get the value for the key related to date.
                // Set with the reformatted date.
                String webPubDate = currentArticle.getString("webPublicationDate");
                String articleDate = reformattedDate(webPubDate);

                // For a given article, get the value for the key related to author.
                // Check if the JSONObject has the key "fields."
                // If so, extract the value for the key called "byline."
                String articleAuthor = " ";
                if (currentArticle.has("fields")) {
                    JSONObject fieldsObject = currentArticle.getJSONObject("fields");
                    if (fieldsObject != null && fieldsObject.has("byline")) {
                        articleAuthor = fieldsObject.getString("byline");
                        articleAuthor = "By " + articleAuthor;
                    }
                }

                // Create a new Article object with section, title, reformatted date, author.
                Article article = new Article(sectionName, articleTitle, articleAuthor, articleDate,
                        articleUrl);

                // Add the article to the list of articles.
                articles.add(article);
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing article JSON results", e);
        }

        // Return the list of articles.
        return articles;

    }

    // Parse the webPubDate "YYYY-MM-DDTHH:MM:SSZ" and reformat to "MM dd yyyy").
    // (Credit: Java SimpleDateFormat on 8/27/2018, http://tutorials.jenkov.com/java-internationalization/simpledateformat.html,
    // which is a tutorial posted to Slack on 8/22/2018 by ABND Scholar Charles Rowland.)
    private static String reformattedDate(String webPubDate) {
        Date date = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            date = simpleDateFormat.parse(webPubDate);
        } catch (ParseException e) {
            Log.e("QueryUtils", "Problem parsing the date", e);
        }

        SimpleDateFormat simpleDateFormatResult = new SimpleDateFormat("MMM dd, yyyy");
        String reformattedDate = simpleDateFormatResult.format(date);
        return reformattedDate;
    }
}



