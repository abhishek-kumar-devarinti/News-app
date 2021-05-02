package com.example.newsapp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.List;

public class NewsDisplayActivity extends AppCompatActivity {
    TextView bodyTextView;
    private static final String API_KEY = "0f4e1a53-14b9-4a72-9892-bd0dc1fea7a1";
    private static final String API_URL = "https://content.guardianapis.com/search?";
    List<News> displaynews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_display);

        NewsViewModel viewModel = ViewModelProviders.of(this).get(NewsViewModel.class);
        ImageView displayImage = findViewById(R.id.display_image);
        TextView displayTitle = findViewById(R.id.display_title);
        bodyTextView = findViewById(R.id.display_body);

        Bundle b = getIntent().getExtras();
        assert b != null;
        int position = b.getInt("position");
        String type = b.getString("type");




        if (type.equals("main")){
            displaynews = MainActivity.news;
            viewModel.getBitmaps("display").observe(this, new Observer<List<Bitmap>>() {
                @Override
                public void onChanged(List<Bitmap> bitmaps) {
                    if (bitmaps.size()==displaynews.size()){
                        displayImage.setImageBitmap(bitmaps.get(position));
                    }

                }
            });
        }
        else if (type.equals("search")){
            displaynews = SearchActivity.searchResult;
            viewModel.getSearchBitmaps().observe(this, new Observer<List<Bitmap>>() {
                @Override
                public void onChanged(List<Bitmap> bitmaps) {
                    displayImage.setImageBitmap(bitmaps.get(position));
                }
            });
        }

        displayTitle.setText(displaynews.get(position).getmTitle());
        bodyTextView.setText(displaynews.get(position).getBodyText());





        //setBodyText(position);



    }

//    private void setBodyText(int position) {
//        Uri baseUri = Uri.parse(API_URL);
//        Uri.Builder builder = baseUri.buildUpon();
//        builder.appendQueryParameter("ids",MainActivity.news.get(position).getNewsId());
//        builder.appendQueryParameter("show-fields","bodyText");
//        builder.appendQueryParameter("api-key",API_KEY);
//        GetBodyText getBodyText = new GetBodyText();
//        getBodyText.execute(builder.toString());
//    }
//
//
//   class GetBodyText extends AsyncTask<String,Void,String>{
//
//       @Override
//       protected String doInBackground(String... strings) {
//           String url = strings[0];
//           return QueryUtils.fetchBodyText(url);
//       }
//
//       @Override
//       protected void onPostExecute(String s) {
//           ShimmerFrameLayout shimmerFrameLayout = findViewById(R.id.shimmer_layout);
//           shimmerFrameLayout.setVisibility(View.GONE);
//           bodyTextView.setText(s);
//       }
//   }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            super.onBackPressed();
            return true;
        }
        return false;
    }
}