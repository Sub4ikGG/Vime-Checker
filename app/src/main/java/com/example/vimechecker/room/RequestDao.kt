package com.example.vimechecker.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RequestDao {
    @Query("SELECT * FROM player_table")
    fun readAllData(): LiveData<List<Request>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPlayer(vararg request: Request)

    @Delete
    suspend fun deletePlayer(request: Request)

    @Query("SELECT * FROM player_table WHERE name LIKE :name AND type LIKE :type")
    fun getPlayer(name: String, type: String): Request?

    @Query("DELETE FROM player_table")
    fun dropTable()
}