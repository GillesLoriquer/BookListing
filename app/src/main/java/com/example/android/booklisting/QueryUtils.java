package com.example.android.booklisting;

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

public final class QueryUtils {
    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    public static final String LOG_TAG = MainActivity.class.getName();

    /**
     * Query the USGS dataset and return a list object containing all books from the URL
     */
    public static List<Book> fetchBookData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        List<Book> books = extractBooks(jsonResponse);

        // Return the {@link Event}
        return books;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
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
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
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
     * Return a list of book objects that has been built up from
     * parsing a JSON response.
     */
    private static List<Book> extractBooks(String jsonResponse) {
        // Create an empty List that we can start adding books to
        List<Book> books = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Parse the response given by the SAMPLE_JSON_RESPONSE string and
            // build up a list of Book objects with the corresponding data.
            JSONObject root = new JSONObject(jsonResponse);
            JSONArray JSONBooks = root.getJSONArray("items");

            for (int i = 0; i < JSONBooks.length(); i++) {
                JSONObject currentBook = JSONBooks.getJSONObject(i);
                JSONObject currentBookVolInfo = currentBook.getJSONObject("volumeInfo");

                // Book's fields
                String title;
                String authors = "";
                String year;
                String category = "";
                String pages;
                String description;
                String thumbnail;
                String infoLink;

                // Get title name
                if (existingField(currentBookVolInfo, "title")) {
                    title = currentBookVolInfo.getString("title");
                } else title = "No title provided";

                // Get author(s)'s name(s)
                if (existingField(currentBookVolInfo, "authors")) {
                    JSONArray currentBookVolInfoAuthors =
                            currentBookVolInfo.getJSONArray("authors");
                    int nbAuthors = currentBookVolInfoAuthors.length();
                    if (nbAuthors > 0) {
                        for (int j = 0; j < nbAuthors; j++) {
                            if (j == 0) authors += currentBookVolInfoAuthors.getString(j);
                            else authors += ", " + currentBookVolInfoAuthors.getString(j);
                        }
                    } else authors = "No author provided";
                } else authors = "No author provided";

                // Get publishedDate
                if (existingField(currentBookVolInfo, "publishedDate")) {
                    year = currentBookVolInfo.getString("publishedDate");
                } else year = "No published date provided";

                // Get category(ies)'s book
                if (existingField(currentBookVolInfo, "categories")) {
                    JSONArray currentBookVolInfoCategories =
                            currentBookVolInfo.getJSONArray("categories");
                    int nbCategories = currentBookVolInfoCategories.length();

                    for (int j = 0; j < nbCategories; j++) {
                        if (j == 0) category += currentBookVolInfoCategories.getString(j);
                        else category += ", " + currentBookVolInfoCategories.getString(j);
                    }
                } else category = "No category provided";

                // Get page count
                if (existingField(currentBookVolInfo, "pageCount")) {
                    pages = String.valueOf(currentBookVolInfo.getInt("pageCount")) + " pages";
                } else pages = "No page count provided";

                // Get description
                if (existingField(currentBookVolInfo, "description")) {
                    description = currentBookVolInfo.getString("description");
                } else description = "No description provided";

                // Get thumbnail
                if (existingField(currentBookVolInfo, "imageLinks")) {
                    JSONObject currentBookVolInfoImageLiknks =
                            currentBookVolInfo.getJSONObject("imageLinks");
                    thumbnail = currentBookVolInfoImageLiknks.getString("thumbnail");
                }
                // The "no picture" string is important here for Picasso to put a placeholder
                // image when no picture is available. Picasso checks if the String is null
                // or empty, hence the text "no picture"
                else thumbnail = "no picture";

                // Get info link
                if (existingField(currentBookVolInfo, "infoLink")) {
                    infoLink = currentBookVolInfo.getString("infoLink");
                } else infoLink = "No link provided";

                // Create a new Book with data extracted from json Object
                books.add(new Book(
                        title,
                        authors,
                        year,
                        category,
                        pages,
                        description,
                        thumbnail,
                        infoLink));
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the books JSON results", e);
        }
        // Return the list fo books
        return books;
    }

    private static boolean existingField(JSONObject jsonObject, String fieldName) {
        return jsonObject.has(fieldName);
    }
}
