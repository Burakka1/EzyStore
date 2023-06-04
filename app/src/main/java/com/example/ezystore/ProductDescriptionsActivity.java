package com.example.ezystore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ProductDescriptionsActivity extends AppCompatActivity {

    private ImageView imageProduct;
    private TextView textProductName;
    private TextView textDescription;
    private TextView textTotal;
    private Button buttonAddToCart;

    private FirebaseFirestore firestore;
    private String productName;
    private String imageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_descriptions);

        imageProduct = findViewById(R.id.imageProduct);
        textProductName = findViewById(R.id.textProductName);
        textDescription = findViewById(R.id.textDescription);
        textTotal = findViewById(R.id.textTotal);
        buttonAddToCart = findViewById(R.id.buttonAddToCart);

        firestore = FirebaseFirestore.getInstance();

        // productName ve imageURL değerlerini intent'ten al
        Intent intent = getIntent();
        if (intent != null) {
            productName = intent.getStringExtra("productName");
            imageURL = intent.getStringExtra("imageURL");
        }



        // Ürün adını göster
        textProductName.setText(productName);

        // Firestore'dan ürünün detaylarını al
        getProductDetailsFromFirestore();

        // Sepete Ekle butonuna tıklama olayını ekle
        buttonAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sepete ekleme işlemlerini burada gerçekleştirin
                // ...
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
                            String description = documentSnapshot.getString("description");
                            String price = documentSnapshot.getString("price");
                            imageURL = documentSnapshot.getString("imageURL");

                            // Ürün resmini yükle
                            Glide.with(ProductDescriptionsActivity.this).load(imageURL).into(imageProduct);

                            // Açıklamayı ve fiyatı göster
                            textDescription.setText(description);
                            textTotal.setText(price);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ProductDescriptions", "Error getting product details from Firestore: " + e.getMessage());
                    }
                });
    }
}
