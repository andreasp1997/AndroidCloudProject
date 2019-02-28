package com.example.healthexercise;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private EditText email;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        username = (EditText) findViewById(R.id.input_usernamereg);
        password = (EditText) findViewById(R.id.input_password);
        email = (EditText) findViewById(R.id.input_email);

    }

    private void registerUser(){
        String string_username = username.getText().toString().trim();
        String string_password = password.getText().toString().trim();
        String string_email = email.getText().toString().trim();

        if(TextUtils.isEmpty(string_username)){
            Toast.makeText(getApplicationContext(),"You forgot the username",Toast.LENGTH_LONG);
            return;
        }

        if(TextUtils.isEmpty(string_password)){
            Toast.makeText(getApplicationContext(),"You forgot the password",Toast.LENGTH_LONG);
            return;
        }

        if(TextUtils.isEmpty(string_email)){
            Toast.makeText(getApplicationContext(),"You forgot the email",Toast.LENGTH_LONG);
            return;
        }

    }
}
