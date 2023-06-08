package com.example.ezystore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.example.ezystore.databinding.ActivitySingUpScreenBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpScreen extends AppCompatActivity {

    String Email;
    String Password;
    String RepeatPassword;
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
         Email = binding.emailEditText.getText().toString();
         Password = binding.passwordEditText.getText().toString();
         RepeatPassword = binding.repeatpasswordEditText.getText().toString();

        if (Email.equals("") || Password.equals("") || RepeatPassword.equals("")) {

            Toast.makeText(SignUpScreen.this, "Tüm alanları Doldurunuz!", Toast.LENGTH_LONG).show();

        } else {
            if (Password.equals(RepeatPassword)) {
                auth.createUserWithEmailAndPassword(Email, Password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        String userId = auth.getCurrentUser().getUid();
                        Intent intent = new Intent(SignUpScreen.this, preliminaryinformation.class);
                        intent.putExtra("Email", Email);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpScreen.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            } else {
                Toast.makeText(SignUpScreen.this, "Şifreler Uyuşmuyor!", Toast.LENGTH_LONG).show();
            }

        }


    }

}