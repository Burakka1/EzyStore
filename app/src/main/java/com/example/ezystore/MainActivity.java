package com.example.ezystore;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    String Email ;
    String Password ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView EmailTextView = findViewById(R.id.Emailtext);
        TextView PasswordTextview = findViewById(R.id.Passwordtext);
        Button Login = findViewById(R.id.LoginButton);



        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Email = EmailTextView.getText().toString();
                Password = PasswordTextview.getText().toString();
                System.out.println(Email);
            }
        });
    }
}