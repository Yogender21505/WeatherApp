package com.example.weatherapp
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.weatherapp.models.WeatherDao
import com.example.weatherapp.models.WeatherDatabase
import com.example.weatherapp.models.WeatherRepository
import com.example.weatherapp.models.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Retrofit

class MainActivity : ComponentActivity() {

    private val permissionRequestCode = 123
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val db by lazy{
        Room.databaseBuilder(
            applicationContext,
            WeatherDatabase::class.java,
            "weathers.db"
        ).build()
    }

    private val viewModel by viewModels<WeatherViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory{
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return WeatherViewModel(db.dao) as T
                }
            }
        }
    )
    @SuppressLint("UnrememberedMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestLocationPermission()

        setContent {
            WeatherScreen(viewModel,latitude.value,longitude.value)
        }

    }
//    WeatherScreen(date,weatherViewModel,isLoading=isLoading,loadingProgress) { submittedDate ->
//        date.value = submittedDate.toString() // Update the date when submitted // Fetch weather data with the new date
//    }
    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                permissionRequestCode
            )
        } else {
            getLocation()
        }
    }
    val latitude = mutableStateOf(0.0)
    val longitude = mutableStateOf(0.0)
    @SuppressLint("MissingPermission")
    private fun getLocation() {

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    // You have the location, now you can fetch weather data
                    latitude.value = location.latitude
                    longitude.value = location.longitude
//                    fetchWeatherData(latitude.value, longitude.value)
                } else {
                    // Handle case where location is null
                }
            }
            .addOnFailureListener { exception ->
                // Handle failure to get location
            }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            } else {
                // Permission denied, handle accordingly
            }
        }
    }
//    private fun fetchWeatherDataAndUpdateUI(
//        minTemperature: MutableState<Double>,
//        maxTemperature: MutableState<Double>,
//        date: MutableState<String>
//    ) {
//        lifecycleScope.launch {
//            try {
//                val maxTemp = withContext(Dispatchers.IO) {
//                    weatherDao.getMaxTemperatureByDate(date.value)
//                }
//                val minTemp = withContext(Dispatchers.IO) {
//                    weatherDao.getMinTemperatureByDate(date.value)
//                }
//                // Update UI on the main thread
//                withContext(Dispatchers.Main) {
//                    maxTemperature.value = maxTemp
//                    minTemperature.value = minTemp
//                }
//            } catch (e: Exception) {
//                // Handle exceptions here
//            }
//        }
//    }



//    private fun fetchWeatherData(latitude: Double, longitude: Double) {
//        isLoading = true // Start loading
//        lifecycleScope.launch {
//            try {
//                val weatherData = withContext(Dispatchers.IO) {
//                    fetchWeatherDataFromApi(latitude, longitude,date)
//                }
//                if (weatherData.isEmpty()) {
//                    if (weatherDao.getAllWeather() == null) {
//                        //toast that internet required for downloading data from API
//                    }
//                } else {
//                    val totalItems = weatherData.size // Calculate the total number of items
//                    parseAndInsertWeatherData(weatherData, weatherDao,) { progress ->
//                        // Update the loading progress here
//                        // For example, you can update a mutableState variable representing the loading progress
//                        loadingProgress = progress
//                    }
//
////                    println(weatherDao.getAllWeather().toString())
//                }
//            } catch (e: Exception) {
//                // Handle exceptions here
//            }
//            finally {
//                isLoading=false
//            }
//        }
//    }
//
//    private suspend fun parseAndInsertWeatherData(
//        weatherData: List<Pair<String, Double>>,
//        dao: WeatherDao,
//        updateProgress: (Float) -> Unit // Callback function to update loading progress
//    ) {
//        withContext(Dispatchers.IO) {
//            val totalItems = weatherData.size // Total number of items to be processed
//            var processedItems = 0 // Counter to keep track of processed items
//            weatherData.forEach { (time, temperature) ->
//
//                val weather = Weather(
//                    latitude = latitude.value,
//                    longitude = longitude.value,
//                    date = time,
//                    temperature = temperature
//                )
//                println(time + " " + temperature)
//                dao.insertWeather(weather)
//
//                processedItems++ // Increment the counter for processed items
//
//                // Calculate the progress as a percentage
//                val progress = processedItems.toFloat() / totalItems
//                updateProgress(progress) // Update the loading progress
//            }
//        }
//    }
//
//
//
//
//    private val TIME_KEY = "time"
//    private val TEMPERATURE_KEY = "temperature_2m"
//
//
//
//    private suspend fun fetchWeatherDataFromApi(
//        latitude: Double,
//        longitude: Double,
//        date: MutableState<String>
//    ):  List<Pair<String,Double>> {
//        val apiUrl = "https://archive-api.open-meteo.com/v1/era5?latitude=$latitude&longitude=$longitude&start_date=${date.value}&end_date=${date.value}&hourly=temperature_2m"
//        val url = URL(apiUrl)
//        val connection = url.openConnection() as HttpsURLConnection
//        connection.requestMethod = "GET"
//        connection.connect()
//        return withContext(Dispatchers.IO) {
//            val WeatherList = mutableListOf<Pair<String, Double>>()
//
//            val jsonResponse = connection.inputStream.bufferedReader().use {
//                it.readText()
//            }
//            val jsonObject = JSONObject(jsonResponse)
//            val hourlyData = jsonObject.getJSONObject("hourly")
//            val timeArray = hourlyData.getJSONArray(TIME_KEY)
//            val temperatureArray = hourlyData.getJSONArray(TEMPERATURE_KEY)
//            for (i in 0 until timeArray.length()) {
//                val time = timeArray.getString(i).substring(0, 10)
//                val temperature = temperatureArray.getDouble(i)
//                val value = Pair(time, temperature)
//                WeatherList.add(value)
//            }
//            WeatherList
//        }
//    }


}
