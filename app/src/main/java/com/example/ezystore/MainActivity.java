package com.example.ezystore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ezystore.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseAuth auth;
    public String Email;
    public String Password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        auth = FirebaseAuth.getInstance();


    }

    public void Login(View view) {

         Email = binding.resetemailEditText.getText().toString();
         Password = binding.passwordEditText.getText().toString();

        if (Email.equals("") || Password.equals("")) {

            Toast.makeText(MainActivity.this, "Tüm alanları Doldurunuz!", Toast.LENGTH_LONG).show();

        } else {

            auth.signInWithEmailAndPassword(Email, Password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                    // HomeScreen

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
        Intent intent = new Intent(MainActivity.this, SingUpScreen.class);
        startActivity(intent);
        finish();

    }

    public void PasswordReset(View view) {

        Intent intent = new Intent(MainActivity.this, PasswordReset.class);
        startActivity(intent);
        finish();
    }


}
