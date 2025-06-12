package com.example.academiaui.feature_db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "download"
)
data class Download(
    @PrimaryKey override val url: String,
    override val title: String,
    override val author: String,
    val uri: String,
    val updatedTime: Date,
    val downloadTime: Date
): ListItem