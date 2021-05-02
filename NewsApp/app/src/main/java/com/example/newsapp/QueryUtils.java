package com.example.newsapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private QueryUtils() {

    }
    public static List<News> fetchNews(String requesturl){
        URL url = createUrl(requesturl);


        String jsonResponse = null;
        try{
            jsonResponse = makeHttpRequest(url);
        }
        catch (IOException e){
            Log.e(LOG_TAG,"Error getting Input");
        }
        return extractFeaturesFromJson(jsonResponse);
    }

    public static String fetchBodyText(String requesturl){
        URL url = createUrl(requesturl);


        String jsonResponse = null;
        try{
            jsonResponse = makeHttpRequest(url);
        }
        catch (IOException e){
            Log.e(LOG_TAG,"Error getting Input");
        }
        return extractBodyTextFromJson(jsonResponse);
    }

    private static String extractBodyTextFromJson(String jsonResponse) {
        String bodyText = "";
        if (TextUtils.isEmpty(jsonResponse)){
            return null;
        }
        try {
            JSONObject jsonRootObject = new JSONObject(jsonResponse);
            JSONObject responseObject = jsonRootObject.getJSONObject("response");
            JSONArray newsArray = responseObject.getJSONArray("results");
            JSONObject newsObject = newsArray.getJSONObject(0);
            JSONObject fieldsObject = newsObject.getJSONObject("fields");
            bodyText = fieldsObject.getString("bodyText");

            return bodyText;


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bodyText;

    }


    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null){
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromInput(inputStream);
            }
            else{
                Log.e(LOG_TAG,"Response code is "+urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (inputStream != null){
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromInput(InputStream inputStream) throws IOException {
        StringBuilder builder = new StringBuilder();
        if (inputStream!= null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null){
                builder.append(line);
                line = bufferedReader.readLine();
            }
        }
        return builder.toString();
    }

    private static URL createUrl(String requesturl) {
        URL url = null;
        try {
            url = new URL(requesturl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    private static List<News> extractFeaturesFromJson(String jsonResponse) {
        List<News> newsList = new ArrayList<>();
        if (TextUtils.isEmpty(jsonResponse)){
            return null;
        }
        try {
            JSONObject jsonRootObject = new JSONObject(jsonResponse);
            JSONObject responseObject = jsonRootObject.getJSONObject("response");
            JSONArray newsArray = responseObject.getJSONArray("results");
            for (int i=0;i<newsArray.length();i++){
                JSONObject currentNewsObject = newsArray.getJSONObject(i);
                JSONObject fieldsObject = currentNewsObject.getJSONObject("fields");
                String title = currentNewsObject.getString("webTitle");
                String sectionName = currentNewsObject.getString("sectionName");
//                String newsId = currentNewsObject.getString("id");
                String time = currentNewsObject.getString("webPublicationDate");
                String thumbnailUrl = fieldsObject.getString("thumbnail");
                String bodyText = fieldsObject.getString("bodyText");
                newsList.add(new News(title,sectionName,time,thumbnailUrl,bodyText));
            }
            Log.e(LOG_TAG,"Hello......");
            return newsList;


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newsList;
    }


}
