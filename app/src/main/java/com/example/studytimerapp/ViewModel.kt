package com.example.studytimerapp

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ViewModel : ViewModel() {


    var totalTimeInMillis by mutableStateOf(0L)
        private set
    var remainingTimeInMillis by mutableStateOf(0L)
        private set
    var isRunning by mutableStateOf(false)
        private set
    private var timerJob: Job? = null

    fun startTimer(context: Context) {
        // Valida se ja esta rodando o timer
        if (isRunning || remainingTimeInMillis <= 0) return

        // Define que o timer ja ta rodando
        isRunning = true

        // Diminui o tempo restante a cada 1 segundo
        timerJob = viewModelScope.launch {
            while (remainingTimeInMillis > 0) {
                delay(1000L)
                remainingTimeInMillis -= 1000L
            }
            // Define que o timer parou
            isRunning = false

            playAlarm(context)



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
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60

        // Formata os minutos e segundos para ter sempre dois dígitos (ex: 05, 12)
        return String.format("%02d:%02d", minutes, seconds)
    }

    // Converte o Inteiro selecionado para mills
    fun onTimeSelected(minutes: Int, seconds: Int) {
        val newTotalTime = (minutes * 60 * 1000L) + (seconds * 1000L)

        totalTimeInMillis = newTotalTime
        remainingTimeInMillis = newTotalTime
    }

    fun playAlarm(context: Context) {
        val alarmUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val ringtone = RingtoneManager.getRingtone(context, alarmUri)
        ringtone.play()

        Handler(Looper.getMainLooper()).postDelayed({
            ringtone?.stop()
        }, 1000L)
    }

}

