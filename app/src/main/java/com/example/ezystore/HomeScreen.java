package com.example.ezystore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridView;
import androidx.appcompat.widget.SearchView;


import java.util.ArrayList;

public class HomeScreen extends AppCompatActivity {
    private SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        GridView gridView;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        SearchView searchView = findViewById(R.id.searchView);
        gridView=findViewById(R.id.gridView);


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