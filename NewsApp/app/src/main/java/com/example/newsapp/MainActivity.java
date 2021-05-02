package com.example.newsapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String API_KEY = "0f4e1a53-14b9-4a72-9892-bd0dc1fea7a1";
    private static final String API_URL = "https://content.guardianapis.com/search?";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    TextView noInternetTextView;
    NewsAdapter adapter;
    public static List<News> news = new ArrayList<>();
    NewsViewModel viewModel;
    ListView newsListView;
    LinearLayout mainShimmerLayout;
    int pageNumber = 1;
    boolean isNewsLoaded = false;
    boolean toLastLoad = true;
    boolean isFilterOpen = false;
    FrameLayout filterContainer;
    public static boolean isFilter = false;
    View footer;
    StringBuilder sections;
    String[] filterSections;
    List<Bitmap> emptyList = new ArrayList<>();
    FetchingNews toCancelNews = new FetchingNews();


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noInternetTextView = findViewById(R.id.no_internet_text_view);
        mainShimmerLayout = findViewById(R.id.main_shimmer_layout);
        filterContainer = findViewById(R.id.filter_container);
        getSupportFragmentManager().beginTransaction().replace(R.id.filter_container, new FilterFragment()).commit();

        Resources res = getResources();
        filterSections = res.getStringArray(R.array.filters);

        viewModel = ViewModelProviders.of(this).get(NewsViewModel.class);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activityNetwork = cm.getActiveNetworkInfo();
        if (activityNetwork != null && activityNetwork.isConnected()) {
            extractNews();

        } else {
            mainShimmerLayout.setVisibility(View.GONE);
            noInternetTextView.setText(R.string.no_internet);
        }
        newsListView = findViewById(R.id.main_news_list);
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), NewsDisplayActivity.class);
                ImageView imageView = view.findViewById(R.id.thumbnail_image_view);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, imageView, "list_transition");
                intent.putExtra("position", position);
                intent.putExtra("type", "main");
                startActivity(intent, options.toBundle());

            }
        });

        viewModel = ViewModelProviders.of(this).get(NewsViewModel.class);


        newsListView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (!newsListView.canScrollVertically(1) && isNewsLoaded && toLastLoad) {
                    if (isFilter) {
                        extractFilterNews(sections.toString().substring(0, sections.length() - 1));
                    } else {
                        extractNews();
                    }
                    toLastLoad = false;

                }
            }
        });

        footer = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.progress_bar_footer, null, false);


        NewsViewModel.getFilters().observe(this, new Observer<List<Integer>>() {
            @Override
            public void onChanged(List<Integer> filterIds) {
                if (filterIds != null) {
                    setFilterNews(filterIds);

                }

            }
        });


    }

    private void setFilterNews(List<Integer> filterIds) {
        setFilterFragment();
        NewsViewModel.passableImageTask.cancel(true);
        toCancelNews.cancel(true);
        news.clear();
        Log.e(LOG_TAG,"News position " + news);
        NewsViewModel.downloadedImages = 0;
        emptyList.clear();
        NewsViewModel.images.setValue(emptyList);
        if (adapter != null){
            adapter.clear();
            adapter.notifyDataSetChanged();
        }

        pageNumber = 1;
        isNewsLoaded = false;
        isFilter = true;


        sections = new StringBuilder();
        Log.e(LOG_TAG, "filters size " + filterIds.size());
        Log.e(LOG_TAG, "String builder before " + sections.toString());
        for (int i = 0; i < filterIds.size(); i++) {
            int index = (int) filterIds.get(i);
            Log.e(LOG_TAG, String.valueOf(index));
            sections.append(filterSections[index]).append("|");
        }
        if (filterIds.size() != 0){
            extractFilterNews(sections.toString().substring(0, sections.length() - 1));
        }
        else {
            isFilter = false;
            extractNews();
        }
    }

    private void extractFilterNews(String sections) {

        Uri baseUri = Uri.parse(API_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("format", "json");
        uriBuilder.appendQueryParameter("section", sections);
        uriBuilder.appendQueryParameter("page-size", "50");
        uriBuilder.appendQueryParameter("page", String.valueOf(pageNumber));
        uriBuilder.appendQueryParameter("show-fields", "thumbnail,bodyText");
        uriBuilder.appendQueryParameter("api-key", API_KEY);

        FetchingNews fetchingFilterNews = new FetchingNews();
        fetchingFilterNews.execute(uriBuilder.toString());
        Log.e(LOG_TAG,"filter url" + uriBuilder.toString());


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_search) {
            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
            ActionMenuItemView searchView = findViewById(R.id.menu_search);
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, searchView, "search_transition");
            startActivity(intent, optionsCompat.toBundle());
        }
        if (id == R.id.menu_filter) {
            setFilterFragment();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setFilterFragment() {
        if (isFilterOpen) {
            filterContainer.setVisibility(View.GONE);
            isFilterOpen = false;
        } else {
            filterContainer.setVisibility(View.VISIBLE);
            isFilterOpen = true;
        }

    }

    private void extractNews() {
        Uri baseUri = Uri.parse(API_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("format", "json");
        uriBuilder.appendQueryParameter("page-size", "50");
        uriBuilder.appendQueryParameter("page", String.valueOf(pageNumber));
        uriBuilder.appendQueryParameter("show-fields", "thumbnail,bodyText");
        uriBuilder.appendQueryParameter("api-key", API_KEY);

        FetchingNews fetchingNews = new FetchingNews();
        fetchingNews.execute(uriBuilder.toString());
        toCancelNews = fetchingNews;

    }

    @SuppressLint("StaticFieldLeak")
    public class FetchingNews extends AsyncTask<String, Void, List<News>> {

        @Override
        protected List<News> doInBackground(String... strings) {
            String url = strings[0];
//            news = QueryUtils.fetchNews(url);
            if (news ==null){
                news = new ArrayList<>();
            }
            Log.e(LOG_TAG,"news in background "+news);

            news.addAll(QueryUtils.fetchNews(url));
            return news;
        }

        @Override
        protected void onPostExecute(List<News> result) {
            mainShimmerLayout.setVisibility(View.GONE);
            newsListView.removeFooterView(footer);
            pageNumber++;
            toLastLoad = true;
            if (!isNewsLoaded) {
                adapter = new NewsAdapter(getApplicationContext(), news);
                newsListView.setAdapter(adapter);
                newsListView.addFooterView(footer);
            } else {
                newsListView.addFooterView(footer);
                adapter.notifyDataSetChanged();

            }
            isNewsLoaded = true;
            viewModel.getBitmaps("main").observe(MainActivity.this, new Observer<List<Bitmap>>() {
                @Override
                public void onChanged(List<Bitmap> bitmaps) {

                    adapter.notifyDataSetChanged();
                    Log.e(LOG_TAG, "In images changed adapter");

                }
            });



        }
    }


}