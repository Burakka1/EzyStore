package com.example.ezystore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Cart extends AppCompatActivity {

    private CartAdapter adapter;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Product> productList = new ArrayList<>();
    private String User;
    Integer amount;
    List<Integer> amountList = new ArrayList<>();
    TextView textTotal;
    Button siparisadd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        User = user.getEmail();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new CartAdapter(productList);
        recyclerView.setAdapter(adapter);
        textTotal = findViewById(R.id.textTotal);
        siparisadd = findViewById(R.id.siparisadd);

        loadDataFromFirestore();


        siparisadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                db.collection("Bag")
                        .whereEqualTo("Email", User)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    String productId = document.getId();
                                    // ID'yi kullanarak istediğiniz işlemleri yapabilirsiniz
                                    // Örneğin, Toast mesajıyla ID'yi gösterebiliriz:
                                    Toast.makeText(Cart.this, "Öğe ID'si: " + productId, Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Hata durumunda yapılacak işlemleri buraya ekleyin
                            }
                        });
            }
        });

    }

    private void loadDataFromFirestore() {
        db.collection("Bag").whereEqualTo("Email", User)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        productList.clear();
                        amountList.clear(); // amountList'i temizle
                        Integer total = 0;
                        amount = 0;

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String productName = document.getString("ProductName");
                            String price = document.getString("Price");
                            String imageUrl = document.getString("imageUrl");
                            String count = document.getString("count");
                            String id = document.getId();

                            Product dataClass = new Product(productName, price, imageUrl, count, id);
                            productList.add(dataClass);

                            total = Integer.parseInt(price);
                            amount = Integer.parseInt(count);

                            amount = amount * total;
                            amountList.add(amount);
                        }

                        // Toplam miktarı hesapla
                        Integer totalAmount = 0;
                        for (int price : amountList) {
                            totalAmount += price;
                        }
                        amount = totalAmount;
                        textTotal.setText(amount + " Tl");

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

    public void refreshCart() {
        loadDataFromFirestore();
    }
}
