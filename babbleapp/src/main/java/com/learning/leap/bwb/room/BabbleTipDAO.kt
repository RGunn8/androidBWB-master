package com.learning.leap.bwb.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.learning.leap.bwb.model.BabbleTip
import io.reactivex.rxjava3.core.Single

@Dao
interface BabbleTipDAO {
    @Insert
    fun insertAll(notifications: List<BabbleTip>)

    @Query("Select * from BabbleTip")
    suspend fun getAll(): List<BabbleTip>

    @Update
    suspend fun updateTip(vararg babbleTips: BabbleTip)

    @Query("DELETE FROM BabbleTip")
    suspend fun deleteAllTips()

    @Query("Select * From BabbleTip where category  = :category")
    suspend fun getTipsForCategory(category: String): List<BabbleTip>

    @Query("Select * from BabbleTip where subCategory = :subCategory")
    suspend fun getTipsFromSubCategory(subCategory: String): List<BabbleTip>


    @Query("SELECT category  FROM BabbleTip GROUP BY category")
    suspend fun getCategories(): List<String>

    @Query("SELECT subCategory  FROM BabbleTip where category = :category GROUP BY subCategory")
    suspend fun getSubCategories(category: String): List<String>

    @Query("Select * from BabbleTip where favorite = 1")
    suspend fun getTipsForFavorites(): List<BabbleTip>

    @Query("Select * from BabbleTip where playToday = 1")
    suspend fun getTipsForPlayToday(): List<BabbleTip>
}