package com.learning.leap.bwb.library.libraryCategory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learning.leap.bwb.room.BabbleDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryCategoryViewModel @Inject constructor(
    private val babbleDatabase: BabbleDatabase
): ViewModel() {
    private val _pairsStateFlow: MutableStateFlow<List<Pair<String, Int>>> =
        MutableStateFlow(listOf())
    val pairsStateFlow: StateFlow<List<Pair<String, Int>>> = _pairsStateFlow

    fun getLibraryCategories(){
        viewModelScope.launch {
           val categoryList =  babbleDatabase.babbleTipDAO().getAll()
               val categoryPairs = categoryList.groupBy {
                   it.category
               }.filter {
                   it.key.isNotEmpty()
               }.map {
                       Pair(it.key, it.value.size)

               }
           val favoriteTips =  babbleDatabase.babbleTipDAO().getTipsForFavorites()
            val allCategoryPair = categoryPairs.toMutableList()
            allCategoryPair.add(0,Pair("All", categoryList.size))
            if (favoriteTips.isNotEmpty()){
                allCategoryPair.add(1,Pair("Favorites", favoriteTips.size))
            }
            _pairsStateFlow.value = allCategoryPair.toList()
        }
    }

    fun getLibrarySubCategories(category:String){
        viewModelScope.launch {
            val subCategoryList = babbleDatabase.babbleTipDAO().getTipsForCategory(category)
            val subCategoryPairs = subCategoryList.groupBy {
            it.subCategory
        }.filter {
                it.key.isNotEmpty()
            }.map {
                Pair(it.key, it.value.size)

        }
            val allSubCategoryPairs = subCategoryPairs.toMutableList()
            allSubCategoryPairs.add(0, Pair("All",subCategoryList.size))
            _pairsStateFlow.value = allSubCategoryPairs
        }
    }
}