package com.example.weatherapp

//import androidx.compose.foundation.layout.BoxScopeInstance.align
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.RowScopeInstance.align
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.models.WeatherViewModel

@SuppressLint("UnrememberedMutableState")
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    latitude: Double,
    longitude: Double
) {
//    var MINtemp = mutableStateOf(0.0)
//    var MAXtemp =mutableStateOf(0.0)
//    var loadingProgress =mutableStateOf(0f)
//    val isLoading =  mutableStateOf(false)
    var dateText by remember{mutableStateOf("2025-01-01")}
    Column {
        // TextField for entering the date
        if (viewModel.isLoading.value) {
            LinearProgress(viewModel.loadingProgress.value, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        else {

            TextField(
                value = dateText,
                onValueChange = { dateText = it },
                label = { Text("Enter Date") },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )

            // Button to submit the selected date
            Button(
                onClick = {
                          viewModel.updateMinMax(dateText,latitude,longitude)
                    viewModel.isLoading.value=false
                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            ) {
                Text(text = "Submit")
            }

            // Display loading progress if isLoading is true

            // Display minimum and maximum temperatures
//            println("min" + MINtemp.value)
            LazyColumn {
                item {
                    ElevatedCardExample(name = "Min",viewModel.MINtemp.value)
                }
                item {
                    ElevatedCardExample(name = "Max",viewModel.MAXtemp.value)
                }
            }
        }
    }
}

@Composable
fun ElevatedCardExample(name: String, temp:Double) {
    ElevatedCard(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(100.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row {
            Text(
                text = "$temp",
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp
            )

            Text(
                text = name,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
    }
}
@Composable
fun LinearProgress(progress: Float, modifier: Modifier) {
    Row(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LinearProgressIndicator(
            modifier = Modifier
                .size(40.dp)
                .width(10.dp),
            progress = progress
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "${(progress * 100).toInt()}%",
            style = TextStyle(textAlign = TextAlign.Center),
            modifier = Modifier.wrapContentSize()
        )
    }
}