package com.example.weatherapp.models

data class WeatherState(
    val weathers: List<Weather> = emptyList(),
    val latitude: Double= 0.0,
    val longitude: Double = 0.0,
    val date: String= "",
    val temperature: Double= 0.0
)