package com.learning.leap.bwb.baseInterface;

public interface PresenterInterface extends LifeCycleInterface {
    void onHomeButtonPressed();

    void onLibraryPressed();

    void onSettingPressed();

    void onPlayTodayPressed();

    void attachView(ViewInterface viewInterface);
}
