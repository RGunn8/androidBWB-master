package com.learning.leap.bwb.utility;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.appcompat.app.AlertDialog;

import android.util.DisplayMetrics;
import android.view.View;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.learning.leap.bwb.R;
import com.learning.leap.bwb.baseActivity.HomeActivity;
import com.learning.leap.bwb.download.DownloadActivity;
import com.learning.leap.bwb.helper.LocalLoadSaveHelper;
import com.learning.leap.bwb.models.ActionHistory;
import com.learning.leap.bwb.userInfo.UserInfoActivity;

public class Utility {
    private static final String sharedPreferencesFile = "Global";

    public static CognitoCredentialsProvider getCredientail(Context context) {
        return new CognitoCachingCredentialsProvider(
                context.getApplicationContext(),  /* get the context for the application */
                Constant.CognitoIdentityPoolId,    /* Identity Pool ID */
                Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        );
    }

    public static void displayAlertMessage(int errorTitle, int errorMessage, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(errorTitle);
        builder.setMessage(errorMessage);
        builder.setNeutralButton(R.string.okay, (dialogInterface, i) -> dialogInterface.dismiss());
        builder.show();
    }

    public static void newUserCheck(Context context) {
        Boolean didDownload = Utility.readBoolSharedPreferences(Constant.DID_DOWNLOAD, context);
        Boolean needUpdate = Utility.readBoolSharedPreferences(Constant.UPDATE, context);
        if (didDownload && !needUpdate) {
            homeIntent(context);
        } else if (needUpdate) {
            downloadIntent(context);
        } else {
            userInfoIntent(context);
        }
    }

    private static void userInfoIntent(Context context) {
        Intent userInfoIntent = new Intent(context, UserInfoActivity.class);
        userInfoIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        userInfoIntent.putExtra(Constant.NEW_USER, true);
        context.startActivity(userInfoIntent);
    }

    private static void downloadIntent(Context context) {
        Intent downloadIntent = new Intent(context, DownloadActivity.class);
        downloadIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        downloadIntent.putExtra(Constant.UPDATE, true);
        context.startActivity(downloadIntent);
    }

    public static void homeIntent(Context context) {
        Intent homeIntent = new Intent(context, HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(homeIntent);
    }

    public static void writeIntSharedPreferences(String sharedPreferenceKey, int intToWrite, Context context) {
        SharedPreferences mSharedPreferences;
        mSharedPreferences = context.getSharedPreferences(sharedPreferencesFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(sharedPreferenceKey, intToWrite);
        editor.apply();

    }

    public static void writeStringSharedPreferences(String sharedPreferenceKey, String stringToWrite, Context context) {
        SharedPreferences mSharedPreferences;
        mSharedPreferences = context.getSharedPreferences(sharedPreferencesFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(sharedPreferenceKey, stringToWrite);
        editor.apply();

    }

    public static void writeBoolenSharedPreferences(String sharedPreferenceKey, Boolean boolToWrite, Context context) {
        SharedPreferences mSharedPreferences;
        mSharedPreferences = context.getSharedPreferences(sharedPreferencesFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(sharedPreferenceKey, boolToWrite);
        editor.apply();

    }

    public static int readIntSharedPreferences(String sharedPreferenceKey, Context context) {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(sharedPreferencesFile, Context.MODE_APPEND);
        return mSharedPreferences.getInt(sharedPreferenceKey, 0);
    }

    public static String readStringSharedPreferences(String sharedPreferenceKey, Context context) {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(sharedPreferencesFile, Context.MODE_APPEND);
        return mSharedPreferences.getString(sharedPreferenceKey, null);
    }

    public static Boolean readBoolSharedPreferences(String sharedPreferenceKey, Context context) {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(sharedPreferencesFile, Context.MODE_APPEND);
        return mSharedPreferences.getBoolean(sharedPreferenceKey, false);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void addCustomEvent(String event, String ID, String tag, Context context) {
        ActionHistory.createActionHistoryItem(ID, event, tag, readStringSharedPreferences(Constant.GROUP_CODE, context));
    }


    public static String getUserID(Context context) {
        LocalLoadSaveHelper saveHelper = new LocalLoadSaveHelper(context);
        return saveHelper.getBabbleID();
    }


    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         DisplayMetrics displayMetrics) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, displayMetrics.widthPixels, displayMetrics.heightPixels);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    public static void hideButtonCheck(View playToday, View library) {

    }
}
