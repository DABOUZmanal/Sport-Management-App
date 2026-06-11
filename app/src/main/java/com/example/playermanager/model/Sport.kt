package com.example.playermanager.model

import java.io.Serializable

data class Sport(
    val id: Long = -1,
    val name: String,
    val location: String,
    val description: String,
    val openingTime: String,
    val price: Double,
    val imageUri: String? = null
) : Serializable
