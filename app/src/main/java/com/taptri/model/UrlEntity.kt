package com.taptri.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UrlEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 1,
    var url: String = "null"
)