package com.example.weatherapp.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class WeatherRepository(private val weatherDao:WeatherDao) {
    suspend fun getMinMax(
        date: String,
        latitude: Double,
        longitude: Double,
        min: MutableState<Double>,
        max: MutableState<Double>
    ) {
        withContext(Dispatchers.IO) {
            if(date>"2022-01-01"){
                min.value = -1.0
                max.value = -1.0
            }
            else{
                val minTemp = weatherDao.getMinTemperatureByDate(date)
                println(minTemp)
                val maxTemp = weatherDao.getMaxTemperatureByDate(date)
                min.value = minTemp
                max.value = maxTemp
            }


        }
    }
    suspend fun getWeatherData(
        date: String,
        latitude: Double,
        longitude: Double,
        min: MutableState<Double>,
        max: MutableState<Double>,
        loadingProgress: MutableState<Float>,
        isLoading: MutableState<Boolean>
    ) {

        isLoading.value = true
        var weatherList = mutableStateOf<List<Pair<String, Double>>>(emptyList())
        var apiUrl = ""

        if (weatherDao.getWeatherByDate(date) == null) {
            apiUrl = if (date > "2022-01-01") {
                "https://archive-api.open-meteo.com/v1/era5?latitude=$latitude&longitude=$longitude&start_date=2010-01-01&end_date=2020-01-01&hourly=temperature_2m"
            } else {
                "https://archive-api.open-meteo.com/v1/era5?latitude=$latitude&longitude=$longitude&start_date=$date&end_date=$date&hourly=temperature_2m"
            }

            weatherList.value = fetchWeatherDataFromApi(apiUrl)
        }
        println(weatherList)
        val totalItems = weatherList.value.size // Calculate the total number of items
        parseAndInsertWeatherData(latitude,longitude,weatherList.value, weatherDao,) { progress ->
            // Update the loading progress here
            // For example, you can update a mutableState variable representing the loading progress
            loadingProgress.value = progress

            if(loadingProgress.value==totalItems.toFloat()){
                isLoading.value=false
            }
        }

        getMinMax(date,latitude,longitude,min, max)
        println(min)
    }
    private suspend fun fetchWeatherDataFromApi(
        api: String
    ): List<Pair<String, Double>> {
        return withContext(Dispatchers.IO) {
            var apiUrl = api
            val url = URL(apiUrl)
            val connection = url.openConnection() as HttpsURLConnection
            connection.requestMethod = "GET"
            connection.connect()

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