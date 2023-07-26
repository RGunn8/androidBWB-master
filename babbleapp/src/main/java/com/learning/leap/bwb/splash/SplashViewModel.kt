package com.learning.leap.bwb.splash

import android.os.CountDownTimer
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learning.leap.bwb.utility.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) :ViewModel(){
    private val _splashDestination: MutableStateFlow<SplashScreenDestination> =
        MutableStateFlow(SplashScreenDestination.NONE)
    val splashDestination: StateFlow<SplashScreenDestination> = _splashDestination

    init {
        getDestination()
    }
    private fun getDestination(){
        viewModelScope.launch{
             dataStore.data.collectLatest {
                val didDownload = it[booleanPreferencesKey(Constant.DID_DOWNLOAD)] ?: false
                 val needUpdate = it[booleanPreferencesKey(Constant.UPDATE)] ?: false
                 if (didDownload && !needUpdate) {
                     _splashDestination.value = SplashScreenDestination.HOME
                 } else if (needUpdate) {
                     _splashDestination.value = SplashScreenDestination.DOWNLOAD
                 } else {
                     _splashDestination.value = SplashScreenDestination.USER_SETTINGS
                 }
            }
        }

    }

}