package com.example.android.popularmovies.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.android.popularmovies.app.data.Trailer;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import static com.example.android.popularmovies.app.Utility.buildImageFirstFotogram;
import static com.example.android.popularmovies.app.Utility.buildUriTrailer;

/**
 * Created by mhuertas on 7/01/17.
 */
public class FetchTrailerTask extends AsyncTask<String,Void,Trailer[]> {

    private final String LOG_TAG = FetchTrailerTask.class.getSimpleName();
    private View mView;
    private Context mContext;
    private Callback mCallback;

    public FetchTrailerTask(Context context, View view,Callback callback) {
        this.mContext=context;
        mView=view;
        this.mCallback=callback;
    }

    @Override
    protected Trailer[] doInBackground(String... params) {
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

    @Override
    protected void onPostExecute(Trailer[] result) {
        super.onPostExecute(result);
        if (result != null) {
            LinearLayout layout = (LinearLayout)mView.findViewById(R.id.moview_linear_layout);
            //Add Card View
            layout.addView(buildTrailerCard(result));
        }
        mCallback.onTrailerInserted();
    }

    private CardView buildTrailerCard(Trailer[] trailers) {

        int margin = Utility.dpToPx(mContext,5);
        int padding = margin;

        CardView card = new CardView( new ContextThemeWrapper(mContext, R.style.CardViewStyle) , null, 0);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(margin, margin, margin, margin);
        card.setLayoutParams(params);

        HorizontalScrollView scrollView = new HorizontalScrollView(mContext);
        LinearLayout cardInner = new LinearLayout(new ContextThemeWrapper(mContext, R.style.Widget_CardContent));
        cardInner.setOrientation(LinearLayout.HORIZONTAL);

        for (Trailer trailer : trailers)  {
            ImageView iv_trailer = new ImageView(mContext);
            iv_trailer.setTag(trailer.getKey());
            iv_trailer.setPadding(padding ,padding ,padding ,padding );
            iv_trailer.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  String key = (String) v.getTag();
                                                  Intent webIntent = new Intent(Intent.ACTION_VIEW,
                                                         buildUriTrailer(key));
                                                  webIntent.putExtra("force_fullscreen",true);
                                                  mContext.startActivity(webIntent);
                                              }
                                          });
            Picasso.with(mContext).load(buildImageFirstFotogram(trailer.getKey())).into(iv_trailer);
            cardInner.addView(iv_trailer);
        }
        scrollView.addView(cardInner);
        card.addView(scrollView);

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
                .appendPath("videos")
                .appendQueryParameter(APPID_PARAM,BuildConfig.THE_MOVIE_DB_API_KEY)
                .build();
        return builtUri;
    }

    private Trailer[] parseResponce(String trailersJsonStr) throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String TRAILER_ID = "id";
        final String TRAILER_KEY = "key";
        final String TRAILER_NAME = "name";
        final String TRAILER_LIST = "results";

        JSONObject trailersJson = new JSONObject(trailersJsonStr);
        JSONArray trailersArray = trailersJson.getJSONArray(TRAILER_LIST);
        Trailer[] results = new Trailer[trailersArray.length()];
        for(int i = 0; i < trailersArray.length(); i++) {
            // Get the JSON object representing a movie
            JSONObject trailerJson = trailersArray.getJSONObject(i);

            //Build the movie entry string
            Trailer trailer = new Trailer();
            trailer.setId(trailerJson.getString(TRAILER_ID));
            trailer.setKey(trailerJson.getString(TRAILER_KEY));
            trailer.setName(trailerJson.getString(TRAILER_NAME));

            results[i] = trailer;
        }
        for (Trailer s : results) {
            Log.v(LOG_TAG, "Trailer entry: " + s);
        }
        return results;
    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onTrailerInserted();
    }

}
