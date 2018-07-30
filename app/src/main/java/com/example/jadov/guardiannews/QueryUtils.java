package com.example.jadov.guardiannews;

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

    private static final String LOG_TAG = NewsActivity.class.getName();

    private QueryUtils() {
    }

    // return a list of {@link News} objects that is built from parsing the JSON response
    private static List<News> extractFeatureFromJson(String newsJson) {
        // if the JSON string is empty or null, then return early
        if (TextUtils.isEmpty(newsJson)) {
            return null;
        }

        // create an empty ArrayList to add new News objects to
        List<News> news = new ArrayList<>();

        // try to parse the JSON response and throw an error if there's a problem with the format
        try {

            // create a JSONObject from the JSON response string
            JSONObject newsJsonResponse = new JSONObject(newsJson);

            // extract the JSONObject associated with the key called "response"
            JSONObject response = newsJsonResponse.getJSONObject("response");

            // extract the JSONArray associated with the key called "results"
            JSONArray newsArray = response.getJSONArray("results");

            // for each news article in the newsArray, create an {@link News} object
            for (int i = 0; i < newsArray.length(); i++) {

                // get a single news article at position i within the list of news
                JSONObject currentNews = newsArray.getJSONObject(i);

                // extract the value associated with the key called "sectionName"
                String section = currentNews.getString("sectionName");

                // extract the value associated with the key called "webPublicationDate"
                String date = currentNews.getString("webPublicationDate");

                // extract the value associated with the key called "webTitle"
                String title = currentNews.getString("webTitle");

                // extract the value associated with the key called "webUrl"
                String url = currentNews.getString("webUrl");

                // create a StringBuilder for news article author(s)
                StringBuilder author = new StringBuilder("By: ");

                // extract the JSONArray associated with the key called "tags"
                JSONArray authorArray = currentNews.getJSONArray("tags");

                // determine if the authorArray is not null and length is greater than 0 in order
                // to display a list of author(s)
                if (authorArray != null && authorArray.length() > 0) {

                    // for each author list them accordingly
                    for (int j = 0; j < authorArray.length(); j++) {

                        // get a single author at position j within the list of author(s)
                        JSONObject authors = authorArray.getJSONObject(j);

                        // extract the value associated with the key called "webTitle"
                        String authorsListed = authors.getString("webTitle");

                        // if the authorArray is not null and length is greater than 1, then
                        // list all authors separated by tabs
                        if (authorArray != null && authorArray.length() > 1) {
                            author.append(authorsListed);
                            author.append("\t\t\t");

                            // if there is only 1 author, then list just that author
                        } else {
                            author.append(authorsListed);
                        }
                    }
                    // if there are no authors within the authorsArray, then state "No author(s) listed"
                } else {
                    author.replace(0, 3, "No author(s) listed");
                }

                // create a new {@link News} object with the information from the JSON response
                News news1 = new News(section, date, title, author.toString(), url);

                // add the new {@link News} to the list of news
                news.add(news1);
            }
            // if an error is thrown from the above JSON parsing, then catch the exception, so the app doesn't crash
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the news JSON results", e);
        }

        // return the list of news
        return news;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
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

    /**
     * Query the Guardian dataset and return a list of {@link News} objects.
     */
    public static List<News> fetchNewsData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link News}
        List<News> news = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link News}
        return news;
    }
}
