package com.example.newsapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private static final String API_KEY = "0f4e1a53-14b9-4a72-9892-bd0dc1fea7a1";
    private static final String API_URL = "https://content.guardianapis.com/search?";
    ProgressBar progressBar;
    ListView searchListView;
    SearchAdapter adapter;
    public static List<News> searchResult = new ArrayList<>();
    NewsViewModel viewModel;
    ImageView voiceImage;
    SearchView searchView;
    List<Bitmap> emptyList = new ArrayList<>();
    GetSearchResults toCancelSearch = new GetSearchResults();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = findViewById(R.id.search_view);
        searchListView = findViewById(R.id.search_result_list);
        progressBar = findViewById(R.id.search_progress_bar);
        adapter = new SearchAdapter(getApplicationContext(), searchResult);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getQueryResult(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        viewModel = ViewModelProviders.of(this).get(NewsViewModel.class);

        searchListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getApplicationContext(), NewsDisplayActivity.class);
            ImageView imageView = view.findViewById(R.id.thumbnail_image_view);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(SearchActivity.this, imageView, "list_transition");
            intent.putExtra("position", position);
            intent.putExtra("type", "search");
            startActivity(intent, options.toBundle());
        });
        setVoiceButton();
        setChips();

    }

    private void setVoiceButton() {
        voiceImage = findViewById(R.id.voice_image);
        voiceImage.setOnClickListener(v -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            startActivityForResult(intent, 200);
        });
    }

    private void setChips() {
        Chip footballChip = findViewById(R.id.chip_football);
        Chip cricketChip = findViewById(R.id.chip_cricket);
        Chip covidChip = findViewById(R.id.chip_covid);
        Chip stockChip = findViewById(R.id.chip_stock);
        Chip bitcoinChip = findViewById(R.id.chip_bitcoin);

        footballChip.setOnClickListener(v -> {
            String tag = "football/football";
            getTagResult(tag);
        });
        cricketChip.setOnClickListener(v -> {
            String tag = "sport/cricket";
            getTagResult(tag);
        });
        covidChip.setOnClickListener(v -> {
            String tag = "world/series/covid-19-investigations";
            getTagResult(tag);
        });
        stockChip.setOnClickListener(v -> {
            String tag = "business/stock-markets";
            getTagResult(tag);
        });
        bitcoinChip.setOnClickListener(v -> {
            String tag = "technology/bitcoin";
            getTagResult(tag);
        });
    }

    private void getTagResult(String tag) {
        NewsViewModel.passableSearchImageTask.cancel(true);
        toCancelSearch.cancel(true);
        adapter.clear();
        emptyList.clear();
        NewsViewModel.searchImages.setValue(emptyList);
        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.VISIBLE);

        Uri baseUri = Uri.parse(API_URL);
        Uri.Builder builder = baseUri.buildUpon();
        builder.appendQueryParameter("tag", tag);
        builder.appendQueryParameter("page-size", "20");
        builder.appendQueryParameter("show-fields", "thumbnail,bodyText");
        builder.appendQueryParameter("api-key", API_KEY);
        GetSearchResults getSearchResults = new GetSearchResults();
        getSearchResults.execute(builder.toString());
        toCancelSearch = getSearchResults;

    }

    private void getQueryResult(String queryText) {
        NewsViewModel.passableSearchImageTask.cancel(true);
        toCancelSearch.cancel(true);
        adapter.clear();
        emptyList.clear();
        NewsViewModel.searchImages.setValue(emptyList);
        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.VISIBLE);


        Uri baseUri = Uri.parse(API_URL);
        Uri.Builder builder = baseUri.buildUpon();
        builder.appendQueryParameter("q", queryText);
        builder.appendQueryParameter("page-size", "20");
        builder.appendQueryParameter("show-fields", "thumbnail,bodyText");
        builder.appendQueryParameter("api-key", API_KEY);
        GetSearchResults getSearchResults = new GetSearchResults();
        getSearchResults.execute(builder.toString());
        toCancelSearch = getSearchResults;

    }

    class GetSearchResults extends AsyncTask<String, Void, List<News>> {

        @Override
        protected List<News> doInBackground(String... strings) {
            String url = strings[0];
            searchResult = QueryUtils.fetchNews(url);
            return searchResult;
        }

        @Override
        protected void onPostExecute(List<News> news) {
            progressBar.setVisibility(View.GONE);
            TextView noSearchResults = findViewById(R.id.no_search_results);
            if (news.size() == 0) {
                noSearchResults.setVisibility(View.VISIBLE);
            } else {
                noSearchResults.setVisibility(View.INVISIBLE);
            }
            adapter = new SearchAdapter(getApplicationContext(), searchResult);
            searchListView.setAdapter(adapter);
            viewModel.getSearchBitmaps().observe(SearchActivity.this, new Observer<List<Bitmap>>() {
                @Override
                public void onChanged(List<Bitmap> bitmaps) {
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK) {
            ArrayList<String> resultList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String voice = resultList.get(0);
            searchView.setQuery(voice, true);

        }
    }
}