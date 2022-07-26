package com.taptri.model.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taptri.model.UrlEntity

@Dao
interface UrlDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUrl(url: UrlEntity)

    @Query("SELECT * FROM urlentity LIMIT 1")
    fun getUrl(): LiveData<UrlEntity?>

}