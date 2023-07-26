package com.learning.leap.bwb.library.libraryCategory

sealed class LibraryCategoryState{
    data class Categories(val pairs:List<Pair<String,Int>>)
    data class SubCategories(val pairs:List<Pair<String,Int>>)
}
