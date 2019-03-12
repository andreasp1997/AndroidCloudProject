package com.example.healthexercise;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.ArcProgress;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.SENSOR_SERVICE;

public class HomeFragment extends Fragment implements SensorEventListener, StepListener {

    private ArcProgress arcProgress;

    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private int numSteps;
    Thread t1;

    private ImageButton editStep;
    private ImageButton editCalories;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home_frag, container, false);

        sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);

        arcProgress =(ArcProgress)v.findViewById(R.id.distance_progress);
        arcProgress.setSuffixText("");

        editStep = (ImageButton) v.findViewById(R.id.edit_steps_btn);
        editStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("BTN", "BTNWORKING");

                openDialogSteps();

            }
        });

        editCalories = (ImageButton) v.findViewById(R.id.edit_calories_btn);
        editCalories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openDialogCalories();

            }
        });

        t1 = new Thread() {
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                arcProgress.setText(String.valueOf(numSteps));
                                arcProgress.setProgress(numSteps);

                            }
                        });//runOnUiThread ends here
                    }//While ends here
                } catch (InterruptedException e) {
                }//Catch ends here
            }

        };

        t1.start();

        return v;

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    @Override
    public void step(long timeNS) {

        numSteps++;

    }

    private void openDialogSteps(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View subView = inflater.inflate(R.layout.custom_dialog, null);
        final EditText subEditText = (EditText)subView.findViewById(R.id.dialogEditText);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
        builder.setTitle("Enter Steps");
        builder.setMessage("Enter your daily steps goal below ");
        builder.setView(subView);
        AlertDialog alertDialog = builder.create();

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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

    private void openDialogCalories(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View subView = inflater.inflate(R.layout.custom_dialog_2, null);

        final EditText editWeight = (EditText)subView.findViewById(R.id.edit_weight);
        final EditText editHeight = (EditText)subView.findViewById(R.id.edit_height);

        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("Male");
        spinnerArray.add("Female");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) subView.findViewById(R.id.spinner);
        sItems.setAdapter(adapter);

        final EditText editAge = (EditText)subView.findViewById(R.id.edit_age);
        final TextView maintainWeightText = (TextView) subView.findViewById(R.id.text_maintain_weight);
        final EditText editCalories = (EditText)subView.findViewById(R.id.edit_calories);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
        builder.setTitle("Set Calorie Intake");
        builder.setMessage("Enter your weight, height, gender and age to calculate calories needed to maintain weight" +
                " then enter your desired calorie intake");
        builder.setView(subView);
        AlertDialog alertDialog = builder.create();

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
