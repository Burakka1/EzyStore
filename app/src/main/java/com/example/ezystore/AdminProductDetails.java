package com.example.ezystore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class AdminProductDetails extends AppCompatActivity {
    private ImageView adminimageProduct;
    private TextView textDescription;
    private TextView admintextTotal,admintextProductName;
    private Button deleteProduct;

    private FirebaseFirestore firestore;
    private String imageURL;
    private String price;
    private String description,productName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_details);

        adminimageProduct = findViewById(R.id.adminimageProduct);
        admintextProductName = findViewById(R.id.admintextProductName);
        textDescription = findViewById(R.id.admintextDescription);
        admintextTotal = findViewById(R.id.admintextTotal);
        deleteProduct = findViewById(R.id.deleteproduct);

        firestore = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        if (intent != null) {
            productName = intent.getStringExtra("productName");
            imageURL = intent.getStringExtra("imageURL");

        }
        getProductDetailsFromFirestore();
        deleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteProduct();
                Toast.makeText(AdminProductDetails.this, "Ürün Başarıyla silindi", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getProductDetailsFromFirestore() {
        firestore.collection("Products")
                .whereEqualTo("productName", productName)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            description = documentSnapshot.getString("description");
                            price = documentSnapshot.getString("price");
                            imageURL = documentSnapshot.getString("imageURL");

                            Glide.with(AdminProductDetails.this).load(imageURL).into(adminimageProduct);
                            admintextProductName.setText(productName);
                            textDescription.setText(description);
                            admintextTotal.setText(price + " Tl");
                        }
                    }
                });
    }

    private void deleteProduct() {
        firestore.collection("Products")
                .whereEqualTo("productName", productName)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String productId = document.getId();
                            firestore.collection("Products").document(productId)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Intent intent = new Intent(AdminProductDetails.this, adminhomescreen.class);
                                            startActivity(intent);
                                            finish();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}
