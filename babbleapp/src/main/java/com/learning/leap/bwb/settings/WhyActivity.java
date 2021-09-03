package com.learning.leap.bwb.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.learning.leap.bwb.R;

public class WhyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiy_why);
        ImageButton backToSettignsButton = findViewById(R.id.backSettingsButton);
        backToSettignsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WhyActivity.this.finish();
            }
        });


    }
}
