package com.oxfordbrookes.max.studentsatisfactionapp.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.oxfordbrookes.max.studentsatisfactionapp.R;
import com.oxfordbrookes.max.studentsatisfactionapp.utils.TweetSentiment;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TweetSentimentAdapter extends BaseAdapter implements Filterable {
    private Activity activity;
    private List<TweetSentiment> originalTweets;
    private List<TweetSentiment> filteredTweets;
    private static LayoutInflater inflater = null;
    private TweetFilter tweetFilter;

    public TweetSentimentAdapter(Activity activity, @NonNull List<TweetSentiment> tweetSentimentArrayList) {
        this.activity = activity;
        this.tweetFilter = new TweetFilter();
        this.originalTweets = tweetSentimentArrayList;
        this.filteredTweets = tweetSentimentArrayList;
        inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return filteredTweets.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredTweets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"SetTextI18n", "ViewHolder"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if(convertView == null) {
            view = inflater.inflate(R.layout.listview_custom_adapter, null);
        }

        TextView tvTweet = view.findViewById(R.id.textViewListItem1);
        TextView tvSentiment = view.findViewById(R.id.textViewListItem2);

        TweetSentiment tweetSentiment = filteredTweets.get(position);

        tvTweet.setText(tweetSentiment.getTweet());
        tvSentiment.setText(Integer.toString(tweetSentiment.getSentiment()));

        return view;
    }

    @Override
    public Filter getFilter() {
        return tweetFilter;
    }

    private class TweetFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();
            charSequence = charSequence.toString().toLowerCase();

            if(charSequence == null || charSequence.length() == 0)
            {
                results.values = originalTweets;
                results.count = originalTweets.size();
            }
            else
            {
                ArrayList<TweetSentiment> filteredData = new ArrayList<>();

                for(TweetSentiment tweetSentiment : originalTweets)
                {
                    if(tweetSentiment.getTweet().toLowerCase().contains(charSequence))
                    {
                        filteredData.add(tweetSentiment);
                    }
                }

                results.values = filteredData;
                results.count = filteredData.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredTweets = (ArrayList<TweetSentiment>) results.values;
            notifyDataSetChanged();
        }

    }
}


