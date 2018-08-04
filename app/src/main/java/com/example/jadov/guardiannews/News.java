package com.example.jadov.guardiannews;

public class News {

    // declare variables
    private String mSection;
    private String mDate;
    private String mTitle;
    private String mAuthor;
    private String mUrl;

    // default constructor for creating an news object
    public News(String mSection, String mDate, String mTitle, String mAuthor, String mUrl) {
        this.mSection = mSection;
        this.mDate = mDate;
        this.mTitle = mTitle;
        this.mAuthor = mAuthor;
        this.mUrl = mUrl;
    }

    // retrieves the section of a news article
    public String getSection() {
        return mSection;
    }

    // retrieves the publication date of a news article
    public String getDate() {
        return mDate;
    }

    // retrieves the title of a news article
    public String getTitle() {
        return mTitle;
    }

    // retrieves the author(s) of a news article
    public String getAuthor() {
        return mAuthor;
    }

    // retrieves the URL or a news article
    public String getUrl() {
        return mUrl;
    }
}
