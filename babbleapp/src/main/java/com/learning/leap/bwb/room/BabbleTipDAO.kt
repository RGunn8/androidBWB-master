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
    fun getAll(): Single<List<BabbleTip>>

    @Update
    fun updateTip(vararg babbleTips: BabbleTip)

    @Query("DELETE FROM BabbleTip")
    fun deleteAllNotifications()

    @Query("Select * From BabbleTip where category  = :category")
    fun getNotificationForCategory(category: String): Single<List<BabbleTip>>

    @Query("Select * from BabbleTip where subCategory = :subCategory")
    fun getNotificationFromSubCategory(subCategory: String): Single<List<BabbleTip>>


    @Query("SELECT category  FROM BabbleTip GROUP BY category")
    fun getCategories(): Single<List<String>>

    @Query("SELECT subCategory  FROM BabbleTip where category = :category GROUP BY subCategory")
    fun getSubCategories(category: String): Single<List<String>>

    @Query("Select * from BabbleTip where favorite = 1")
    fun getNotificationForFavorites(): Single<List<BabbleTip>>

    @Query("Select * from BabbleTip where playToday = 1")
    fun getNotificationForPlayToday(): Single<List<BabbleTip>>
}