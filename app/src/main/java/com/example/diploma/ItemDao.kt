package com.example.diploma

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM items")
    fun getAll(): Flow<List<Item>>

    @Insert
    suspend fun insert(item: Item)

    @Delete
    suspend fun delete(item: Item)
}
