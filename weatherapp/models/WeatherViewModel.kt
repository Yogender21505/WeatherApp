package com.example.weatherapp.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class WeatherViewModel(private val dao: WeatherDao):ViewModel() {
    //
    var MINtemp = mutableStateOf(0.0)
    var MAXtemp = mutableStateOf(0.0)
    var loadingProgress =mutableStateOf(0f)
    val isLoading =  mutableStateOf(false)
    fun updateMinMax(
        date: String,
        latitude: Double,
        longitude: Double,
    ) {
        viewModelScope.launch {
            val repository = WeatherRepository(dao)
            repository.getWeatherData(date,latitude,longitude,MINtemp,MAXtemp,loadingProgress,isLoading)
//            println("min view "+ min.value)
//            MINtemp.value=min.value
//            MAXtemp.value=max.value
        }
    }
}