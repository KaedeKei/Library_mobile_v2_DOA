package com.example.library_mobile;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddBookActivity extends AppCompatActivity {

    private EditText titleAddField, authorAddField, descriptionAddField;
    private ImageView coverImageView;
    private Button loadCoverBtn, saveBookBtn;
    private byte[] coverImage;

    private BookDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        titleAddField = findViewById(R.id.title_add_field);
        authorAddField = findViewById(R.id.author_add_field);
        descriptionAddField = findViewById(R.id.description_add_field);
        coverImageView = findViewById(R.id.cover_image_view);
        loadCoverBtn = findViewById(R.id.cover_load_btn);
        saveBookBtn = findViewById(R.id.save_book_btn);
        Bitmap emptybitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        coverImageView.setImageBitmap(emptybitmap);
        coverImage = getBytesFromBitmap(emptybitmap);

        dbHelper = new BookDatabaseHelper(this);

        loadCoverBtn.setOnClickListener(v -> openImageChooser());
        saveBookBtn.setOnClickListener(v -> saveBookToDb());
    }

    //получение результата из активности(выбор изображения из галереи, установка в поле изображения, передача байтового массива для декодирования)
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

    private void saveBookToDb() {
        String title = titleAddField.getText().toString();
        String author = authorAddField.getText().toString();
        String description = descriptionAddField.getText().toString();

        dbHelper.getWritableDatabase().execSQL("insert into books(title, author, description, cover) values(?,?,?,?)", new Object[]{title, author, description, coverImage});

        setResult(RESULT_OK);
        finish();
    }
}