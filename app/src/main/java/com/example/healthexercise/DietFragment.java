package com.example.healthexercise;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class DietFragment extends Fragment {

    FirebaseFirestore db;
    DocumentReference documentReference;

    private final int REQ_CODE_SPEECH_INPUT = 100;
    static Button voiceBtn;
    static EditText infoBox;

    ListView listView;
    int pos;

    private String storedEmail;
    private String storedPassword;

    private int selectedHour;
    private int selectedMinute;
    private String selectedTime;
    private String completeMealInfo;

    String[] days;

    private String meal1;
    private String meal2;
    private String meal3;
    private String meal4;
    private String meal5;
    private String meal6;
    private String meal7;
    private String meal8;

    private String mealTime1;
    private String mealTime2;
    private String mealTime3;
    private String mealTime4;
    private String mealTime5;
    private String mealTime6;
    private String mealTime7;
    private String mealTime8;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.diet_frag, container, false);

        // Getting account info
        SharedPreferences info = getActivity().getSharedPreferences("USER", Context.MODE_PRIVATE);
        storedEmail = info.getString("email", "");
        storedPassword = info.getString("password", "");
        Log.d("EMAIL: ", storedEmail);
        Log.d("PASSWORD", storedPassword);

        // Get database instance and document reference for user data
        db = FirebaseFirestore.getInstance();
        documentReference = db.collection("users").document(storedEmail);

        // initialize listview
        listView = (ListView) v.findViewById(R.id.list);

        // Load initial data from database
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    meal1 = documentSnapshot.getString("meal1");
                    meal2 = documentSnapshot.getString("meal2");
                    meal3 = documentSnapshot.getString("meal3");
                    meal4 = documentSnapshot.getString("meal4");
                    meal5 = documentSnapshot.getString("meal5");
                    meal6 = documentSnapshot.getString("meal6");
                    meal7 = documentSnapshot.getString("meal7");
                    meal8 = documentSnapshot.getString("meal8");

                    mealTime1 = meal1.substring(0, Math.min(meal1.length(), 5));
                    mealTime2 = meal2.substring(0, Math.min(meal2.length(), 5));
                    mealTime3 = meal3.substring(0, Math.min(meal3.length(), 5));
                    mealTime4 = meal4.substring(0, Math.min(meal4.length(), 5));
                    mealTime5 = meal5.substring(0, Math.min(meal5.length(), 5));
                    mealTime6 = meal6.substring(0, Math.min(meal6.length(), 5));
                    mealTime7 = meal7.substring(0, Math.min(meal7.length(), 5));
                    mealTime8 = meal8.substring(0, Math.min(meal8.length(), 5));

                    days = new String[] {
                            "1: " + mealTime1,
                            "2: " + mealTime2,
                            "3: " + mealTime3,
                            "4: " + mealTime4,
                            "5: " + mealTime5,
                            "6: " + mealTime6,
                            "7: " + mealTime7,
                            "8: " + mealTime8

                    };

                    final List<String> days_list = new ArrayList<String>(Arrays.asList(days));

                    ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), R.layout.list_item, R.id.txtitem, days_list);

                    listView.setAdapter(adapter);
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                openDialogDiet();
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                openDialogViewDiet();
                pos = position;
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {

                if (documentSnapshot.exists()){

                    meal1 = documentSnapshot.getString("meal1");
                    meal2 = documentSnapshot.getString("meal2");
                    meal3 = documentSnapshot.getString("meal3");
                    meal4 = documentSnapshot.getString("meal4");
                    meal5 = documentSnapshot.getString("meal5");
                    meal6 = documentSnapshot.getString("meal6");
                    meal7 = documentSnapshot.getString("meal7");
                    meal8 = documentSnapshot.getString("meal8");

                    mealTime1 = meal1.substring(0, Math.min(meal1.length(), 5));
                    mealTime2 = meal2.substring(0, Math.min(meal2.length(), 5));
                    mealTime3 = meal3.substring(0, Math.min(meal3.length(), 5));
                    mealTime4 = meal4.substring(0, Math.min(meal4.length(), 5));
                    mealTime5 = meal5.substring(0, Math.min(meal5.length(), 5));
                    mealTime6 = meal6.substring(0, Math.min(meal6.length(), 5));
                    mealTime7 = meal7.substring(0, Math.min(meal7.length(), 5));
                    mealTime8 = meal8.substring(0, Math.min(meal8.length(), 5));

                    days = new String[] {
                            "1: " + mealTime1,
                            "2: " + mealTime2,
                            "3: " + mealTime3,
                            "4: " + mealTime4,
                            "5: " + mealTime5,
                            "6: " + mealTime6,
                            "7: " + mealTime7,
                            "8: " + mealTime8

                    };

                    final List<String> days_list = new ArrayList<String>(Arrays.asList(days));

                    ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), R.layout.list_item, R.id.txtitem, days_list);

                    listView.setAdapter(adapter);

                }

            }
        });
    }

    private void openDialogDiet(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View subView = inflater.inflate(R.layout.custom_dialog_3, null);
        final TimePicker timePicker = subView.findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);
        infoBox = (EditText)subView.findViewById(R.id.info_box);
        voiceBtn = (Button)subView.findViewById(R.id.btn_voice);
        voiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                        "Hi speak something");
                try {
                    startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
                } catch (ActivityNotFoundException a) {

                }
            }
        });



        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
        builder.setTitle("Enter Meal For Diet");
        builder.setView(subView);
        AlertDialog alertDialog = builder.create();


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (pos){
                    case 0:
                        selectedHour = timePicker.getCurrentHour();
                        selectedMinute = timePicker.getCurrentMinute();
                        selectedTime = String.valueOf(selectedHour) + ":" + String.valueOf(selectedMinute);
                        completeMealInfo = selectedTime + " " + infoBox.getText();

                        documentReference.update("meal1", completeMealInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });

                        break;

                    case 1:
                        selectedHour = timePicker.getCurrentHour();
                        selectedMinute = timePicker.getCurrentMinute();
                        selectedTime = String.valueOf(selectedHour) + ":" + String.valueOf(selectedMinute);
                        completeMealInfo = selectedTime + " " + infoBox.getText();

                        documentReference.update("meal2", completeMealInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });

                        break;

                    case 2:
                        selectedHour = timePicker.getCurrentHour();
                        selectedMinute = timePicker.getCurrentMinute();
                        selectedTime = String.valueOf(selectedHour) + ":" + String.valueOf(selectedMinute);
                        completeMealInfo = selectedTime + " " + infoBox.getText();

                        documentReference.update("meal3", completeMealInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });

                        break;

                    case 3:
                        selectedHour = timePicker.getCurrentHour();
                        selectedMinute = timePicker.getCurrentMinute();
                        selectedTime = String.valueOf(selectedHour) + ":" + String.valueOf(selectedMinute);
                        completeMealInfo = selectedTime + " " + infoBox.getText();

                        documentReference.update("meal4", completeMealInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });

                        break;

                    case 4:
                        selectedHour = timePicker.getCurrentHour();
                        selectedMinute = timePicker.getCurrentMinute();
                        selectedTime = String.valueOf(selectedHour) + ":" + String.valueOf(selectedMinute);
                        completeMealInfo = selectedTime + " " + infoBox.getText();

                        documentReference.update("meal5", completeMealInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });

                        break;

                    case 5:
                        selectedHour = timePicker.getCurrentHour();
                        selectedMinute = timePicker.getCurrentMinute();
                        selectedTime = String.valueOf(selectedHour) + ":" + String.valueOf(selectedMinute);
                        completeMealInfo = selectedTime + " " + infoBox.getText();

                        documentReference.update("meal6", completeMealInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });

                        break;

                    case 6:
                        selectedHour = timePicker.getCurrentHour();
                        selectedMinute = timePicker.getCurrentMinute();
                        selectedTime = String.valueOf(selectedHour) + ":" + String.valueOf(selectedMinute);
                        completeMealInfo = selectedTime + " " + infoBox.getText();

                        documentReference.update("meal7", completeMealInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });

                        break;

                    case 7:
                        selectedHour = timePicker.getCurrentHour();
                        selectedMinute = timePicker.getCurrentMinute();
                        selectedTime = String.valueOf(selectedHour) + ":" + String.valueOf(selectedMinute);
                        completeMealInfo = selectedTime + " " + infoBox.getText();

                        documentReference.update("meal8", completeMealInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });

                        break;
                }

                Toast.makeText(getActivity(), "Meal added to diet", Toast.LENGTH_LONG).show();
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

    private void openDialogViewDiet(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View subView = inflater.inflate(R.layout.custom_dialog_4, null);
        final EditText mealInfo = (EditText)subView.findViewById(R.id.meal_info);

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    meal1 = documentSnapshot.getString("meal1");
                    meal2 = documentSnapshot.getString("meal2");
                    meal3 = documentSnapshot.getString("meal3");
                    meal4 = documentSnapshot.getString("meal4");
                    meal5 = documentSnapshot.getString("meal5");
                    meal6 = documentSnapshot.getString("meal6");
                    meal7 = documentSnapshot.getString("meal7");
                    meal8 = documentSnapshot.getString("meal8");

                    switch (pos){
                        case 0:

                            mealInfo.setText(meal1);

                            break;

                        case 1:

                            mealInfo.setText(meal2);

                            break;

                        case 2:

                            mealInfo.setText(meal3);

                            break;

                        case 3:

                            mealInfo.setText(meal4);

                            break;

                        case 4:

                            mealInfo.setText(meal5);

                            break;

                        case 5:

                            mealInfo.setText(meal6);

                            break;

                        case 6:

                            mealInfo.setText(meal7);

                            break;

                        case 7:

                            mealInfo.setText(meal8);

                            break;
                    }

                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
        builder.setTitle("Meal information");
        builder.setView(subView);
        AlertDialog alertDialog = builder.create();

        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });

        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    infoBox.setText(infoBox.getText() + ". " + result.get(0));
                }
                break;
            }

        }
    }
}
