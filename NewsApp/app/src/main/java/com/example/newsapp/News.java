package com.example.newsapp;

import android.graphics.Bitmap;

public class News {
    private String mTitle;
    private String mSectionName;
    private String mThumbnailUrl;
    private String mTime;
    private String bodyText;

    private Bitmap mBitmap;

    public News(String title, String sectionName, String time, String thumbnailUrl,String id) {
        mTitle = title;
        mSectionName = sectionName;
        mThumbnailUrl = thumbnailUrl;
        mTime = time;
        bodyText = id;
    }


    public String getmTitle() {
        return mTitle;
    }

    public String getmSectionName() {
        return mSectionName;
    }

    public String getmThumbnailUrl() {
        return mThumbnailUrl;
    }


    public String getmTime() {
        return mTime;
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public String getBodyText() {
        return bodyText;
    }


}
