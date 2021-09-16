package com.learning.leap.bwb.userInfo;

import com.learning.leap.bwb.baseInterface.LifeCycleInterface;
import com.learning.leap.bwb.model.BabbleTip;
import com.learning.leap.bwb.model.BabbleUser;

import java.util.List;

/**
 * Created by ryangunn on 12/17/16.
 */

public interface UserInfoPresenterInterface extends LifeCycleInterface {
    void updatePlayer();

    void saveNotifications(List<BabbleTip> notifications);

    void retriveNotificationsFromAmazon();

    void createBabblePlayer(BabbleUser babblePlayer);

    void checkUserInput();

    void loadPlayerFromSharedPref();
}
