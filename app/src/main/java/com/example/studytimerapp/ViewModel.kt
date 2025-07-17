package com.example.studytimerapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ViewModel : ViewModel() {

    // Tempo total definido (em milissegundos)
    var totalTimeInMillis by mutableStateOf(0L)
        private set

    // Tempo restante (em milissegundos)
    var remainingTimeInMillis by mutableStateOf(0L)
        private set

    // Estado de execução do timer (ligado/desligado)
    var isRunning by mutableStateOf(false)
        private set

    private var timerJob: Job? = null

    fun startTimer() {
        // Valida se ja esta rodando o timer
        if (isRunning || remainingTimeInMillis <= 0) return

        // Define que o timer ja ta rodando
        isRunning = true

        // Diminui o tempo restante a cada 1 segundo
        timerJob = viewModelScope.launch {
            while (remainingTimeInMillis > 0) {
                delay(1000L)
                remainingTimeInMillis - 1000L
            }
            // Define que o timer parou
            isRunning = false
        }
    }

    fun stopTimer() {
        timerJob?.cancel()        // Cancela a coroutine
        timerJob = null           // Limpa o Job
        isRunning = false         // Atualiza o estado
    }

    fun resetTimer() {
        stopTimer()
        remainingTimeInMillis = totalTimeInMillis
    }

    fun formatTime(millis: Long): String {
        return ""
    }

    fun onTimeSelected() {}


}

