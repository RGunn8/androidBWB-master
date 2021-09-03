package com.learning.leap.bwb.userInfo;

import androidx.annotation.NonNull;

import com.learning.leap.bwb.Player;
import com.learning.leap.bwb.model.BabbleUser;
import com.learning.leap.bwb.models.BabblePlayer;


public interface UserInfoViewInterface {
    void displayErrorDialog(int dialogTitle, int dialogMessage);

    void dismissActivity();

    void downloadIntent();

    void displayUserInfo(@NonNull BabbleUser babbleUser);

    void displaySaveDialog();

    void dismissSaveDialog();

    String getUserGender();

}
