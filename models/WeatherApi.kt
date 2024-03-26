package com.example.weatherapp.models

import retrofit2.http.GET
import retrofit2.http.Query
import kotlinx.coroutines.flow.Flow

interface WeatherApi {
    @GET("weather")
    suspend fun getWeather(@Query("date") date: String): List<WeatherData>
}