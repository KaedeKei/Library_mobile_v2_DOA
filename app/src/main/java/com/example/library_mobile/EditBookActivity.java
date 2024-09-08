package com.example.library_mobile;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditBookActivity extends AppCompatActivity {

    EditText titleField, authorField, descriptionField;
    ImageView coverImageView;
    Button loadCoverBtn, saveBookBtn;
    byte[] coverImage;

    int bookId = 0;
    BookAdapter bookAdapter;

    private BookDatabaseHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        titleField = findViewById(R.id.title_field);
        authorField = findViewById(R.id.author_field);
        descriptionField = findViewById(R.id.description_field);
        coverImageView = findViewById(R.id.cover_image_view);
        loadCoverBtn = findViewById(R.id.cover_load_btn);
        saveBookBtn = findViewById(R.id.save_book_btn);
        dbHelper = new BookDatabaseHelper(this);

        loadCoverBtn.setOnClickListener(v -> openImageChooser());

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            bookId = extras.getInt("book_id");
        }

        if(bookId > 0) {
            db = dbHelper.getWritableDatabase();
            Book book = null;
            String query = String.format("select * from %s where %s=?", "books", "id");
            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(bookId)});
            if(cursor.moveToFirst()) {
                String title = cursor.getString(1);
                String author = cursor.getString(2);
                String description = cursor.getString(3);
                byte[] cover = cursor.getBlob(4);
                book = new Book(bookId, title, author, description, cover);
            }
            cursor.close();

            titleField.setText(book.getTitle());
            authorField.setText(book.getAuthor());
            descriptionField.setText(book.getDescription());
            byte[] bitmapdata = book.getCover();
            Bitmap cover = BitmapFactory.decodeByteArray(book.getCover(), 0, bitmapdata.length);
            coverImageView.setImageBitmap(cover);
            coverImage = bitmapdata;
            dbHelper.close();
        } else {
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void save(View view) {
        String title = titleField.getText().toString();
        String author = authorField.getText().toString();
        String description = descriptionField.getText().toString();
        Book book = new Book(bookId, title, author, description, coverImage);

        db = dbHelper.getWritableDatabase();
        if(bookId > 0) {
            String whereClause = "id" + "=" + book.getId();
            ContentValues cv = new ContentValues();
            cv.put("title", book.getTitle());
            cv.put("author", book.getAuthor());
            cv.put("description", book.getDescription());
            cv.put("cover", book.getCover());

            db.update("books", cv, whereClause, null);

            bookAdapter.notifyDataSetChanged();
            setResult(RESULT_OK);
            finish();
        }
        else{
            finish();
        }
    }

    private final ActivityResultLauncher<Intent> selectImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getResultCode() == RESULT_OK && result.getData() != null) {
            Uri selectedImageUri = result.getData().getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                coverImageView.setImageBitmap(bitmap);
                coverImage = getBytesFromBitmap(bitmap);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    });

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        selectImageLauncher.launch(intent);
    }

    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
        return stream.toByteArray();
    }
}