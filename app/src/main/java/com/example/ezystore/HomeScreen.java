package com.example.ezystore;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import androidx.appcompat.widget.SearchView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class HomeScreen extends AppCompatActivity {
    private GridView gridView;
    private ArrayList<DataClass> dataList;
    private MyAdapter adapter;
    private CategoryAdapter adapter2;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Category> categoryList;
    private FirebaseFirestore firestore;
    private ImageButton Cart;
    private ImageButton Home;
    private ImageButton Profile;
    private SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        searchView = (SearchView) findViewById(R.id.searchView);
        Profile = findViewById(R.id.Profile);
        Cart = findViewById(R.id.cart);
        Home = findViewById(R.id.Home);
        gridView = findViewById(R.id.gridView);
        dataList = new ArrayList<>();
        adapter = new MyAdapter(dataList, this);
        gridView.setAdapter(adapter);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        categoryList = new ArrayList<>();
        adapter2 = new CategoryAdapter(categoryList, this);
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

        if (adapter2.ticket.equals("Home")) {
            loadDataFromFirestore();
        } else {
            loadDataFromFirestorefilter(adapter2.ticket);
        }

        Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, Profile.class);
                startActivity(intent);
                finish();
            }
        });

        Cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this, Cart.class);
                startActivity(intent);
                finish();
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

        Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadDataFromFirestore();
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
    }

    void loadDataFromFirestorefilter(String ticket) {
        db.collection("Products")
                .whereEqualTo("ticket", ticket)
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
