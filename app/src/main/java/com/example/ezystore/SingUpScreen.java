package com.example.ezystore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.ezystore.databinding.ActivityMainBinding;
import com.example.ezystore.databinding.ActivitySingUpScreenBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SingUpScreen extends AppCompatActivity {


    private ActivitySingUpScreenBinding binding;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySingUpScreenBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        auth = FirebaseAuth.getInstance();

    }

    public void NewMember(View view) {
        String Email = binding.emailEditText.getText().toString();
        String Password = binding.passwordEditText.getText().toString();
        String RepeatPassword = binding.repeatpasswordEditText.getText().toString();

        if (Email.equals("") || Password.equals("") || RepeatPassword.equals("")) {

            Toast.makeText(SingUpScreen.this, "Tüm alanları Doldurunuz!", Toast.LENGTH_LONG).show();

        } else {
            if (Password.equals(RepeatPassword)) {
                auth.createUserWithEmailAndPassword(Email, Password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        Intent intent = new Intent(SingUpScreen.this, MainActivity.class);
                        Toast.makeText(SingUpScreen.this, "Kaydınız Yapıldı", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SingUpScreen.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Toast.makeText(SingUpScreen.this, "Şifreler Uyuşmuyor!", Toast.LENGTH_LONG).show();
            }

        }


    }

}