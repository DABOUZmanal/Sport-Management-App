package com.example.playermanager.model

import java.io.Serializable

data class Player(
    val id: Long = -1,
    val fullName: String,
    val phone: String,
    val email: String,
    val ntrpLevel: String,
    val sportType: String,
    val joinDate: String,
    val membershipType: String,
    val photoUri: String? = null,
    val expiryDate: String = "",
    val coachName: String = ""
) : Serializable
