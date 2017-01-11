package com.example.android.popularmovies.app;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.popularmovies.app.data.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mhuertas on 9/01/17.
 */

public class FetchReviewTask extends AsyncTask<String,Void,Review[]> {

    private final String LOG_TAG = FetchReviewTask.class.getSimpleName();
    //TODO The number of words depending on layout
    private static int FIRST_N_WORDS = 15;
    private Context mContext;
    private View mView;

    public FetchReviewTask(Context context,View view) {
        this.mContext=context;
        this.mView=view;
    }

    @Override
    protected Review[] doInBackground(String... params) {
        Uri builtUri = buildUri(params[0]);

        Log.v(LOG_TAG, "Reviews URL: " + builtUri.toString());
        try {
            URL url = new URL(builtUri.toString());
            return parseResponce(Utility.request(url));
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, e.getMessage());
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return null;
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @Override
    protected void onPostExecute(Review[] result) {
        super.onPostExecute(result);

        if (result != null) {
            for (Review review: result) {
                //Add Card View
                LinearLayout layout = (LinearLayout)mView.findViewById(R.id.moview_linear_layout);
                layout.addView(buildReviewCard(review));
            }
        }
    }

    private CardView buildReviewCard(Review review) {
        CardView card = new CardView( new ContextThemeWrapper(mContext, R.style.CardViewStyle) , null, 0);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int margin = dpToPx(5);
        params.setMargins(margin, margin, margin, margin);
        card.setLayoutParams(params);

        LinearLayout cardInner = new LinearLayout(new ContextThemeWrapper(mContext, R.style.Widget_CardContent));

        TextView tv_title = new TextView(new ContextThemeWrapper(mContext, R.style.CardViewTextStyle));
        tv_title.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        tv_title.setText("Review by " + review.getAuthor()); //TODO String in other file.

        TextView tv_overview = new TextView(new ContextThemeWrapper(mContext, R.style.CardViewTextStyle));
        tv_overview.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        tv_overview.setText(Utility.getFirstNStrings(review.getContent(),FIRST_N_WORDS));

        //TODO For the moment it shows the URL, it will be possible to use a fragment to show the
        //complete review.
        TextView tv_url = new TextView(new ContextThemeWrapper(mContext, R.style.CardViewTextStyle));
        tv_url.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        tv_url.setText(Utility.buildUrl(review.getUrl()));
        tv_url.setMovementMethod(LinkMovementMethod.getInstance());

        cardInner.addView(tv_title);
        cardInner.addView(tv_overview);
        cardInner.addView(tv_url);

        card.addView(cardInner);

        return card;
    }

    private Uri buildUri(String ID) {
        // Construct the URL
        final String APPID_PARAM = "api_key";

        Uri.Builder builder = new Uri.Builder();
        Uri builtUri = builder.scheme("http")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(ID)
                .appendPath("reviews")
                .appendQueryParameter(APPID_PARAM,BuildConfig.THE_MOVIE_DB_API_KEY)
                .build();
        return builtUri;
    }

    private Review[] parseResponce(String reviewsJsonStr) throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String REVIEW_ID = "id";
        final String REVIEW_AUTHOR = "author";
        final String REVIEW_CONTENT = "content";
        final String REVIEW_URL = "url";
        final String REVIEW_LIST = "results";

        JSONObject reviewsJson = new JSONObject(reviewsJsonStr);
        JSONArray reviewsArray = reviewsJson.getJSONArray(REVIEW_LIST);
        Review[] results = new Review[reviewsArray.length()];
        for(int i = 0; i < reviewsArray.length(); i++) {
            // Get the JSON object representing a movie
            JSONObject reviewJson = reviewsArray.getJSONObject(i);

            //Build the movie entry string
            Review review = new Review();
            review.setId(reviewJson.getString(REVIEW_ID));
            review.setAuthor(reviewJson.getString(REVIEW_AUTHOR));
            review.setContent(reviewJson.getString(REVIEW_CONTENT));
            review.setUrl(reviewJson.getString(REVIEW_URL));

            results[i] = review;
        }

        for (Review s : results) {
            Log.v(LOG_TAG, "Trailer entry: " + s);
        }
        return results;
    }

}
