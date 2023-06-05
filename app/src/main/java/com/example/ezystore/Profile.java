package com.example.ezystore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        cart2 = findViewById(R.id.cart2);
        home2 = findViewById(R.id.home2);
        profile = findViewById(R.id.Profile);
        textFullName = findViewById(R.id.textFullName);
        textPhoneNumber = findViewById(R.id.textPhoneNumber);
        editFullName = findViewById(R.id.editFullName);
        editPhoneNumber = findViewById(R.id.editPhoneNumber);
        editAddress = findViewById(R.id.editAddress);
        btnEdit = findViewById(R.id.btnEdit);
        btnSave = findViewById(R.id.btnSave);
        btnSave2=findViewById(R.id.btnSave2);

        String address = getIntent().getStringExtra("address");
        isEditMode = getIntent().getBooleanExtra("edit_mode", false);
        editAddress.setText(address);

        // Firebase Firestore ve Auth örnekleri al
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        invalidateOptionsMenu();

        // Firestore'dan kullanıcının bilgilerini al ve EditText'lere yerleştir
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
                                String email = documentSnapshot.getString("Email");
                                String Adress = documentSnapshot.getString("Adress");

                                editFullName.setText(fullName);
                                editPhoneNumber.setText(phoneNumber);
                                editAddress.setText(Adress);

                                if (isEditMode) {
                                    enableEditMode();
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Hata durumunda hata mesajı gösterebilirsiniz
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
                // Edit düğmesine tıklanınca EditText'lere düzenlenebilir hale gelir
                btnSave.setVisibility(View.VISIBLE);
                enableEditMode();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kaydet düğmesine tıklanınca EditText'teki bilgiler güncellenir ve tekrar devre dışı bırakılır
                if (validatePhoneNumber()) {
                    updateUserInfo();
                    disableEditMode();
                    btnSave.setVisibility(View.INVISIBLE);
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
            finish();
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


    private void enableEditMode() {
        editFullName.setEnabled(true);
        editPhoneNumber.setEnabled(true);
        editAddress.setEnabled(true);
        btnSave.setVisibility(View.VISIBLE);
        btnSave2.setVisibility(View.VISIBLE);
    }

    private void disableEditMode() {
        editFullName.setEnabled(false);
        editPhoneNumber.setEnabled(false);
        editAddress.setEnabled(false);
    }

    private void updateUserInfo() {
        // EditText'teki bilgileri al ve Firebase Firestore'e güncelleme yap
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

                                // Bilgileri güncelle
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
                                                // Güncelleme işlemi başarılı olduğunda gerçekleştirilecek işlemler
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Hata durumunda hata mesajı gösterebilirsiniz
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Hata durumunda hata mesajı gösterebilirsiniz
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