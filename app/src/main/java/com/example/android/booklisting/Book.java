package com.example.android.booklisting;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {
    /** Attributes */
    private String mTitle;
    private String mAuthor;
    private String mYear;
    private String mCategory;
    private String mPages;
    private String mDescription;
    private String mThumbnail;
    private String mInfoLink;

    /** Constructors */
    public Book() {}

    public Book(
            String mTitle,
            String mAuthor,
            String mYear,
            String mCaterory,
            String mPages,
            String mDescription,
            String mThumbnail,
            String mInfoLink) {
        this.mTitle = mTitle;
        this.mAuthor = mAuthor;
        this.mYear = mYear;
        this.mCategory = mCaterory;
        this.mPages = mPages;
        this.mDescription = mDescription;
        this.mThumbnail = mThumbnail;
        this.mInfoLink = mInfoLink;
    }

    protected Book(Parcel in) {
        mTitle = in.readString();
        mAuthor = in.readString();
        mYear = in.readString();
        mCategory = in.readString();
        mPages = in.readString();
        mDescription = in.readString();
        mThumbnail = in.readString();
        mInfoLink = in.readString();
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

    public String getmInfoLink() {
        return mInfoLink;
    }

    /** Method */
    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mAuthor);
        dest.writeString(mYear);
        dest.writeString(mCategory);
        dest.writeString(mPages);
        dest.writeString(mDescription);
        dest.writeString(mThumbnail);
        dest.writeString(mInfoLink);
    }
}
