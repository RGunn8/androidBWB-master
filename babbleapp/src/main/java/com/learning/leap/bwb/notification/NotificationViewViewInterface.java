package com.learning.leap.bwb.notification;

import com.learning.leap.bwb.baseInterface.BaseNotificationViewInterface;
import com.learning.leap.bwb.baseInterface.ViewInterface;

public interface NotificationViewViewInterface extends ViewInterface, BaseNotificationViewInterface {
    void hideNextButton();

    void hidePreviousButton();

    void displayNextButton();

    void displayPreviousButton();
}
