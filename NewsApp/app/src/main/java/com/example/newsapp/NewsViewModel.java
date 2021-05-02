package com.example.newsapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class NewsViewModel  extends ViewModel {
    private static final String LOG_TAG = NewsViewModel.class.getSimpleName();
    public static int downloadedImages = 0;
    public static DownloadImageTask passableImageTask = new DownloadImageTask();
    public static DownloadImageTask passableSearchImageTask = new DownloadImageTask();

    private static final String API_KEY = "0f4e1a53-14b9-4a72-9892-bd0dc1fea7a1";
    private static final String API_URL = "https://content.guardianapis.com/search?";
    static boolean isSearch = false;


    public static MutableLiveData<List<Integer>> getFilters() {
        return filters;
    }

    public static MutableLiveData<List<Integer>> filters = new MutableLiveData<>();
    public static MutableLiveData<List<Bitmap>> images = new MutableLiveData<>();
    public static MutableLiveData<List<Bitmap>> searchImages = new MutableLiveData<>();
    public MutableLiveData<List<Bitmap>> getBitmaps(String from) {
        if (images == null){
            images = new MutableLiveData<>();
        }
        isSearch = false;
        if (from.equals("main")){
            getImages(MainActivity.news);
        }
        return images;
    }
    public MutableLiveData<List<Bitmap>> getSearchBitmaps() {
        if (images == null){
            images = new MutableLiveData<>();
        }
        isSearch = true;
        getSearchImages(SearchActivity.searchResult);
        return searchImages;
    }
    private void getImages(List<News> news) {
        Log.e(LOG_TAG,"Downloaded images before "+downloadedImages );
        if (images.getValue()!=null&&images.getValue().size()>0){
            downloadedImages = images.getValue().size();
        }
        Log.e(LOG_TAG,"Downloaded images after "+downloadedImages );
        int size = news.size();
        Log.e(LOG_TAG,"News size " + size);
        List<String> urls = new ArrayList<>();
        for(int j=0, i=downloadedImages;i<size;i++,j++){
            urls.add(news.get(i).getmThumbnailUrl());
        }
//        Log.e(LOG_TAG, "images size " + images.getValue().size());
        DownloadImageTask downloadImageTask = new DownloadImageTask();
        downloadImageTask.execute(urls);
        passableImageTask = downloadImageTask;

    }

    private void getSearchImages(List<News> news){
        int size = news.size();
        List<String> urls = new ArrayList<>();
        for(int i=0;i<size;i++){
            urls.add(news.get(i).getmThumbnailUrl());
        }
        DownloadImageTask downloadImageTask = new DownloadImageTask();
        downloadImageTask.execute(urls);
        passableSearchImageTask = downloadImageTask;

    }



    private static void addToList(List<Bitmap> bitmaps) {
        List<Bitmap> imagesList = new ArrayList<>();
        if (!isSearch){
            if (images.getValue()!=null){
                imagesList = images.getValue();
                Log.e(LOG_TAG,"Before adding images " + imagesList.size());
            }
            imagesList.addAll(bitmaps);
            images.setValue(imagesList);
            Log.e(LOG_TAG,"Images size after download " +images.getValue().size());
        }
        else {
            searchImages.setValue(bitmaps);
        }
    }

    static class DownloadImageTask extends AsyncTask<List<String>, Void, List<Bitmap>> {

        @Override
        protected List<Bitmap> doInBackground(List<String>... lists) {
            List<Bitmap> bitmaps = new ArrayList<>();
            List<String> urls = lists[0];
            for (String url : urls) {
                Bitmap bmp = null;
                try {
                    InputStream in = new java.net.URL(url).openStream();
                    bmp = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", Objects.requireNonNull(e.getMessage()));
                    e.printStackTrace();
                }
                bitmaps.add(bmp);
            }
            return bitmaps;
        }

        @Override
        protected void onPostExecute(List<Bitmap> bitmaps) {
            addToList(bitmaps);
           // images.setValue(bitmaps);

        }

        public void cancelImageTask(){
            this.cancel(true);
        }
    }







}
