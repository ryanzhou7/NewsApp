package com.ryanzhou.company.newsapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ryanzhou.company.newsapp.model.Story;

import java.util.List;

/**
 * Created by ryanzhou on 6/24/16.
 */
public class StoryRecyclerViewAdapter extends RecyclerView.Adapter<StoryRecyclerViewAdapter.ViewHolder> {

    private OnRecyclerViewInteraction listener;
    public final String LOG_TAG = getClass().getSimpleName();
    private List<Story> mValues;

    public StoryRecyclerViewAdapter(List<Story> items, Context context) {
        mValues = items;
        if (context instanceof OnRecyclerViewInteraction) {
            listener = (OnRecyclerViewInteraction) context;
        } else
            Log.e(LOG_TAG, context.toString() + " did not implement OnRecyclerViewInteraction interface");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_story, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = getmValues().get(position);
        holder.mTextViewSectionName.setText(holder.mItem.getSectionName());
        holder.mTextViewTitle.setText(holder.mItem.getWebTitle());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(holder.mItem.getWebUrl());
            }
        });
    }

    @Override
    public int getItemCount() {
        return getmValues().size();
    }

    public List<Story> getmValues() {
        return mValues;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTextViewTitle;
        public final TextView mTextViewSectionName;
        public Story mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTextViewSectionName = (TextView) view.findViewById(R.id.textViewSectionName);
            mTextViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
        }
    }

    public interface OnRecyclerViewInteraction {
        public void onItemClick(String webUrl);
    }

}