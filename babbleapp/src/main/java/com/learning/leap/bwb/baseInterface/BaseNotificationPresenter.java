package com.learning.leap.bwb.baseInterface;

import com.learning.leap.bwb.model.BabbleTip;
import com.learning.leap.bwb.notification.NotificationPresenterInterface;
import com.learning.leap.bwb.notification.NotificationViewViewInterface;
import com.learning.leap.bwb.room.BabbleDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


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


    public abstract Single<List<BabbleTip>> getRealmResults();

    public void updateView() {
        if (isPlaying) {
            onStopButtonPress();
        }
        baseNotificationViewInterface.displayPrompt(tipAtIndex().getMessage());
        videoButtonCheck();
        soundButtonCheck();
        displayFavorite();
    }

    public void setNotifications(List<BabbleTip> notificationRealmResults) {
        notifications.addAll(notificationRealmResults);
        Collections.shuffle(notifications);
        totalCount = notifications.size();
        if (notificationViewInterface != null) {
            displayFirstTip();
        }
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
        BabbleTip tip = tipAtIndex();
        tip.setFavorite(!tip.getFavorite());
        Completable.fromAction(() -> {
            BabbleDatabase.Companion.getInstance(null).babbleTipDAO().updateTip(tip);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        return tip.getFavorite();
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
        Disposable disposable = getRealmResults().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setNotifications, Throwable::printStackTrace);
        disposables.add(disposable);
    }

    private void displayFirstTip() {
        notificationViewInterface.hidePreviousButton();
        if (notifications.size() <= 1) {
            notificationViewInterface.hideNextButton();
        }

        if (notifications.size() == 0) {
            hideAllButtons();
        } else {
            videoButtonCheck();
            soundButtonCheck();
            displayFavorite();
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
