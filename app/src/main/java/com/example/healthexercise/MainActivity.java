package com.example.healthexercise;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private String mapToggle;
    private String stepToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("WeFit");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(1).select();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {

            SharedPreferences.Editor editor = getSharedPreferences("USER", MODE_PRIVATE).edit();
            editor.putString("email", "");
            editor.putString("password", "");
            editor.apply();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            finish();
            startActivity(intent);
        }


        if (id == R.id.action_settings) {
            LayoutInflater inflater = LayoutInflater.from(this);
            View subView = inflater.inflate(R.layout.settings_dialog, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
            builder.setTitle("Settings");
            builder.setMessage("Here you can Enable/Disable the step and map function");
            builder.setView(subView);
            AlertDialog alertDialog = builder.create();

            SharedPreferences info = getSharedPreferences("USER", Context.MODE_PRIVATE);
            mapToggle = info.getString("maptoggle", "");
            stepToggle = info.getString("steptoggle", "");

            Switch mapswitch = (Switch) subView.findViewById(R.id.map_toggle);
            Switch stepswitch = (Switch) subView.findViewById(R.id.steps_toggle);

            if (mapToggle.equals("On")){
                mapswitch.setChecked(true);
            } else if (mapToggle.equals("Off")){
                mapswitch.setChecked(false);
            }

            if (stepToggle.equals("On")){
                stepswitch.setChecked(true);
            } else if (stepToggle.equals("Off")){
                stepswitch.setChecked(false);
            }

            stepswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        stepToggle = "On";
                    } else {
                        stepToggle = "Off";
                    }
                }
            });

            mapswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        mapToggle = "On";
                    } else {
                        mapToggle = "Off";
                    }
                }
            });

            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    SharedPreferences.Editor editor = getSharedPreferences("USER", MODE_PRIVATE).edit();

                    if (mapToggle.equals("On")){
                        editor.putString("maptoggle", "On");
                        ExerciseFragment.startExerciseBtn.setEnabled(true);
                        ExerciseFragment.startExerciseBtn.setBackgroundColor(Color.parseColor("#57BC90"));
                    } else if (mapToggle.equals("Off")){
                        editor.putString("maptoggle", "Off");
                        ExerciseFragment.startExerciseBtn.setEnabled(false);
                        ExerciseFragment.startExerciseBtn.setBackgroundColor(Color.parseColor("#888888"));
                    }

                    if (stepToggle.equals("On")){
                        editor.putString("steptoggle", "On");
                    } else if (stepToggle.equals("Off")){
                        editor.putString("steptoggle", "Off");
                    }

                    editor.apply();

                }
            });

            builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            builder.show();

        }

        return super.onOptionsItemSelected(item);
    }




    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new DietFragment();
                    break;
                case 1:
                    fragment = new HomeFragment();
                    break;
                case 2:
                    fragment = new ExerciseFragment();
                    break;
            }
            return fragment;

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Diet";
                case 1:
                    return "Home";
                case 2:
                    return "Exercise";
            }
            return null;
        }

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }
}
