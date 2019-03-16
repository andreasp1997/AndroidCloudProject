package com.example.healthexercise;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText password;
    private EditText email;
    private Button registerbutton;
    private String dbEmail;
    private String dbPass;
    private String documentID;
    private String monthDay;

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        FirebaseApp.initializeApp(RegisterActivity.this);

        password = (EditText) findViewById(R.id.input_password);
        email = (EditText) findViewById(R.id.input_email);
        registerbutton = (Button) findViewById(R.id.btn_register);

        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbPass = password.getText().toString();
                dbEmail = email.getText().toString();
                documentID = dbEmail;
                createAccount(email.getText().toString(),password.getText().toString());
            }
        });
    }



    private void createAccount(final String email, String password){

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Date date = Calendar.getInstance().getTime();

        monthDay = new SimpleDateFormat("yyyy-MM-dd").format(date);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Map<String, String> user = new HashMap<>();
                            user.put("email", ""+ dbEmail + "");
                            user.put("password", "" + dbPass + "");
                            user.put("steps", "0");
                            user.put("stepsgoal", "100");
                            user.put("weight", "");
                            user.put("height", "");
                            user.put("age", "");
                            user.put("gender", "");
                            user.put("calorieintake", "");
                            user.put("meal1", "");
                            user.put("meal2", "");
                            user.put("meal3", "");
                            user.put("meal4", "");
                            user.put("meal5", "");
                            user.put("meal6", "");
                            user.put("meal7", "");
                            user.put("meal8", "");
                            user.put("latitude", "");
                            user.put("longitude", "");
                            user.put("distancecover", "");
                            user.put("stepcounterdate", monthDay);


                            DocumentReference dr = db.collection("users").document(documentID);
                            dr.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(RegisterActivity.this, "Registration successful!.",
                                    Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);

                            RegisterActivity.this.startActivity(intent);


                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity.this, "Registration failed. Email is either wrong or password is too short",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
}
