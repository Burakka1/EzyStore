package com.example.ezystore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.ezystore.databinding.ActivityMainBinding;
import com.example.ezystore.databinding.ActivityPreliminaryinformationBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class preliminaryinformation extends AppCompatActivity {

    private ActivityPreliminaryinformationBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    String fullname;
    String adress;
    String phoneNumber;
    HashMap<String, Object> postData = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPreliminaryinformationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    public void Save(View view) {

        fullname = binding.fullName.getText().toString();
        adress = binding.adress.getText().toString();
        phoneNumber = binding.phoneNumber.getText().toString();


        if (fullname.equals("") || adress.equals("") || phoneNumber.equals("")) {
            System.out.println(fullname + " " + adress + " " + phoneNumber);
            Toast.makeText(preliminaryinformation.this, "Tüm alanları doldurunuz!", Toast.LENGTH_LONG).show();
        } else {
            if (binding.phoneNumber.getText().toString().length() == 10) {
                phoneNumber = binding.phoneNumber.getText().toString();
                postData.put("FullName", fullname);
                postData.put("Adress", adress);
                postData.put("PhoneNumber", phoneNumber);
                //postData.put("Email",Email);

                firebaseFirestore.collection("informations").add(postData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        Intent intent = new Intent(preliminaryinformation.this, MainActivity.class);
                        Toast.makeText(preliminaryinformation.this, "Kaydınız Tamamlandı", Toast.LENGTH_LONG).show();
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(preliminaryinformation.this, "Beklenmedik Hata!", Toast.LENGTH_LONG).show();
                    }
                });

            } else {

                Toast.makeText(preliminaryinformation.this, "Telefon numaranızı başında sıfır olmadan 10 hane giriniz", Toast.LENGTH_LONG).show();

            }

        }


    }


}