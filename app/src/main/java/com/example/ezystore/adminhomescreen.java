package com.example.ezystore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class adminhomescreen extends AppCompatActivity {
    private GridView gridView;
    private ArrayList<DataClass> dataList;
    private MyAdapter adapter;
    private adminCategoryAdapter adapter2;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Category> categoryList;
    private FirebaseFirestore firestore;
    private SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminhomescreen);
        searchView = (SearchView) findViewById(R.id.searchView1);

        gridView = findViewById(R.id.gridView1);
        dataList = new ArrayList<>();
        adapter = new MyAdapter(dataList, this);
        gridView.setAdapter(adapter);
        RecyclerView recyclerView = findViewById(R.id.recyclerView1);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        categoryList = new ArrayList<>();
        adapter2 = new adminCategoryAdapter(categoryList,this);
        recyclerView.setAdapter(adapter2);

        firestore = FirebaseFirestore.getInstance();

        db.collection("Category")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                String categoryName = document.getString("CategoryName");
                                categoryList.add(new Category(categoryName));
                            }
                            adapter2.notifyDataSetChanged();
                        } else {
                        }
                    }
                });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadDataFromFirestoreSearchFilter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadDataFromFirestoreSearchFilter(newText);
                return true;
            }
        });


        if (adapter2.ticket.equals("Home")){
            loadDataFromFirestore();
        }else {
            loadDataFromFirestorefilter(adapter2.ticket);
        }


    }

    private void loadDataFromFirestore() {
        db.collection("Products")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        dataList.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            DataClass dataClass = document.toObject(DataClass.class);
                            dataList.add(dataClass);
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Failed to load data: " + e.getMessage());
                    }
                });
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(adminhomescreen.this, AddProduct.class);
                startActivity(intent);
            }
        });

    }
    void loadDataFromFirestoreSearchFilter(final String query) {
        db.collection("Products")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        dataList.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String productName = document.getString("productName");
                            if (productName != null && productName.toLowerCase().contains(query.toLowerCase())) {
                                DataClass dataClass = document.toObject(DataClass.class);
                                dataList.add(dataClass);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Failed to load data: " + e.getMessage());
                    }
                });
    }
    void loadDataFromFirestorefilter(String ticket) {
        db.collection("Products").whereEqualTo("ticket",ticket)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        dataList.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            DataClass dataClass = document.toObject(DataClass.class);
                            dataList.add(dataClass);
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Failed to load data: " + e.getMessage());
                    }
                });

    }

}
