package com.learning.leap.bwb.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.learning.leap.bwb.model.BabbleUser

@Dao
interface BabbleUserDAO {
    @Query("DELETE FROM BabbleUser")
    fun deleteUser()

    @Insert
    fun addUser(vararg user: BabbleUser)

    @Query("SELECT * FROM BabbleUser LIMIT 1")
    fun getUser() : BabbleUser

    @Update(entity = BabbleUser::class)
    fun update(obj: BabbleUser)
}