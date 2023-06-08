package com.example.ezystore;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {

    private TextView textFullName, textPhoneNumber;
    private EditText editFullName, editPhoneNumber, editAddress;
    private Button btnEdit, btnSave,btnSave2;
    private ImageButton cart2,home2,profile;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private boolean isEditMode = false;
    private boolean isclicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        cart2 = findViewById(R.id.cart2);
        home2 = findViewById(R.id.home2);
        profile = findViewById(R.id.profile);
        textFullName = findViewById(R.id.textFullName);
        textPhoneNumber = findViewById(R.id.textPhoneNumber);
        editFullName = findViewById(R.id.editFullName);
        editPhoneNumber = findViewById(R.id.editPhoneNumber);
        editAddress = findViewById(R.id.editAddress);
        btnEdit = findViewById(R.id.btnEdit);
        btnSave = findViewById(R.id.btnSave);
        btnSave2=findViewById(R.id.btnSave2);
        btnSave.setEnabled(isclicked);
        btnSave2.setEnabled(isclicked);
        String address = getIntent().getStringExtra("address");
        isEditMode = getIntent().getBooleanExtra("edit_mode", false);
        editAddress.setText(address);


        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        invalidateOptionsMenu();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();

            db.collection("informations")
                    .whereEqualTo("Email", userEmail)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                                String fullName = documentSnapshot.getString("FullName");
                                String phoneNumber = documentSnapshot.getString("PhoneNumber");
                                String Adress = documentSnapshot.getString("Adress");

                                editFullName.setText(fullName);
                                editPhoneNumber.setText(phoneNumber);
                                editAddress.setText(Adress);

                                if (isEditMode) {
                                    enableEditMode(isclicked);
                                }
                            }
                        }
                    });
        }

        home2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, HomeScreen.class);
                startActivity(intent);
                finish();
            }
        });

        cart2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, Cart.class);
                startActivity(intent);
                finish();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isclicked = !isclicked;
                enableEditMode(isclicked);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatePhoneNumber()) {
                    updateUserInfo();
                    disableEditMode();
                    btnSave.setVisibility(View.INVISIBLE);
                    btnSave2.setVisibility(View.INVISIBLE);
                }
            }
        });
        btnSave2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, MapsActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_item1) {
            Intent intent = new Intent(Profile.this, past_orders.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_item2) {
            Intent intent = new Intent(Profile.this, PasswordReset.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.menu_item3) {
            Intent intent = new Intent(Profile.this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void enableEditMode(Boolean isclicked) {

        editFullName.setEnabled(isclicked);
        editPhoneNumber.setEnabled(isclicked);
        editAddress.setEnabled(isclicked);
        btnSave.setEnabled(isclicked);
        btnSave2.setEnabled(isclicked);
    }

    private void disableEditMode() {
        editFullName.setEnabled(false);
        editPhoneNumber.setEnabled(false);
        editAddress.setEnabled(false);
    }

    private void updateUserInfo() {
        String updatedFullName = editFullName.getText().toString();
        String updatedPhoneNumber = editPhoneNumber.getText().toString();
        String updatedAddress = editAddress.getText().toString();

        if (currentUser != null) {
            String userEmail = currentUser.getEmail();

            db.collection("informations")
                    .whereEqualTo("Email", userEmail)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                                String documentId = documentSnapshot.getId();

                                Map<String, Object> updatedInfo = new HashMap<>();
                                updatedInfo.put("FullName", updatedFullName);
                                updatedInfo.put("PhoneNumber", updatedPhoneNumber);
                                updatedInfo.put("Adress", updatedAddress);

                                db.collection("informations")
                                        .document(documentId)
                                        .update(updatedInfo)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                            }
                        }
                    });
        }
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = editPhoneNumber.getText().toString().trim();
        if (phoneNumber.length() < 10 || !phoneNumber.matches("\\d+")) {
            Toast.makeText(Profile.this, "Telefon numaranızı başında sıfır olmadan 10 hane giriniz", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}