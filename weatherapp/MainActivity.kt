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
    val latitude = mutableStateOf(0.0)
    val longitude = mutableStateOf(0.0)

    @SuppressLint("UnrememberedMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestLocationPermission()

        setContent {
            println(latitude.value)
            WeatherScreen(viewModel,latitude.value,longitude.value)
        }

    }
    private fun requestLocationPermission() {
        println("lati"+ latitude.value)
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
    @SuppressLint("MissingPermission")
    private fun getLocation() {

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    // You have the location, now you can fetch weather data
                    latitude.value = location.latitude
                    longitude.value = location.longitude
                    println("lati"+ latitude.value)
//                    fetchWeatherData(latitude.value, longitude.value)
                } else {
                    // Handle case where location is null
                }
            }
            .addOnFailureListener { exception ->
                // Handle failure to get location
                println("lati"+ latitude.value)
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
                println("lati"+ latitude.value)
            }
        }
    }

}