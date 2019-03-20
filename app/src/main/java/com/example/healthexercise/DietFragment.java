package com.example.healthexercise;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DietFragment extends Fragment {

    ListView listView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.diet_frag, container, false);

        listView = (ListView) v.findViewById(R.id.list);

        String[] days = new String[] {
                "1: ",
                "2: ",
                "3: ",
                "4: ",
                "5: ",
                "6:",
                "7: ",
                "8: "

        };

        final List<String> days_list = new ArrayList<String>(Arrays.asList(days));

        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), R.layout.list_item, R.id.txtitem, days_list);

        listView.setAdapter(adapter);

        return v;
    }

    private void openDialogDiet(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View subView = inflater.inflate(R.layout.custom_dialog_3, null);
        final TimePicker timePicker = subView.findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);
        final EditText infoBox = (EditText)subView.findViewById(R.id.info_box);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
        builder.setTitle("Enter Meal For Diet");
        builder.setView(subView);
        AlertDialog alertDialog = builder.create();

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
}
