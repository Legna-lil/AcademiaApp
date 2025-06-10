package com.example.academiaui.feature_db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.academiaui.feature_db.entities.ListItem
import java.time.LocalTime

// 收藏表
@Entity(
    tableName = "star"
)
data class Star (
    @PrimaryKey override val url: String,
    override val title: String,
    override val author: String,
    val starredTime: LocalTime
): ListItem