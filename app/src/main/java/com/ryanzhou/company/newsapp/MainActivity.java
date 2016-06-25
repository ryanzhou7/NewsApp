package com.ryanzhou.company.newsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.ryanzhou.company.newsapp.model.Story;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FetchStoriesTask.FetchStoryTaskListener,
        StoryRecyclerViewAdapter.OnRecyclerViewInteraction {

    public final String LOG_TAG = getClass().getSimpleName();
    private TextView textViewInfoMessage;
    private RecyclerView recyclerView;
    private StoryRecyclerViewAdapter storyRecyclerViewAdapter;
    private EditText editTextQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextQuery = (EditText) findViewById(R.id.editTextQuery);
        textViewInfoMessage = (TextView) findViewById(R.id.textViewError);
        recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        storyRecyclerViewAdapter = new StoryRecyclerViewAdapter(new ArrayList<Story>(), MainActivity.this);
        recyclerView.setAdapter(storyRecyclerViewAdapter);
        if (Utility.isNetworkAvailable(getApplicationContext()))
            textViewInfoMessage.setVisibility(View.GONE);

        editTextQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE)) {
                    if (Utility.isNetworkAvailable(getApplicationContext())) {
                        FetchStoriesTask fetchStoriesTask = new FetchStoriesTask(MainActivity.this);
                        fetchStoriesTask.execute(editTextQuery.getText().toString());
                    } else
                        displayNetworkError();

                }
                return false;
            }
        });
    }

    @Override
    public void fetchItemsDone(List<Story> stories) {
        clearListItems();
        if (stories == null)
            displayNetworkError();
        else {
            textViewInfoMessage.setVisibility(View.GONE);
            List<Story> list = storyRecyclerViewAdapter.getmValues();
            list.addAll(stories);
        }
    }

    @Override
    public void noResultsFound() {
        clearListItems();
        textViewInfoMessage.setVisibility(View.VISIBLE);
        textViewInfoMessage.setText(getString(R.string.no_results_from_query_message));
    }

    @Override
    public void networkError() {
        displayNetworkError();
    }

    private void displayNetworkError() {
        clearListItems();
        textViewInfoMessage.setVisibility(View.VISIBLE);
        textViewInfoMessage.setText(getString(R.string.no_network_error_message));
    }

    private void clearListItems() {
        List<Story> list = storyRecyclerViewAdapter.getmValues();
        list.clear();
        storyRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(String webUrl) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)));
    }
}
