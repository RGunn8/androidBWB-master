package com.learning.leap.bwb.baseInterface;

import android.widget.RelativeLayout;

import com.learning.leap.bwb.model.BabbleTip;
import com.learning.leap.bwb.notification.NotificationPresenterInterface;
import com.learning.leap.bwb.notification.NotificationViewViewInterface;

import java.util.ArrayList;
import java.util.Collections;

import io.reactivex.disposables.CompositeDisposable;
import io.realm.Realm;
import io.realm.RealmResults;


public abstract class BaseNotificationPresenter implements NotificationPresenterInterface {
    public final CompositeDisposable disposables = new CompositeDisposable();
    public NotificationViewViewInterface notificationViewInterface;
    public int index = 0;
    public int totalCount = 0;
    public BaseNotificationViewInterface baseNotificationViewInterface;
    public ArrayList<BabbleTip> notifications = new ArrayList<>();
    public String babyName;
    public boolean isPlaying;

    public void onDestory() {
        disposables.clear();
    }


    public abstract void getRealmResults();

    public void updateView() {
        if (isPlaying) {
            onStopButtonPress();
        }
        baseNotificationViewInterface.displayPrompt(tipAtIndex().getMessage());
        videoButtonCheck();
        soundButtonCheck();
    }

    public void setNotifications(RealmResults<BabbleTip> notificationRealmResults) {
        notifications.addAll(notificationRealmResults);
        Collections.shuffle(notifications);
        totalCount = notifications.size();
    }


    public void soundButtonCheck() {
        if (tipAtIndex().noSoundFile()) {
            baseNotificationViewInterface.hideSoundButton();
        } else {
            baseNotificationViewInterface.displaySoundButton();
        }
    }

    public void videoButtonCheck() {
        if (tipAtIndex().noVideFile()) {
            baseNotificationViewInterface.hideVideoButton();
        } else {
            baseNotificationViewInterface.displayVideoButton();
        }
    }

    public String getTag() {
        return tipAtIndex().getTag();
    }


    protected BabbleTip tipAtIndex() {
        return notifications.get(index);
    }

    public Boolean updateFavoriteForTip() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        boolean isFavorite = tipAtIndex().getFavorite();
        tipAtIndex().setFavorite(!isFavorite);
        realm.commitTransaction();
        return tipAtIndex().getFavorite();
    }


    public void displayFavorite() {
        baseNotificationViewInterface.updateFavorite(tipAtIndex().getFavorite());
    }

    protected void displayPrompt() {
        baseNotificationViewInterface.displayPrompt(tipAtIndex().updateMessage(babyName));
    }

    public void onPlayAudioPress() {
        String notificationSoundFile = tipAtIndex().getCreated() + "-" + tipAtIndex().getSoundFileName();
        baseNotificationViewInterface.playSound(notificationSoundFile);
        isPlaying = true;
        showStopButton();
    }

    public void onStopButtonPress() {
        removeStopButton();
        baseNotificationViewInterface.stopPlayer();
        isPlaying = false;
    }

    private void showStopButton() {
        baseNotificationViewInterface.displayStopButton();
        baseNotificationViewInterface.hideSoundButton();
    }

    private void removeStopButton() {
        baseNotificationViewInterface.hideStopButton();
        baseNotificationViewInterface.displaySoundButton();
    }

    public void onPlayVideoPress() {
        String notificationVideoFile = tipAtIndex().getCreated() + "-" + tipAtIndex().getVideoFileName();
        baseNotificationViewInterface.playVideo(notificationVideoFile);
    }

    protected void setBaseNotificationViewInterface(BaseNotificationViewInterface baseNotificationViewInterface) {
        this.baseNotificationViewInterface = baseNotificationViewInterface;
    }

    @Override
    public void onCreate() {
        setBaseNotificationViewInterface(notificationViewInterface);
        getRealmResults();
        notificationViewInterface.hidePreviousButton();
        if (notifications.size() <= 1) {
            notificationViewInterface.hideNextButton();
        }

        if (notifications.size() == 0) {
            hideAllButtons();
        } else {
            displayPrompt();
            baseNotificationViewInterface.updateFavorite(tipAtIndex().getFavorite());
        }
    }


    @Override
    public void onNextPress() {
        index++;
        nextButtonCheck();
        previousButtonCheck();
        displayPrompt();
        soundButtonCheck();
        videoButtonCheck();
        baseNotificationViewInterface.updateFavorite(tipAtIndex().getFavorite());
    }

    @Override
    public void onBackPress() {
        index--;
        previousButtonCheck();
        nextButtonCheck();
        displayPrompt();
        soundButtonCheck();
        videoButtonCheck();
        baseNotificationViewInterface.updateFavorite(tipAtIndex().getFavorite());
    }


    @Override
    public void onResume() {

    }

    @Override
    public void attachView(ViewInterface viewInterface) {
        notificationViewInterface = (NotificationViewViewInterface) viewInterface;
    }


    private Boolean indexIs0() {
        return index == 0;
    }

    private void nextButtonCheck() {
        if (index == totalCount - 1) {
            notificationViewInterface.hideNextButton();
        } else {
            notificationViewInterface.displayNextButton();
            if (isPlaying) {
                onStopButtonPress();
            }
        }
    }

    private void previousButtonCheck() {
        if (indexIs0()) {
            notificationViewInterface.hidePreviousButton();
        } else {
            notificationViewInterface.displayPreviousButton();
        }
    }

    private void hideAllButtons() {
        notificationViewInterface.hideNextButton();
        notificationViewInterface.hidePreviousButton();
        notificationViewInterface.hideSoundButton();
        notificationViewInterface.hideVideoButton();
        notificationViewInterface.hideFavoriteButton();
    }

    @Override
    public void onHomeButtonPressed() {
        notificationViewInterface.onHomePress();
    }

    @Override
    public void onLibraryPressed() {
        notificationViewInterface.onLibraryPress();
    }

    @Override
    public void onSettingPressed() {
        notificationViewInterface.onSettingsPress();
    }

    @Override
    public void onPlayTodayPressed() {
        notificationViewInterface.onPlayToday();
    }
}
