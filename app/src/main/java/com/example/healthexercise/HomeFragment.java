package com.example.healthexercise;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.SENSOR_SERVICE;

public class HomeFragment extends Fragment implements SensorEventListener, StepListener {

    FirebaseFirestore db;
    DocumentReference documentReference;

    private ArcProgress arcProgress;
    private TextView weightInfo;
    private TextView heightInfo;
    private TextView genderInfo;
    private TextView ageInfo;
    private TextView calorieIntakeInfo;

    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private int numSteps;
    private int stepsGoalNum;
    private String monthDay;
    private float percent;
    private String numStepsString;
    private DecimalFormat df;
    private Double neededCalories;
    public static Thread t1;

    private String storedEmail;
    private String storedPassword;

    private String dbWeight;
    private String dbHeight;
    private String dbGender;
    private String dbAge;
    private String dbCaloriesGoal;
    private String dbStepsGoal;
    private String dbDate;

    private ImageButton editStep;
    private ImageButton editCalories;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home_frag, container, false);

        // Getting account info
        SharedPreferences info = getActivity().getSharedPreferences("USER", Context.MODE_PRIVATE);
        storedEmail = info.getString("email", "");
        storedPassword = info.getString("password", "");
        Log.d("EMAIL: ", storedEmail);
        Log.d("PASSWORD", storedPassword);

        // Textviews for fragment
        weightInfo = (TextView) v.findViewById(R.id.textWeightVal);
        heightInfo = (TextView) v.findViewById(R.id.textHeightVal);
        genderInfo = (TextView) v.findViewById(R.id.textGenderVal);
        ageInfo = (TextView) v.findViewById(R.id.textAgeVal);
        calorieIntakeInfo = (TextView) v.findViewById(R.id.textCalVal);

        //Progress bar
        arcProgress = (ArcProgress)v.findViewById(R.id.distance_progress);
        arcProgress.setSuffixText("");

        // Get database instance and document reference for user data
        db = FirebaseFirestore.getInstance();
        documentReference = db.collection("users").document(storedEmail);

        // Load initial data from database
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    numStepsString = documentSnapshot.getString("steps");
                    numSteps = Integer.parseInt(numStepsString);
                    dbWeight = documentSnapshot.getString("weight");
                    dbHeight = documentSnapshot.getString("height");
                    dbGender = documentSnapshot.getString("gender");
                    dbAge = documentSnapshot.getString("age");
                    dbCaloriesGoal = documentSnapshot.getString("calorieintake");
                    dbStepsGoal = documentSnapshot.getString("stepsgoal");
                    dbDate = documentSnapshot.getString("stepcounterdate");
                    stepsGoalNum = Integer.parseInt(dbStepsGoal);

                    Date date = Calendar.getInstance().getTime();

                    monthDay = new SimpleDateFormat("yyyy-MM-dd").format(date);

                    // Update date and reset steps if new day
                    if (!monthDay.equals(dbDate)){
                        Map<String, Object> user = new HashMap<>();
                        user.put("steps", "0");
                        user.put("stepcounterdate", monthDay);

                        documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }

                    // calculate percentage to steps goal if percent is less than 100

                    if (numSteps > Integer.parseInt(dbStepsGoal)){
                        percent = 100;
                    }

                    if (percent != 100){
                        percent = numSteps * 100f / stepsGoalNum;
                    }

                    arcProgress.setText(numStepsString);
                    arcProgress.setBottomText(dbStepsGoal);
                    arcProgress.setProgress(percent);

                    weightInfo.setText(dbWeight);
                    heightInfo.setText(dbHeight);
                    genderInfo.setText(dbGender);
                    ageInfo.setText(dbAge);
                    calorieIntakeInfo.setText(dbCaloriesGoal);

                }
            }
        });

        //Create step detector sensor
        sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);


        // Button for setting daily steps goal
        editStep = (ImageButton) v.findViewById(R.id.edit_steps_btn);
        editStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("BTN", "BTNWORKING");

                openDialogSteps();

            }
        });

        // button for calculating daily calorie intake
        editCalories = (ImageButton) v.findViewById(R.id.edit_calories_btn);
        editCalories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openDialogCalories();

            }
        });

        return v;

    }

    // Method contains snapshot listener for getting updated info from database
    @Override
    public void onStart() {
        super.onStart();

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {

                if (documentSnapshot.exists()){

                    numStepsString = documentSnapshot.getString("steps");
                    numSteps = Integer.parseInt(numStepsString);
                    dbWeight = documentSnapshot.getString("weight");
                    dbHeight = documentSnapshot.getString("height");
                    dbGender = documentSnapshot.getString("gender");
                    dbAge = documentSnapshot.getString("age");
                    dbCaloriesGoal = documentSnapshot.getString("calorieintake");
                    dbStepsGoal = documentSnapshot.getString("stepsgoal");
                    dbDate = documentSnapshot.getString("stepcounterdate");
                    stepsGoalNum = Integer.parseInt(dbStepsGoal);

                    Date date = Calendar.getInstance().getTime();

                    monthDay = new SimpleDateFormat("yyyy-MM-dd").format(date);

                    // Update date and reset steps if new day
                    if (!monthDay.equals(dbDate)){
                        Map<String, Object> user = new HashMap<>();
                        user.put("steps", "0");
                        user.put("stepcounterdate", monthDay);

                        documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }

                    if (percent != 100){
                        // Calculate percentage to steps goal
                        percent = numSteps * 100f / stepsGoalNum;
                    }


                    arcProgress.setText(numStepsString);
                    arcProgress.setBottomText(dbStepsGoal);
                    arcProgress.setProgress(percent);

                    weightInfo.setText(dbWeight);
                    heightInfo.setText(dbHeight);
                    genderInfo.setText(dbGender);
                    ageInfo.setText(dbAge);
                    calorieIntakeInfo.setText(dbCaloriesGoal);

                }

            }
        });

    }

    // used when detecting step detector sensor change
    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }

    }

    // Sensor accuracy method
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    // updating steps when step detector sensor changed
    @Override
    public void step(long timeNS) {

        numSteps++;

        numStepsString = Integer.toString(numSteps);

        documentReference.update("steps", numStepsString).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });

    }

    // Method creates dialog for changing daily steps goal
    private void openDialogSteps(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View subView = inflater.inflate(R.layout.custom_dialog, null);
        final EditText stepsGoalText = (EditText)subView.findViewById(R.id.dialogEditText);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
        builder.setTitle("Enter Steps");
        builder.setMessage("Enter your daily steps goal below ");
        builder.setView(subView);
        AlertDialog alertDialog = builder.create();

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dbStepsGoal = stepsGoalText.getText().toString();

                percent = 0;

                documentReference.update("stepsgoal", dbStepsGoal).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });

                Toast.makeText(getActivity(), "Daily step goal updated", Toast.LENGTH_LONG).show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "Cancel", Toast.LENGTH_LONG).show();
            }
        });

        builder.show();
    }

    // Method contains dialog for calculating and entering calorie intake
    private void openDialogCalories(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        df = new DecimalFormat("###.###");

        View subView = inflater.inflate(R.layout.custom_dialog_2, null);

        final EditText editWeight = (EditText)subView.findViewById(R.id.edit_weight);
        final EditText editHeight = (EditText)subView.findViewById(R.id.edit_height);

        final List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("Male");
        spinnerArray.add("Female");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner sItems = (Spinner) subView.findViewById(R.id.spinner);
        sItems.setAdapter(adapter);

        final EditText editAge = (EditText)subView.findViewById(R.id.edit_age);
        final TextView maintainWeightText = (TextView) subView.findViewById(R.id.text_maintain_weight);
        final EditText editCalories = (EditText)subView.findViewById(R.id.edit_calories);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            // Calculate calorie intake for changes in textfields
            @Override
            public void afterTextChanged(Editable s) {

                if (editWeight.getText().toString().trim().isEmpty() || editHeight.getText().toString().isEmpty()
                        || editAge.getText().toString().isEmpty()) {

                    maintainWeightText.setText("Leave no above fields empty");
                }

                else {

                    if (sItems.getSelectedItem().equals("Male")){

                        neededCalories = 66.4730 + (13.7516 *
                                Integer.parseInt(editWeight.getText().toString())) +
                                (5.0033 * Integer.parseInt(editHeight.getText().toString()))
                                - (6.7550 * Integer.parseInt(editAge.getText().toString()));


                        maintainWeightText.setText(String.valueOf(df.format(neededCalories)) + " " +
                                "calories a day to maintain weight");

                    } else if (sItems.getSelectedItem().equals("Female")){

                        neededCalories = 655.0955 + (9.5634 *
                                Integer.parseInt(editWeight.getText().toString())) +
                                (1.8496 * Integer.parseInt(editHeight.getText().toString()))
                                - (4.6756 * Integer.parseInt(editAge.getText().toString()));

                        maintainWeightText.setText(df.format(neededCalories) + " " +
                                "calories a day to maintain weight");
                    }
                }
            }
        };

        editWeight.addTextChangedListener(textWatcher);
        editAge.addTextChangedListener(textWatcher);
        editHeight.addTextChangedListener(textWatcher);

        // Calculate calorie intake for changes in spinner
        sItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                dbGender = sItems.getItemAtPosition(position).toString();

                if (editWeight.getText().toString().trim().isEmpty() || editHeight.getText().toString().isEmpty()
                        || editAge.getText().toString().isEmpty()) {

                    maintainWeightText.setText("Leave no above fields empty");
                }

                else {

                    if (sItems.getSelectedItem().equals("Male")){


                        neededCalories = 66.4730 + (13.7516 *
                                Integer.parseInt(editWeight.getText().toString())) +
                                (5.0033 * Integer.parseInt(editHeight.getText().toString()))
                                - (6.7550 * Integer.parseInt(editAge.getText().toString()));


                        maintainWeightText.setText(String.valueOf(df.format(neededCalories)) + " " +
                                "calories a day to maintain weight");

                    } else if (sItems.getSelectedItem().equals("Female")){


                        neededCalories = 655.0955 + (9.5634 *
                                Integer.parseInt(editWeight.getText().toString())) +
                                (1.8496 * Integer.parseInt(editHeight.getText().toString()))
                                - (4.6756 * Integer.parseInt(editAge.getText().toString()));

                        maintainWeightText.setText(df.format(neededCalories) + " " +
                                "calories a day to maintain weight");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        builder.setTitle("Set Calorie Intake");
        builder.setMessage("Enter your weight, height, gender and age to calculate calories needed to maintain weight" +
                " then enter your desired calorie intake");
        builder.setView(subView);
        AlertDialog alertDialog = builder.create();

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dbWeight = editWeight.getText().toString();
                dbHeight = editHeight.getText().toString();
                dbAge = editAge.getText().toString();
                dbCaloriesGoal = editCalories.getText().toString();

                Map<String, Object> user = new HashMap<>();
                user.put("weight", dbWeight);
                user.put("height", dbHeight);
                user.put("gender", dbGender);
                user.put("age", dbAge);
                user.put("calorieintake", dbCaloriesGoal);

                documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

                Toast.makeText(getActivity(), "Daily step goal updated", Toast.LENGTH_LONG).show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "Cancel", Toast.LENGTH_LONG).show();
            }
        });

        builder.show();
    }
}
