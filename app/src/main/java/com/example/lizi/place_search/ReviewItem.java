package com.example.lizi.place_search;

import java.util.Comparator;
import java.util.Date;

public class ReviewItem {
    private String authorName;
    private float rating;
    private Date mDate;
    private String text;
    private String url;
    private String authorImageUrl;

    public ReviewItem(String authorName, float rating, Date date, String text, String url, String imageUrl) {
        this.authorName = authorName;
        this.rating = rating;
        mDate = date;
        this.text = text;
        this.url = url;
        authorImageUrl = imageUrl;
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

    public String getAuthorImageUrl() {
        return authorImageUrl;
    }

    @Override
    public String toString() {
        String str = "anthor name: " + authorName + "\n";
        str += "rating: " + rating + "\n";
        str += "Date: " + mDate.toString() + "\n";
        str += "url: " + url + "\n";
        str += "text: " + text + "\n";
        str += "author image url: " + authorImageUrl;
        return str;
    }

    public static Comparator<ReviewItem> ReviewRatingComparator = new Comparator<ReviewItem>() {
        @Override
        public int compare(ReviewItem o1, ReviewItem o2) {
            Float rating1 = o1.getRating();
            Float rating2 = o2.getRating();
            return rating1.compareTo(rating2);
        }
    };

    public static Comparator<ReviewItem> ReviewDateComparator = new Comparator<ReviewItem>() {
        @Override
        public int compare(ReviewItem o1, ReviewItem o2) {
            return o1.mDate.compareTo(o2.mDate);
        }
    };


}
