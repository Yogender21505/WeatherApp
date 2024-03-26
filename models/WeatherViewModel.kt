package com.example.weatherapp.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeatherViewModel(private val dao: WeatherDao):ViewModel() {
    private val _state = MutableStateFlow(WeatherState())

    fun onEvent(even: WeatherEvent){
        when(even){
            WeatherEvent.SaveWeather -> {
                val latitude = _state.value.latitude
                val longitude=_state.value.longitude
                val temp=_state.value.temperature
                val date=_state.value.date

                if(latitude==0.0 || longitude==0.0 || temp==0.0 || date.isBlank()){
                    return
                }

                val weather = Weather(
                    latitude = latitude,
                    longitude= longitude,
                    date=date,
                    temperature =temp
                )
                viewModelScope.launch { dao.insertWeather(weather) }
            }
        }
    }
//
    fun updateMinMax(date: String,latitude: Double,longitude: Double) {
        viewModelScope.launch {

            val minTemp = dao.getMinTemperatureByDate(date)
            val maxTemp = dao.getMaxTemperatureByDate(date)
        }
    }
}