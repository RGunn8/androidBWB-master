package com.learning.leap.bwb.baseInterface;


import androidx.annotation.NonNull;

public interface BaseNotificationViewInterface {
    void displayPrompt(String prompt);

    void playSound(String fileName);

    void playVideo(String fileName);

    void displaySoundButton();

    void displayVideoButton();

    void hideStopButton();

    void displayStopButton();

    void hideSoundButton();

    void hideVideoButton();

    void stopPlayer();

    void updateFavorite(@NonNull Boolean isFavorite);

    String babyName();

    void hideFavoriteButton();
}
