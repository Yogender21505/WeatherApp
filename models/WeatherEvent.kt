package com.example.weatherapp.models

import java.sql.Time

sealed interface WeatherEvent {
    object SaveWeather: WeatherEvent
//    data class setLatitude(val latitude: Double):WeatherEvent
//    data class setLongitude(val longitude: Double):WeatherEvent
//    data class setTime(val time: String):WeatherEvent
//    data class setTemp(val temp: Double): WeatherEvent
}
//val latitude: Double,
//val longitude: Double,
//val time: String,
//val temperature: Double