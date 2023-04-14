package com.learning.leap.bwb.splash

sealed class SplashDestination{
    object Download: SplashDestination()
    object UserSetting : SplashDestination()
    object Home : SplashDestination()
}
