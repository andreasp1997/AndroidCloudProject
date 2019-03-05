package com.example.healthexercise;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday",
                "Sunday"
        };

        final List<String> days_list = new ArrayList<String>(Arrays.asList(days));

        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), R.layout.list_item, R.id.txtitem, days_list);

        listView.setAdapter(adapter);

        return v;
    }
}
