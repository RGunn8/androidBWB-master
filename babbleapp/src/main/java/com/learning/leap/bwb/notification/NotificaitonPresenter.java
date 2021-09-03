package com.learning.leap.bwb.notification;

import com.learning.leap.bwb.baseInterface.BaseNotificationPresenter;
import com.learning.leap.bwb.model.BabbleTip;
import com.learning.leap.bwb.models.Notification;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;


public class NotificaitonPresenter extends BaseNotificationPresenter {
    public boolean isAll = true;
    public boolean isCategory = false;
    public boolean isSubCategory = false;
    public boolean isFavorite = false;
    public String category;
    public String subCategory;

    @Override
    public void getRealmResults() {
        babyName = baseNotificationViewInterface.babyName();
        if (isAll) {
            Disposable disposable = BabbleTip.Companion.getNotificationFromRealm(Realm.getDefaultInstance())
                    .subscribe(this::setNotifications, Throwable::printStackTrace);
            disposables.add(disposable);
        } else if (isCategory) {
            Disposable disposable = BabbleTip.Companion.getTipsWithCategory(category, Realm.getDefaultInstance())
                    .subscribe(this::setNotifications, Throwable::printStackTrace);
            disposables.add(disposable);
        } else if (isFavorite) {
            Disposable disposable = BabbleTip.Companion.getFavoriteTips(Realm.getDefaultInstance())
                    .subscribe(this::setNotifications, Throwable::printStackTrace);
            disposables.add(disposable);
        } else {
            Disposable disposable = BabbleTip.Companion.getTipsWithSubcategory(subCategory, Realm.getDefaultInstance())
                    .subscribe(this::setNotifications, Throwable::printStackTrace);
            disposables.add(disposable);
        }
    }


}
