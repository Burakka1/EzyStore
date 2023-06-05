package com.example.ezystore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class Cart extends AppCompatActivity {

    private CartAdapter adapter;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Product> productList = new ArrayList<>();
    private String user;
    private Integer amount;
    private List<Integer> amountList = new ArrayList<>();
    private TextView textTotal;
    private Button siparisadd;
    private ImageButton Home;
    private ImageButton Profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Home = findViewById(R.id.Home);
        Profile = findViewById(R.id.Profile);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            user = currentUser.getEmail();
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        textTotal = findViewById(R.id.textTotal);
        siparisadd = findViewById(R.id.siparisadd);

        adapter = new CartAdapter(productList, this, textTotal);
        recyclerView.setAdapter(adapter);

        loadDataFromFirestore();

        Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Cart.this, Profile.class);
                startActivity(intent);
                finish();

            }
        });
        siparisadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationDialog();


            }
        });

        Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Cart.this,HomeScreen.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void loadDataFromFirestore() {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(today);
        db.collection("Bag").whereEqualTo("Email", user)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        productList.clear();
                        amountList.clear();
                        Integer total = 0;
                        amount = 0;

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String productName = document.getString("ProductName");
                            String price = document.getString("Price");
                            String imageUrl = document.getString("imageUrl");
                            String count = document.getString("count");
                            String id = document.getId();
                            String email = document.getString("Email");


                            Product dataClass = new Product(productName, price, imageUrl, count, id, email, formattedDate);
                            productList.add(dataClass);

                            total = Integer.parseInt(price);
                            amount = Integer.parseInt(count);

                            amount = amount * total;
                            amountList.add(amount);
                        }

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

    private void addOrdersToFirestore() {
        for (Product product : productList) {
            db.collection("Orders").add(product)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            deleteProductFromBag(product.getId());
                            Intent intent = new Intent(Cart.this, HomeScreen.class);
                            startActivity(intent);
                            finish();

                        }

                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Cart.this, "Sipariş verilirken bir hata oluştu.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void deleteProductFromBag(String productId) {
        db.collection("Bag").document(productId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        refreshCart();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Cart.this, "Sipariş verirken bir hata oluştu.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void refreshCart() {
        loadDataFromFirestore();
    }
    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Cart.this);
        builder.setTitle("Sipariş Onayı");
        builder.setMessage("Siparişi onaylıyor musunuz?");

        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addOrdersToFirestore();
                Toast.makeText(Cart.this, "Siparişiniz Başarıyla Tamamlandı", Toast.LENGTH_SHORT).show();

            }
        });

        builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}


