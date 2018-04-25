package com.example.lizi.place_search;

import java.util.Date;

public class ReviewItem {
    private String authorName;
    private float rating;
    private Date mDate;
    private String text;
    private String url;

    public ReviewItem(String authorName, float rating, Date date, String text, String url) {
        this.authorName = authorName;
        this.rating = rating;
        mDate = date;
        this.text = text;
        this.url = url;
    }

    public String getAuthorName() {
        return authorName;
    }

    public float getRating() {
        return rating;
    }

    public Date getDate() {
        return mDate;
    }

    public String getText() {
        return text;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        String str = "anthor name: " + authorName + "\n";
        str += "rating: " + rating + "\n";
        str += "Date: " + mDate.toString() + "\n";
        str += "url: " + url + "\n";
        str += "text: " + text + "\n";
        return str;
    }
}
