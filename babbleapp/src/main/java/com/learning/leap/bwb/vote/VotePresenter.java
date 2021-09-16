package com.learning.leap.bwb.vote;

import com.learning.leap.bwb.baseInterface.BaseNotificationPresenter;
import com.learning.leap.bwb.model.BabbleTip;
import com.learning.leap.bwb.room.BabbleDatabase;

import java.util.List;

import io.reactivex.rxjava3.core.Single;


public class VotePresenter extends BaseNotificationPresenter {
    private int numberOfTips;
    private int bucketNumber;
    private VoteViewViewInterface voteViewInterface;

    VotePresenter(int numberOfTips, int bucketNumber, VoteViewViewInterface voteViewInterface) {
        this.numberOfTips = numberOfTips;
        this.bucketNumber = bucketNumber;
        this.voteViewInterface = voteViewInterface;
    }

    @Override
    public Single<List<BabbleTip>> getRealmResults() {
        return BabbleDatabase.Companion.getInstance(null).babbleTipDAO().getAll();
    }

    @Override
    public void onCreate() {
        setBaseNotificationViewInterface(voteViewInterface);
        babyName = baseNotificationViewInterface.babyName();
        getRealmResults();
        if (notifications.size() == 0) {
            voteViewInterface.homeIntent();
        } else {
            displayPrompt();
        }

    }

    private Boolean doHomeIntent() {
        return index == numberOfTips - 1;
    }

    void thumbUpButtonTapped() {
        updateRandomNotification();
        checkForHomeIntent();
    }

    void thumbDownButtonTapped() {
        updateRandomNotification();
        checkForHomeIntent();
    }

    private void checkForHomeIntent() {
        if (doHomeIntent()) {
            voteViewInterface.homeIntent();
        } else {
            index++;
            updateView();

        }
    }

    private void updateRandomNotification() {

        //Todo update random notificaiton
//        Realm realm = Realm.getDefaultInstance();
//        realm.beginTransaction();
//        tipAtIndex().setPlayToday(true);
//        realm.copyToRealmOrUpdate(tipAtIndex());
//        realm.commitTransaction();
//        realm.beginTransaction();
//        AnswerNotification answerNotification = new AnswerNotification();
//        answerNotification.setAnswerBucket(bucketNumber);
//        answerNotification.mAnswerTime = new Date();
//        realm.copyToRealm(answerNotification);
//        realm.commitTransaction();
    }

}
