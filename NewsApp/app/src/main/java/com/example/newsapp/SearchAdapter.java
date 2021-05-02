package com.example.newsapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

public class SearchAdapter  extends ArrayAdapter<News> {
    public SearchAdapter(@NonNull Context context, @NonNull List<News> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_list_item, parent, false);
        }
        News currentNews = getItem(position);
        ShimmerFrameLayout frameLayout = listItemView.findViewById(R.id.image_shimmer);
        frameLayout.setVisibility(View.VISIBLE);

        Bitmap currentBitmap = null;
        if (NewsViewModel.searchImages.getValue() != null && NewsViewModel.searchImages.getValue().size() == SearchActivity.searchResult.size()) {
            currentBitmap = NewsViewModel.searchImages.getValue().get(position);
        }


        TextView titleTextView = listItemView.findViewById(R.id.news_title_text);
        TextView sectionNameTextView = listItemView.findViewById(R.id.news_sectionName_text);
        TextView timeView = listItemView.findViewById(R.id.news_time_view);
        ImageView thumbnail = listItemView.findViewById(R.id.thumbnail_image_view);

        titleTextView.setText(currentNews.getmTitle());
        sectionNameTextView.setText(currentNews.getmSectionName());

        String time = currentNews.getmTime().substring(0, 10);
        timeView.setText(time);

        if (currentBitmap != null) {
            frameLayout.setVisibility(View.GONE);
            thumbnail.setImageBitmap(currentBitmap);
        }


        return listItemView;
    }
}
