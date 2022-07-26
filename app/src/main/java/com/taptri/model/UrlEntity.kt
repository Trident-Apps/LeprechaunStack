package com.taptri.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UrlEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 1,
    val url: String,
    val flag: Boolean
)