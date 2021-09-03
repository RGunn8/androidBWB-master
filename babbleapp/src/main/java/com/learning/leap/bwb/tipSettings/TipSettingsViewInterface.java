package com.learning.leap.bwb.tipSettings;

import com.learning.leap.bwb.baseInterface.ViewInterface;

/**
 * Created by ryangunn on 12/25/16.
 */

public interface TipSettingsViewInterface extends ViewInterface {
    void displayStartTimeAtIndex(String startTime);

    void displayEndTimeAtIndex(String endTime);

    void displayUserMaxTipsAtIndex(String maxTips);

    void hideStartTimePlusSigned();

    void displayStartTimePlusSigned();

    void hideStartTimeMinusSigned();

    void displayStartTimeMinusSigned();

    void hideEndTimePlusSigned();

    void displayEndTimePlusSigned();

    void hideEndTimeMinusSigned();

    void displayEndTimeMinusSigned();

    void hideMaxNumberOfTipsPlusSigned();

    void displayMaxNumberOfTipsPlusSigned();

    void hideMaxNumberOfTipsMinusSigned();

    void displayMaxNumberOfTipsMinusSigned();

    void setTipSwitch(Boolean sendTipsToday);

    void displaySaveButton();

    void displayErrorMessage();

    boolean tipOneSwitchIsOn();

    boolean tipTwoSwitchIsOn();

    void saveCompleted(boolean turnOffTips);

    void displayFirstSwitch(boolean readBoolSharedPreferences);

    void displaySecondSwitch(boolean readBoolSharedPreferences);
}
