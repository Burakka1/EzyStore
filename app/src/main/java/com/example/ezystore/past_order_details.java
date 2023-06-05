package com.example.ezystore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class past_order_details extends AppCompatActivity {

    String productName, imageURL, price, count, formattedDate,userFullName,user;
    ImageView imageProduct;
    TextView  productNametext, orderdate, textTotal, countedit,userfullnametext;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_order_details);
        imageProduct = findViewById(R.id.imageProduct);
        productNametext = findViewById(R.id.productNametext);
        orderdate = findViewById(R.id.orderdate);
        textTotal = findViewById(R.id.textTotal);
        countedit = findViewById(R.id.countedit);
        userfullnametext = findViewById(R.id.userfullnametext);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            user = currentUser.getEmail();
        }
        Intent intent = getIntent();
        db.collection("informations").whereEqualTo("Email",user)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            userFullName = documentSnapshot.getString("FullName");
                            userfullnametext.setText(userFullName);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
        if (intent != null) {
            productName = intent.getStringExtra("productName");
            price = intent.getStringExtra("price");
            imageURL = intent.getStringExtra("imageUrl");
            count = intent.getStringExtra("count");
            formattedDate = intent.getStringExtra("formattedDate");
        }






        Glide.with(this)
                .load(imageURL)
                .into(imageProduct);
        productNametext.setText("Ürün Adı: "+productName);
        countedit.setText(count+" Adet");
        orderdate.setText("Sipariş Tarihi: "+formattedDate);
        textTotal.setText("Ürünün alınan adet fiyatı: "+price+ "tl");

    }



}