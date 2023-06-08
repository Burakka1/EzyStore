package com.example.ezystore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDescriptionsActivity extends AppCompatActivity {

    private TextView textProductName;
    private TextView textDescription;
    private TextView textTotal;
    private Button buttonAddToCart;

    private FirebaseFirestore firestore;
    private String productName;
    private String imageURL;
    private String imageURL2;
    private String price;
    private  String  Email;
    private String description;
    List<String> photoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_descriptions);


        textProductName = findViewById(R.id.textProductName);
        textDescription = findViewById(R.id.textDescription);
        textTotal = findViewById(R.id.textTotal);
        buttonAddToCart = findViewById(R.id.buttonAddToCart);

        firestore = FirebaseFirestore.getInstance();
        Map<String ,String > bag = new HashMap<>();
        Intent intent = getIntent();
        if (intent != null) {
            productName = intent.getStringExtra("productName");
            imageURL = intent.getStringExtra("imageURL");
             Email = getIntent().getStringExtra("Email");

        }

        ImageAdapter adapter = new ImageAdapter(photoList);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewimage);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)); // Yatay bir liste için
        recyclerView.setAdapter(adapter);


        textProductName.setText(productName);


        getProductDetailsFromFirestore();


        buttonAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bag.put("ProductName",productName);
                bag.put("Price",price);
                bag.put("imageUrl",imageURL);
                bag.put("Email",Email);
                bag.put("count","1");
                AddBag(bag);
                Intent intent = new Intent(ProductDescriptionsActivity.this, HomeScreen.class);
                startActivity(intent);
                finish();
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
                            imageURL2 = documentSnapshot.getString("imageURL2");
                            photoList.add(imageURL);
                            photoList.add(imageURL2);

                            textDescription.setText(description);
                            textTotal.setText(price + " Tl");
                        }
                    }
                });
    }

    private void AddBag(Map productsToAdd){


       firestore.collection("Bag")
                .add(productsToAdd)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        documentReference.update(productsToAdd);

                    }
                });

    }
}
