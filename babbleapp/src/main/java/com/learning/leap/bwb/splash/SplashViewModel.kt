package com.learning.leap.bwb.splash

import android.content.SharedPreferences
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learning.leap.bwb.utility.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences
) :ViewModel(){
    val destinationLiveData:MutableLiveData<SplashDestination> by lazy {
        MutableLiveData<SplashDestination>()
    }
    fun displayTimer() {
        viewModelScope.launch {
            object : CountDownTimer(2000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                }

                override fun onFinish() {
                   getDestination()
                }
            }.start()
        }
    }
    fun getDestination(){

        val didDownload = sharedPreferences.getBoolean(Constant.DID_DOWNLOAD, false)
        val needUpdate = sharedPreferences.getBoolean(Constant.UPDATE, false)

         if (didDownload && !needUpdate) {
           destinationLiveData.postValue(SplashDestination.Home)
        } else if (needUpdate) {
            destinationLiveData.postValue(SplashDestination.Download)
        } else {
             destinationLiveData.postValue(SplashDestination.UserSetting)
        }
    }
}