package com.example.studytimerapp

import android.os.Bundle
import android.widget.NumberPicker
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.studytimerapp.ui.theme.StudyTimerAppTheme

class MainActivity : ComponentActivity() {
    private val viewModel: ViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val navController = rememberNavController()

            NavHost(navController, startDestination = "timer") {
                composable("timer") {
                    TimerScreen(
                        viewModel = viewModel, onNavigateToSetTime = {
                            navController.navigate("setTimer")
                        })
                }
                composable("setTimer") {
                    SetTimerScreen(
                        onTimeSelected = { min, sec -> viewModel.onTimeSelected(min, sec) },
                        onConfirm = { navController.popBackStack() })
                }
            }
        }
    }
}


@Composable
fun TimerScreen(
    viewModel: ViewModel, onNavigateToSetTime: () -> Unit
) {
    val isRunning = viewModel.isRunning
    val remainingTime = viewModel.remainingTimeInMillis
    val formattedTime = viewModel.formatTime(remainingTime)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Tempo formatado
        Text(
            text = formattedTime, fontSize = 48.sp, fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botões de controle
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            val context = LocalContext.current
            Button(
                onClick = { viewModel.startTimer(context) }, enabled = !isRunning
            ) {
                Text("Start")
            }

            Button(
                onClick = { viewModel.stopTimer() }, enabled = isRunning
            ) {
                Text("Stop")
            }

            Button(
                onClick = { viewModel.resetTimer() }) {
                Text("Reset")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            onNavigateToSetTime()
        }) {
            Text("Selecionar Tempo")
        }
    }
}


@Composable
fun SetTimerScreen(
    onTimeSelected: (Int, Int) -> Unit, onConfirm: () -> Unit
) {
    var selectedMinutes by remember { mutableStateOf(0) }
    var selectedSeconds by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Selecione o tempo", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Minutos
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Min", fontSize = 16.sp)
                NumberPickerView(
                    selectedMinutes, 0..60, onValueChange = { selectedMinutes = it })
            }


            // Segundos
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Seg", fontSize = 16.sp)
                NumberPickerView(
                    selectedSeconds, 0..60, onValueChange = { selectedSeconds = it })
            }
        }


        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            onTimeSelected(selectedMinutes, selectedSeconds)
            onConfirm()
        }) {
            Text("Confirmar")
        }
    }
}


@Composable
fun NumberPickerView(
    value: Int, range: IntRange, onValueChange: (Int) -> Unit
) {
    AndroidView(factory = { context ->
        NumberPicker(context).apply {
            minValue = range.first
            maxValue = range.last
            this.value = value
            wrapSelectorWheel = true
            setOnValueChangedListener { _, _, newVal ->
                onValueChange(newVal)
            }
        }
    }, update = {
        if (it.value != value) {
            it.value = value
        }
    })
}





