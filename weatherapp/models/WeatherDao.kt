package com.example.weatherapp.models

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow


@Dao
interface WeatherDao {
    @Upsert
    fun insertWeather(weather: Weather)

    @Query("SELECT * FROM weather")
    fun getAllWeather(): Flow<List<Weather>>

    @Query("SELECT * FROM weather WHERE date=:date")
    fun getWeatherByDate(date:String): Flow<List<Weather>>

    @Query("SELECT MIN(temperature) AS minTemperature FROM weather WHERE SUBSTR(date, 1, 10) = :date GROUP BY date")
    fun getMinTemperatureByDate(date: String): Double

    @Query("SELECT MAX(temperature) AS minTemperature FROM weather WHERE SUBSTR(date, 1, 10) = :date GROUP BY date")
    fun getMaxTemperatureByDate(date: String): Double

}
