package com.example.academiaui.feature_db.entities

sealed interface ListItem {
    val url: String
    val title: String
    val author: String
}