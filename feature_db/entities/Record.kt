package com.example.academiaui.feature_db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "record"
)
data class Record (
    @PrimaryKey override val url: String,
    override val title: String,
    override val author: String,
    val viewedTime: Date
): ListItem
