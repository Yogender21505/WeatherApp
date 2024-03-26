package com.example.weatherapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Weather(
    val latitude: Double,
    val longitude: Double,
    val date: String,
    val temperature: Double,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
