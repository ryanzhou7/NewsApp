package com.ryanzhou.company.newsapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ryanzhou.company.newsapp.model.Story;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryanzhou on 6/24/16.
 */
//with code adoption from https://github.com/udacity/Sunshine-Version-2
public class FetchStoriesTask extends AsyncTask<String, Void, String> {
    public final String LOG_TAG = getClass().getSimpleName();

    //QUERY INFO
    private final String METHOD = "GET";
    private final String BASE_URL = "http://content.guardianapis.com";
    private final String FEATURE_SEARCH = "search";
    private final String PARAM_ORDER = "order-by";
    private final String ORDER_NEWEST = "newest";
    private final String PARAM_PAGE_LIMIT = "page-size";
    private final String TOTAL_STORY_LIMIT = "10";
    private final String PARAM_API_KEY = "api-key";
    String API_KEY = BuildConfig.THE_GUARDIAN_API_KEY;

    //JSON RESULT
    private final String PARAM_RESPONSE = "response";
    private final String PARAM_STATUS_CODE = "status";
    private final String STATUS_CODE_OK = "ok";
    private final String PARAM_TOTAL_PAGES = "total";
    private final String PARAM_RESULTS = "results";

    private FetchStoryTaskListener mListener;

    public FetchStoriesTask(Context context) {
        if (context instanceof FetchStoryTaskListener) {
            mListener = (FetchStoryTaskListener) context;
        } else {
            Log.e(LOG_TAG, context.toString() + " must implement FetchStoryTaskListener");
        }
    }

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String booksJsonStr = null;
        try {
            URL fullUrl = new URL(buildFullUrlWithQuery(params[0]));
            urlConnection = (HttpURLConnection) fullUrl.openConnection();
            urlConnection.setRequestMethod(METHOD);
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null)
                return null;
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null)
                buffer.append(line);
            if (buffer.length() == 0)
                return null;
            booksJsonStr = buffer.toString();
            return booksJsonStr;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }

    private String buildFullUrlWithQuery(String term) {
        StringBuilder stringBuilderUrl = new StringBuilder(BASE_URL);
        stringBuilderUrl.append("/" + FEATURE_SEARCH);
        stringBuilderUrl.append("?" + PARAM_ORDER + "=" + ORDER_NEWEST);
        stringBuilderUrl.append("&" + PARAM_PAGE_LIMIT + "=" + TOTAL_STORY_LIMIT);
        stringBuilderUrl.append("&q=" + validated(term));
        stringBuilderUrl.append("&" + PARAM_API_KEY + "=" + API_KEY);
        return stringBuilderUrl.toString();
    }

    private String validated(String term) {
        return term.replaceAll(" ", "%20");
    }

    @Override
    protected void onPostExecute(String result) {
        if (result == null) {
            mListener.networkError();
            return;
        }
        try {
            JSONObject rootResult = new JSONObject(result);
            JSONObject response = rootResult.getJSONObject(PARAM_RESPONSE);
            String status = response.getString(PARAM_STATUS_CODE);
            if (!status.equalsIgnoreCase(STATUS_CODE_OK)) {
                return;
            }
            String pagesFound = response.getString(PARAM_TOTAL_PAGES);
            if (Integer.valueOf(pagesFound) == 0) {
                mListener.noResultsFound();
                return;
            }
            JSONArray jsonArrayStories = response.getJSONArray(PARAM_RESULTS);
            createModelFromJsonArray(jsonArrayStories);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createModelFromJsonArray(JSONArray result) {
        if (result == null || result.length() == 0) {
            mListener.fetchItemsDone(null);
        }
        List<Story> items = new ArrayList<>();
        for (int i = 0; i < result.length(); i++) {
            try {
                JSONObject jsonObject = result.getJSONObject(i);
                String webUrl = jsonObject.getString(Story.PARAM_WEB_URL);
                String webTitle = jsonObject.getString(Story.PARAM_WEB_TITLE);
                String sectionName = jsonObject.getString(Story.PARAM_SECTION_NAME);
                items.add(new Story(webTitle, webUrl, sectionName));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mListener.fetchItemsDone(items);
    }

    public interface FetchStoryTaskListener {
        void fetchItemsDone(List<Story> stories);

        void noResultsFound();

        void networkError();
    }

}