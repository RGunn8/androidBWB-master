package com.learning.leap.bwb.download;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.learning.leap.bwb.DownloadService;
import com.learning.leap.bwb.R;
import com.learning.leap.bwb.baseActivity.HomeActivity;
import com.learning.leap.bwb.utility.Constant;
import com.learning.leap.bwb.utility.Utility;

public class DownloadActivity extends AppCompatActivity {
    DownloadService downloadService;
    ProgressBar progressBar;
    TextView downloadPercentTextView;
    boolean bound = false;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DownloadService.LocalBinder binder = (DownloadService.LocalBinder) service;
            downloadService = binder.getServiceInstance();
            downloadService.registerClient(DownloadActivity.this);
            bound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    private void startDownloadService() {
        Intent downloadServiceIntent = new Intent(DownloadActivity.this, DownloadService.class);
        downloadServiceIntent.putExtra(Constant.UPDATE, getIntent().getBooleanExtra(Constant.UPDATE, false));
        startService(downloadServiceIntent);
        bindService(downloadServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

//    @Override
//    public boolean comingFromAgeRange() {
//        return getIntent().getBooleanExtra(Constant.COME_FROM_AGE_RANGE, false);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setUpProgressBar();
        if (getIntent().getBooleanExtra(Constant.COME_FROM_AGE_RANGE, false)) {
            TextView titleTextView = findViewById(R.id.downloadActivityTitleTextView);
            titleTextView.setText(R.string.updating);
        }
        startDownloadService();
    }

    @Override
    protected void onStop() {
        if (bound) {
            unbindService(mConnection);
            bound = false;
        }
        super.onStop();
    }

    @Override
    public void onResume() {
        if (Utility.readBoolSharedPreferences(Constant.DID_DOWNLOAD, this) && Utility.readBoolSharedPreferences(Constant.UPDATE_TO_TWO, this)) {
            Utility.homeIntent(this);
        } else {
            if (!bound) {
                startDownloadService();
            }
        }
        super.onResume();

    }

    private void setUpProgressBar() {
        progressBar = findViewById(R.id.downloadProgressBar);
        progressBar.setMax(100);
        progressBar.setIndeterminate(false);
        downloadPercentTextView = findViewById(R.id.downloadTextPercentage);
    }

//    @Override
//    public void displayErrorMessage() {
//        if (bound) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle(getString(R.string.BabbleError));
//            builder.setMessage(getString(R.string.downloadError));
//            builder.setNegativeButton("Close", (dialog, which) -> DownloadActivity.this.finish());
//            builder.setPositiveButton("Retry", (dialogInterface, i) -> startDownloadService());
//            builder.show();
//        }
//    }
//
//    @Override
//    public void updateProgressBar(int progress) {
//        if (bound) {
//            progressBar.setProgress(progress);
//            String downloadPercent = progress + "%";
//            downloadPercentTextView.setText(downloadPercent);
//        }
//    }
//
//    @Override
//    public void downloadCompleted() {
//        if (bound) {
//            if (getIntent().getBooleanExtra(Constant.COME_FROM_AGE_RANGE, false)) {
//                Intent homeIntent = new Intent(this, HomeActivity.class);
//                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(homeIntent);
//            } else {
//                Intent congratsIntent = new Intent(this, CongratsActivity.class);
//                congratsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(congratsIntent);
//            }
//        }
//    }

}
