package com.learning.leap.bwb.notification;

import com.learning.leap.bwb.baseInterface.BaseNotificationPresenter;
import com.learning.leap.bwb.model.BabbleTip;
import com.learning.leap.bwb.room.BabbleDatabase;

import java.util.List;

import io.reactivex.rxjava3.core.Single;


public class NotificaitonPresenter extends BaseNotificationPresenter {
    public boolean isAll = true;
    public boolean isCategory = false;
    public boolean isSubCategory = false;
    public boolean isFavorite = false;
    public String category;
    public String subCategory;

    @Override
    public Single<List<BabbleTip>> getRealmResults() {
        babyName = baseNotificationViewInterface.babyName();
        if (isAll) {
            return null;
            //.subscribe(this::setNotifications, Throwable::printStackTrace);

        } else if (isCategory) {
            return BabbleDatabase.Companion.getInstance(null).babbleTipDAO().getTipsForCategory(category);
            //.subscribe(this::setNotifications, Throwable::printStackTrace);
        } else if (isFavorite) {
            return BabbleDatabase.Companion.getInstance(null).babbleTipDAO().getTipsForFavorites();

        } else {
            return BabbleDatabase.Companion.getInstance(null).babbleTipDAO().getTipsFromSubCategory(subCategory);

        }
    }


}
