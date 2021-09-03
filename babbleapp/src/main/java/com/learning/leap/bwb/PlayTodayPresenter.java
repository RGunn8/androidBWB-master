package com.learning.leap.bwb;

import com.learning.leap.bwb.baseInterface.BaseNotificationPresenter;
import com.learning.leap.bwb.baseInterface.ViewInterface;
import com.learning.leap.bwb.model.BabbleTip;
import com.learning.leap.bwb.models.Notification;
import com.learning.leap.bwb.notification.NotificaitonPresenter;
import com.learning.leap.bwb.notification.NotificationPresenterInterface;
import com.learning.leap.bwb.notification.NotificationViewViewInterface;


import io.reactivex.disposables.Disposable;
import io.realm.Realm;
import io.realm.RealmResults;


public class PlayTodayPresenter extends BaseNotificationPresenter {

    @Override
    public void getRealmResults() {
        babyName = notificationViewInterface.babyName();
        Disposable disposable = BabbleTip.Companion.getPlayTodayFromRealm(Realm.getDefaultInstance()).subscribe(this::setNotifications, Throwable::printStackTrace);
        disposables.add(disposable);
    }

}

