package com.learning.leap.bwb.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.learning.leap.bwb.model.BabbleActionHistory
import io.reactivex.rxjava3.core.Completable

@Dao
interface ActionHistoryDAO {
    @Insert
    fun insertActionHistory(vararg actionHistory: BabbleActionHistory): Completable

    @Query("Select * from BabbleActionHistory")
    fun getAllActionHistory(): List<BabbleActionHistory>

    @Query("DELETE FROM BabbleActionHistory")
    fun deleteAllActionHistory()
}