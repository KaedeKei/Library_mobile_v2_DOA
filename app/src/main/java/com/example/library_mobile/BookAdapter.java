package com.example.library_mobile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private ArrayList<Book> bookList;
    private Context context;

    public BookAdapter(Context context, ArrayList<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookAdapter.BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.bookTitle.setText(book.getTitle());
        holder.bookAuthor.setText(book.getAuthor());
        holder.bookDescription.setText(book.getDescription());

        if(book.getCover() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(book.getCover(), 0, book.getCover().length);
            holder.bookCover.setImageBitmap(bitmap);
        }
        else {
            holder.bookCover.setImageResource(R.drawable.logo);
        }

        holder.editBookButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditBookActivity.class);
            intent.putExtra("book_id", book.getId());
            context.startActivity(intent);
        });

        holder.deleteBookButton.setOnClickListener(v -> {
            BookDatabaseHelper dbHelper = new BookDatabaseHelper(context);
            dbHelper.getWritableDatabase().delete("books", "id=?", new String[] {String.valueOf(book.getId())});

            bookList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, bookList.size());
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView bookTitle, bookAuthor, bookDescription;
        ImageView bookCover;
        Button editBookButton, deleteBookButton;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            bookTitle = itemView.findViewById(R.id.book_title);
            bookAuthor = itemView.findViewById(R.id.book_author);
            bookDescription = itemView.findViewById(R.id.book_description);
            bookCover = itemView.findViewById(R.id.book_cover);
            editBookButton = itemView.findViewById(R.id.edit_book_button);
            deleteBookButton = itemView.findViewById(R.id.delete_book_button);
        }
    }
}
