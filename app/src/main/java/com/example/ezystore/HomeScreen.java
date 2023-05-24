package com.example.ezystore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import android.os.Bundle;

public class HomeScreen extends AppCompatActivity {
    private SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        SearchView searchView = findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Arama butonuna tıklandığında gerçekleşecek eylemler
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Metin değiştiğinde gerçekleşecek eylemler
                return false;
            }

        });

    }
}