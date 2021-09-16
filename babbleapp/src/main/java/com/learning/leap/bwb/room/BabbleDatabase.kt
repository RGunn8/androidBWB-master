package com.learning.leap.bwb.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.learning.leap.bwb.model.*

@Database(
    entities = arrayOf(
        BabbleTip::class, BabbleAgeRange::class,
        BabbleUploadHistory::class, BabbleUser::class, BabbleActionHistory::class
    ), version = 1, exportSchema = false
)
abstract class BabbleDatabase : RoomDatabase() {
    abstract fun babbleTipDAO(): BabbleTipDAO
    abstract fun babbleUserDAO(): BabbleUserDAO
    abstract fun actionHistory(): ActionHistoryDAO

    companion object {
        private var INSTANCE: BabbleDatabase? = null
        fun getInstance(context: Context? = null): BabbleDatabase {
            if (INSTANCE == null && context != null) {
                INSTANCE = Room.databaseBuilder(
                    context,
                    BabbleDatabase::class.java,
                    "babbleDB"
                )
                    .build()
            }

            return INSTANCE as BabbleDatabase
        }
    }
}