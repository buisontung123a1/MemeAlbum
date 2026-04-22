package com.memealbum.app.data.model

data class MemeItem(
    val id: String,
    val title: String,
    val imageUrl: String,
    val author: String,
    val score: Int,
    val createdUtc: Long,
    val permalink: String,
    var isSelected: Boolean = false
)
