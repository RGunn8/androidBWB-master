package com.learning.leap.bwb.library

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learning.leap.bwb.model.BabbleTip
import com.learning.leap.bwb.room.BabbleDatabase
import com.learning.leap.bwb.utility.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryScreenViewModel @Inject constructor(
    private val babbleDatabase: BabbleDatabase,
    val dataStore: DataStore<Preferences>
): ViewModel() {
    private val _tipList: MutableStateFlow<List<BabbleTip>> = MutableStateFlow(listOf())
    val tipList: StateFlow<List<BabbleTip>> = _tipList

    private val _babyName: MutableStateFlow<String> = MutableStateFlow("")
    val babyName: StateFlow<String> = _babyName


    init {
        viewModelScope.launch {
            dataStore.data.collectLatest {
                _babyName.value = it[stringPreferencesKey(Constant.BABY_NAME)] ?: ""
            }
        }
    }

    fun getTips(screenType: LibraryScreenType,categoryName:String){
        viewModelScope.launch {
            when (screenType) {
                LibraryScreenType.ALLTIPS -> {
                    val allTips = babbleDatabase.babbleTipDAO().getAll()
                    _tipList.value = allTips.shuffled()
                }
                LibraryScreenType.ALLSUBCATEGORY -> {
                    val allSubCategoryTips = babbleDatabase.babbleTipDAO().getTipsForCategory(categoryName)
                    _tipList.value = allSubCategoryTips.shuffled()
                }
                LibraryScreenType.SUBCATEGORY -> {
                    val subCategoryTips = babbleDatabase.babbleTipDAO().getTipsFromSubCategory(categoryName)
                    _tipList.value = subCategoryTips.shuffled()
                }

                LibraryScreenType.FAVORITE -> {
                    val allTips = babbleDatabase.babbleTipDAO().getTipsForFavorites()
                    _tipList.value = allTips.shuffled()
                }
            }
        }
    }

    fun updateTipFavorite(tip: BabbleTip, isFavorite:Boolean){
        viewModelScope.launch {
            val updateTip = tip.copy(favorite = isFavorite)
            babbleDatabase.babbleTipDAO().updateTip(updateTip)
        }
    }

}