package com.learning.leap.bwb.userInfo;

import com.learning.leap.bwb.baseInterface.LifeCycleInterface;
import com.learning.leap.bwb.models.BabblePlayer;
import com.learning.leap.bwb.models.Notification;

import java.util.List;

/**
 * Created by ryangunn on 12/17/16.
 */

public interface UserInfoPresenterInterface extends LifeCycleInterface {
    void updatePlayer();

    void saveNotifications(List<Notification> notifications);

    void retriveNotificationsFromAmazon();

    void createBabblePlayer(BabblePlayer babblePlayer);

    void checkUserInput();

    void loadPlayerFromSharedPref();
}
