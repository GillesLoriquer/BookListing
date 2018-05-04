package com.example.android.booklisting;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class BookCardViewAdapter extends RecyclerView.Adapter<BookCardViewAdapter.RowViewHolder> {
    /** Attributes */
    private Context mContext;
    private List<Book> mBooks;
    private OnItemClickListener mListener;

    /** Constructor */
    public BookCardViewAdapter() {}

    public BookCardViewAdapter(Context mContext, List<Book> mBooks, OnItemClickListener mListener) {
        this.mContext = mContext;
        this.mBooks = mBooks;
        this.mListener = mListener;
    }

    /** Inner class */
    public class RowViewHolder extends RecyclerView.ViewHolder {
        private ImageView mThumbnail;
        private TextView mTitle;
        private TextView mAuthor;
        private TextView mYear;
        private TextView mCategory;

        public RowViewHolder(View itemView) {
            super(itemView);
            mThumbnail = itemView.findViewById(R.id.id_book_thumbnail);
            mTitle = itemView.findViewById(R.id.id_book_title);
            mAuthor = itemView.findViewById(R.id.id_book_author);
            mYear = itemView.findViewById(R.id.id_book_year);
            mCategory = itemView.findViewById(R.id.id_book_category);
        }
    }

    @NonNull
    @Override
    public RowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the book_cardview custom layout
        View bookView = LayoutInflater.from(mContext).inflate(
                R.layout.book_cardview,
                parent,
                false);

        // Create a new view holder
        final RowViewHolder bookViewHolder = new RowViewHolder(bookView);
        // Attach a listener on the bookView view
        bookView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(v, bookViewHolder.getAdapterPosition());
            }
        });

        // Create and return the new view holder
        return bookViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RowViewHolder holder, int position) {
        // Get the current book
        Book currentBook = mBooks.get(position);

        // These lines set the title, author and category for the current book
        Picasso.get().load(currentBook.getmThumbnail())
                .placeholder(R.drawable.no_image).into(holder.mThumbnail);
        holder.mTitle.setText(Utils.shrinkString(currentBook.getmTitle(), 28));
        holder.mAuthor.setText(Utils.shrinkString(currentBook.getmAuthor(), 30));
        holder.mYear.setText(Utils.getYear(currentBook.getmYear()));
        holder.mCategory.setText(Utils.shrinkString(currentBook.getmCaterory(), 30));
    }

    @Override
    public int getItemCount() {
        return mBooks.size();
    }

    public void clear() {
        mBooks.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Book> listBook) {
        mBooks.addAll(listBook);
        notifyDataSetChanged();
    }

}
