package com.example.weatherapp.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.widget.ContentLoadingProgressBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class WeatherRepository(private val weatherDao:WeatherDao) {

    suspend fun getWeatherData( latitude: Double, longitude: Double,date: MutableState<String>,loadingProgress:MutableState<Float>,isLoading: MutableState<Boolean>): Flow<List<Weather>> {
        var flag= mutableStateOf(false)
        var weatherList = mutableStateOf(fetchWeatherDataFromApi(latitude,longitude,date,flag))
        if(weatherDao.getWeatherByDate(date.value)==null && weatherList.value==null){
            flag.value=true
            weatherList.value = fetchWeatherDataFromApi(latitude,longitude,date,flag)
        }
        if(weatherDao.getWeatherByDate(date.value)==null && weatherList.value!=null){
            val totalItems = weatherList.value.size // Calculate the total number of items
            parseAndInsertWeatherData(latitude,longitude,weatherList.value, weatherDao,) { progress ->
                // Update the loading progress here
                // For example, you can update a mutableState variable representing the loading progress
                loadingProgress.value = progress
            }
            if(loadingProgress.value==totalItems.toFloat()){
                isLoading.value=false
            }
        }
        return weatherDao.getWeatherByDate(date.value);
    }
    private suspend fun fetchWeatherDataFromApi(
        latitude: Double,
        longitude: Double,
        date: MutableState<String>,
        flag: MutableState<Boolean>
    ):  List<Pair<String,Double>> {

        var apiUrl = "https://archive-api.open-meteo.com/v1/era5?latitude=$latitude&longitude=$longitude&start_date=${date.value}&end_date=${date.value}&hourly=temperature_2m"
        if(flag.value){
            apiUrl="https://archive-api.open-meteo.com/v1/era5?latitude=$latitude&longitude=$longitude&start_date=2010-01-01&end_date=2020-01-01&hourly=temperature_2m"
        }
        val url = URL(apiUrl)
        val connection = url.openConnection() as HttpsURLConnection
        connection.requestMethod = "GET"
        connection.connect()
        return withContext(Dispatchers.IO) {
            val WeatherList = mutableListOf<Pair<String, Double>>()

            val jsonResponse = connection.inputStream.bufferedReader().use {
                it.readText()
            }
            val jsonObject = JSONObject(jsonResponse)
            val hourlyData = jsonObject.getJSONObject("hourly")
            val timeArray = hourlyData.getJSONArray("time")
            val temperatureArray = hourlyData.getJSONArray("temperature_2m")
            for (i in 0 until timeArray.length()) {
                val time = timeArray.getString(i).substring(0, 10)
                val temperature = temperatureArray.getDouble(i)
                val value = Pair(time, temperature)
                WeatherList.add(value)
            }
            WeatherList
        }
    }
    private suspend fun parseAndInsertWeatherData(
        latitude: Double,
        longitude: Double,
        weatherData: List<Pair<String, Double>>,
        dao: WeatherDao,
        updateProgress: (Float) -> Unit // Callback function to update loading progress
    ) {
        withContext(Dispatchers.IO) {
            val totalItems = weatherData.size // Total number of items to be processed
            var processedItems = 0 // Counter to keep track of processed items
            weatherData.forEach { (time, temperature) ->

                val weather = Weather(
                    latitude = latitude,
                    longitude = longitude,
                    date = time,
                    temperature = temperature
                )
                println(time + " " + temperature)
                dao.insertWeather(weather)

                processedItems++ // Increment the counter for processed items

                // Calculate the progress as a percentage
                val progress = processedItems.toFloat() / totalItems
                updateProgress(progress) // Update the loading progress
            }
        }
    }
}