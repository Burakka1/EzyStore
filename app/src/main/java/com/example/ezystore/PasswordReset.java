package com.example.ezystore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.ezystore.databinding.ActivityMainBinding;
import com.example.ezystore.databinding.ActivityPasswordResetBinding;
import com.example.ezystore.databinding.ActivitySingUpScreenBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordReset extends AppCompatActivity {


    private ActivityPasswordResetBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPasswordResetBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        auth = FirebaseAuth.getInstance();
    }

    public void ResetPassword(View view){

        String Email = binding.resetemailEditText.getText().toString();
        if (Email.equals("")){
            Toast.makeText(PasswordReset.this, "Emailinizi Giriniz!", Toast.LENGTH_LONG).show();
        }else {
            auth.sendPasswordResetEmail(Email).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                    Toast.makeText(PasswordReset.this, "Parola sıfırlama linki mailinize gönderildi", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(PasswordReset.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(PasswordReset.this, "Email Hatalı!", Toast.LENGTH_LONG).show();
                }
            });
        }


    }

}