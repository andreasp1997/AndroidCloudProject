package com.example.healthexercise;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText input_username;
    private EditText input_password;

    private String storedEmail;
    private String storedPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);


        input_username = (EditText)findViewById(R.id.input_username);
        input_password = (EditText)findViewById(R.id.input_password);



        final Button loginButton = (Button) findViewById(R.id.btn_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                storedEmail = input_username.getText().toString();
                storedPassword = input_password.getText().toString();
                signIn(input_username.getText().toString(),input_password.getText().toString());
            }
        });

        final Button regButton = (Button) findViewById(R.id.btn_register);
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);

                LoginActivity.this.startActivity(intent);

            }
        });
    }


    private void signIn(String email, String password){

        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            SharedPreferences.Editor editor = getSharedPreferences("USER", MODE_PRIVATE).edit();
                            editor.putString("email", storedEmail);
                            editor.putString("password", storedPassword);
                            editor.apply();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                            LoginActivity.this.startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }


}
