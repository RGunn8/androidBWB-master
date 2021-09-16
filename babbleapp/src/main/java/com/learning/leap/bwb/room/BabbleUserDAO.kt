package com.learning.leap.bwb.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.learning.leap.bwb.model.BabbleUser

@Dao
interface BabbleUserDAO {
    @Query("DELETE FROM BabbleUser")
    fun deleteUser()

    @Insert
    fun addUser(vararg user: BabbleUser)
}