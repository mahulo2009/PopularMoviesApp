package com.example.android.popularmovies.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by mhuertas on 7/01/17.
 */

public class Utility {

    private static final String LOG_TAG = FetchReviewTask.class.getSimpleName();



    /**
     * Checks the device is connected to Internet.
     *
     * @return
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public static int dpToPx(Context context,int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }


    public static String getFirstNStrings(String str, int n) {
        String[] sArr = str.split(" ");
        String firstStrs = "";
        for(int i = 0; (i < n) && (i<sArr.length); i++)
            firstStrs += sArr[i] + " ";
        return firstStrs.trim();
    }

    public static Spanned buildUrlReadMore(String str) {
        //TODO String in other file
        String result = "<html><a href="+str+">Read More...</a></html>";
        return Html.fromHtml(result);
    }

    public static Uri buildUriTrailer(String key) {
        return Uri.parse("http://www.youtube.com/watch?v=" + key);
    }

    /**
     * Extract the year string from the data
     *
     * @param date  date string YY-mm-dd
     *
     * @return      Year string or empty if a problem.
     */
    public static String extractYear(String date) {
        String[] tokens = date.split("-");
        if (tokens.length!=0) {
            return tokens[0];
        }
        return "";
    }

    public static String buildImageFirstFotogram(String url) {
        String result = "http://img.youtube.com/vi/"+url+"/0.jpg";
        return result;
    }

    /**
     * Build the complete image from the relative path.
     *
     * @param poster_path   relative image path
     * @param imageSize     the image size according to API movie service, the valid values are:
     *                      w92, w154, w185, w342, w500, w780.
     *
     * @return              The complete image path
     */
    public static String getImagePath(String poster_path,String imageSize)  {
        URI uri = null;
        try {
            uri = new URI("http","image.tmdb.org","/t/p/"+imageSize + poster_path,null);
        } catch (URISyntaxException e) {
            return "";
        }
        return uri.toString();
    }


    /**
     * Append the max rating to the rate string
     *
     * @param rate      rate string
     *
     * @return          rate + /10
     */
    public static String appendRating(String rate) {
        return rate+"/10";
    }


    public static String request(URL url) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            // Create the request to Themoviedb, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            return buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the movie data, there's no point in attemping
            // to parse it.
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }

}
