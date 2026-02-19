// MainActivity.kt
package com.example.mathalarmclock

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    // Lateinit var for the TimePicker and Buttons
    private lateinit var timePicker: TimePicker
    private lateinit var setAlarmButton: Button
    private lateinit var todoListButton: Button

    override fun onResume() {
        super.onResume()

        val streakText = findViewById<TextView>(R.id.streakText)
        val streak = StreakManager.getStreak(this)

        streakText.text = "🔥 Streak: $streak"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timePicker = findViewById(R.id.timePicker)
        setAlarmButton = findViewById(R.id.setAlarmButton)
        todoListButton = findViewById(R.id.todoListButton)

        setAlarmButton.setOnClickListener {
            setAlarm()
        }
        val deepWorkButton = findViewById<Button>(R.id.btnDeepWork)

        deepWorkButton.setOnClickListener {
            val intent = Intent(this, DeepWorkActivity::class.java)
            startActivity(intent)
        }


        todoListButton.setOnClickListener {
            val intent = Intent(this, TodoActivity::class.java)
            startActivity(intent)
        }
        scheduleDailyCleanup()
    }

    private fun scheduleDailyCleanup() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(this, TaskCleanupReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun setAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // 1. Create a Calendar object for the selected time
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
        calendar.set(Calendar.MINUTE, timePicker.minute)
        calendar.set(Calendar.SECOND, 0)

        // Check if the selected time is in the past, if so, set it for the next day
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        // 2. Create the Intent to fire the AlarmReceiver
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, // Context
            0,    // Request code (unique ID for this alarm)
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )


        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, // Use RTC_WAKEUP to wake the device
            calendar.timeInMillis,   // The time in milliseconds
            pendingIntent
        )

        val timeString = String.format("%02d:%02d", timePicker.hour, timePicker.minute)
        Toast.makeText(this, "Alarm set for $timeString", Toast.LENGTH_LONG).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_my_tasks -> {
                startActivity(Intent(this, MyTaskActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}