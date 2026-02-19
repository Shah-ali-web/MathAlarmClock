package com.example.mathalarmclock

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DeepWorkActivity : AppCompatActivity() {

    private lateinit var timerText: TextView
    private lateinit var startButton: Button

    private var sessionRunning = false
    private var timer: CountDownTimer? = null

    private val focusTimeMillis = 25 * 60 * 1000L // 25 minutes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deep_work)

        timerText = findViewById(R.id.timer_Text)
        startButton = findViewById(R.id.start_Button)

        startButton.setOnClickListener {
            startFocusSession()
        }
    }

    private fun startFocusSession() {
        sessionRunning = true
        startButton.isEnabled = false

        timer = object : CountDownTimer(focusTimeMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 60000
                val seconds = (millisUntilFinished % 60000) / 1000
                timerText.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                sessionRunning = false
                rewardUser()
            }
        }.start()
    }

    private fun rewardUser() {
        val prefs = getSharedPreferences("FOCUS_PREF", MODE_PRIVATE)
        val fuel = prefs.getInt("FUEL", 0) + 10
        prefs.edit().putInt("FUEL", fuel).apply()

        Toast.makeText(this, "Deep Work Complete! +10 Fuel 🔥", Toast.LENGTH_LONG).show()
        finish()
    }

    private fun failSession() {
        timer?.cancel()
        sessionRunning = false
        Toast.makeText(this, "Focus broken! Session failed ❌", Toast.LENGTH_LONG).show()
        finish()
    }

    override fun onPause() {
        super.onPause()
        if (sessionRunning) {
            failSession()
        }
    }
}
