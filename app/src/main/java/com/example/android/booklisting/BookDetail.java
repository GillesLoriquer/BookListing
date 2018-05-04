package com.example.android.booklisting;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class BookDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        // Show the back button on the action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Get the book clicked
        final Book book = getIntent().getExtras().getParcelable("book");

        // Get all views to populate
        ImageView thumbnail = findViewById(R.id.id_detail_book_thumbnail);
        TextView title = findViewById(R.id.id_detail_book_title);
        TextView author = findViewById(R.id.id_detail_book_author);
        TextView year = findViewById(R.id.id_detail_book_year);
        TextView category = findViewById(R.id.id_detail_book_category);
        TextView pages = findViewById(R.id.id_detail_book_pages);
        TextView description = findViewById(R.id.id_detail_book_description);
        Button button = findViewById(R.id.id_detail_book_button);

        // Set the title on the action bar to the book title
        BookDetail.this.setTitle(book.getmTitle());

        // Setting right values for the views
        Picasso.get().load(book.getmThumbnail())
                .placeholder(R.drawable.no_image).into(thumbnail);
        title.setText(book.getmTitle());
        author.setText(book.getmAuthor());
        year.setText(Utils.getYear(book.getmYear()));
        category.setText(book.getmCaterory());
        pages.setText(book.getmPages());
        description.setText(book.getmDescription());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Define a new intent to browse the url of the specific item
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(book.getmInfoLink()));
                // Start the intent
                startActivity(intent);
            }
        });
    }
}
