package com.example.healthexercise;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//import com.github.lzyzsd.circleprogress.ArcProgress;

public class HomeFragment extends Fragment {

   //private ArcProgress arcProgress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home_frag, container, false);

       //arcProgress = (ArcProgress) v.findViewById(R.id.distance_progress);
       //arcProgress.setSuffixText("");

        return v;
    }
}
