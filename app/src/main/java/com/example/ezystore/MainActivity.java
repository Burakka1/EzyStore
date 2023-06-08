package com.example.ezystore;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ezystore.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseAuth auth;
    public String Email;
    public String Password;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        sharedPreferences = this.getSharedPreferences("com.example.ezystore", Context.MODE_PRIVATE);
        auth = FirebaseAuth.getInstance();

        String CEmail = sharedPreferences.getString("Email","");
        String CPassword = sharedPreferences.getString("Password","");
        Boolean isboolean = sharedPreferences.getBoolean("isboolean",false);
        if (!CEmail.equals("") && !CPassword.equals("") && !isboolean.equals(false)){
                binding.EmailEditText.setText(CEmail);
                binding.passwordEditText.setText(CPassword);
                binding.checkBox.setChecked(isboolean);
        }

    }

    public void Login(View view) {

         Email = binding.EmailEditText.getText().toString();
         Password = binding.passwordEditText.getText().toString();

        if (Email.equals("") || Password.equals("")) {

            Toast.makeText(MainActivity.this, "Tüm alanları Doldurunuz!", Toast.LENGTH_LONG).show();

        } else {

            auth.signInWithEmailAndPassword(Email, Password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    if (binding.checkBox.isChecked()){
                        sharedPreferences.edit().putString("Email",Email).apply();
                        sharedPreferences.edit().putString("Password",Password).apply();
                        sharedPreferences.edit().putBoolean("isboolean",true).apply();
                    } else {
                        sharedPreferences.edit().remove("Email").apply();
                        sharedPreferences.edit().remove("Password").apply();
                        sharedPreferences.edit().remove("isboolean").apply();
                    }

                    FirebaseUser user = auth.getCurrentUser();
                    String userId = user.getUid();
                    FirebaseFirestore.getInstance().collection("informations").document(userId)
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    String userType = documentSnapshot.getString("userType");
                                    if (userType != null && userType.equals("admin")) {
                                        Intent intent = new Intent(MainActivity.this, adminhomescreen.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Intent intent = new Intent(MainActivity.this, HomeScreen.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, "Veritabanı hatası: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Email veya Şifre Hatalı!", Toast.LENGTH_LONG).show();
                }
            });


        }

    }

    public void SingUp(View view) {
        Intent intent = new Intent(MainActivity.this, SignUpScreen.class);
        startActivity(intent);
        finish();

    }

    public void PasswordReset(View view) {

        Intent intent = new Intent(MainActivity.this, PasswordReset.class);
        startActivity(intent);
        finish();
    }


}
