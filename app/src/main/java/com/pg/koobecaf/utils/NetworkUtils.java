package com.pg.koobecaf.utils;

import android.net.Uri;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the network.
 */
public class NetworkUtils {

    final static String JPLACEHOLDER_BASE_URL =
            "https://jsonplaceholder.typicode.com";

    public final static String POSTS = "posts";
    public final static String COMMENTS = "comments";
    public final static String ALBUMS = "albums";
    public final static String PHOTOS = "photos";
    public final static String CONTACTS = "users";
    /*
     * The sort field. One of stars, forks, or updated.
     * Default: results are sorted by best match if no field is specified.
     */

    /**
     * Builds the URL used to query GitHub.
     *
     * @param resource The keyword that will be queried for.
     * @return The URL to use to query the GitHub.
     */
    public static URL buildUrl(String resource,@Nullable String option, @Nullable String id) {
        Uri builtUri = Uri.parse(JPLACEHOLDER_BASE_URL).buildUpon()
                .appendPath(resource)
                .appendQueryParameter(option,id)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}