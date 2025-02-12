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




}
