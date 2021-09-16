package com.learning.leap.bwb;

import com.learning.leap.bwb.baseInterface.BaseNotificationPresenter;
import com.learning.leap.bwb.model.BabbleTip;
import com.learning.leap.bwb.room.BabbleDatabase;

import java.util.List;

import io.reactivex.rxjava3.core.Single;


public class PlayTodayPresenter extends BaseNotificationPresenter {

    @Override
    public Single<List<BabbleTip>> getRealmResults() {
        babyName = notificationViewInterface.babyName();
        return BabbleDatabase.Companion.getInstance(null).babbleTipDAO().getNotificationForPlayToday();
    }

}

