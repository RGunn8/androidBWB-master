package com.learning.leap.bwb.library

sealed class LibraryScreenType{
    object AllTips : LibraryScreenType()

    data class AllSubCategory(val categoryName:String) : LibraryScreenType()

    data class SubCategory(val categoryName: String) : LibraryScreenType()
}
