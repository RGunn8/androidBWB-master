package com.learning.leap.bwb.baseActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.learning.leap.bwb.R;
import com.learning.leap.bwb.library.PlayTodayActivity;

public class DetailActivity extends AppCompatActivity {

    public static final String DETAIL_INTENT = "detailIntent";
    public static final int PLAY_TODAY = 1;
    public static final int LIBRARY = 2;
    public static final int SETTING = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        int fragmentToDisplay = getIntent().getIntExtra(DETAIL_INTENT, 1);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.replace(R.id.detailFragment, getSelectedFragment(fragmentToDisplay));

        ft.commit();

    }

    private Fragment getSelectedFragment(int fragmentToDisplay) {
        return null;
    }
}
