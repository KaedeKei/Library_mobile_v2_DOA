package com.example.library_mobile;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private ArrayList<Book> bookList;
    private BookDatabaseHelper dbHelper;
    private Button addBookButton;
    private FirebaseAuth auth;

    private final ActivityResultLauncher<Intent> addBookLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getResultCode() == RESULT_OK) {
            loadBookFromDatabase();
            bookAdapter.notifyDataSetChanged();//уведомляем адаптер об изменении данных
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        auth = FirebaseAuth.getInstance();

        //настройка переключателя для открытия/закрытия меню
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.user_name);

        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null) {
            String username = currentUser.getEmail();
            if(username != null) {
                userNameTextView.setText(username);
            } else {
                userNameTextView.setText("Гость");
            }
        } else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }


        Button logoutButton = headerView.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(v -> logout());

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    finish();
                }
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);

        //контент
        recyclerView = findViewById(R.id.book_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new BookDatabaseHelper(this);
        bookList = new ArrayList<>();

        loadBookFromDatabase();

        bookAdapter = new BookAdapter(this, bookList);
        recyclerView.setAdapter(bookAdapter);

        addBookButton = findViewById(R.id.add_book_button);
        addBookButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddBookActivity.class);
            addBookLauncher.launch(intent);
        });
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    private void loadBookFromDatabase() {
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from books", null);
        bookList.clear();
        while(cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                String author = cursor.getString(2);
                String description = cursor.getString(3);
                byte[] cover = cursor.getBlob(4);

                Book book = new Book(id, title, author, description, cover);
                bookList.add(book);
        }
    }
}