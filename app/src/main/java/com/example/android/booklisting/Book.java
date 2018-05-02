package com.example.android.booklisting;

public class Book {
    /** Attributes */
    private String mTitle;
    private String mAuthor;
    private String mYear;
    private String mCategory;
    private String mPages;
    private String mDescription;
    private String mThumbnail;

    /** Constructors */
    public Book() {}

    public Book(
            String mTitle,
            String mAuthor,
            String mYear,
            String mCaterory,
            String mPages,
            String mDescription,
            String mThumbnail) {
        this.mTitle = mTitle;
        this.mAuthor = mAuthor;
        this.mYear = mYear;
        this.mCategory = mCaterory;
        this.mPages = mPages;
        this.mDescription = mDescription;
        this.mThumbnail = mThumbnail;
    }

    /** Getters */
    public String getmTitle() {
        return mTitle;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public String getmYear() {
        return mYear;
    }

    public String getmCaterory() {
        return mCategory;
    }

    public String getmPages() {
        return mPages;
    }

    public String getmDescription() {
        return mDescription;
    }

    public String getmThumbnail() {
        return mThumbnail;
    }
}
