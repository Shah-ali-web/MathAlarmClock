package com.example.mathalarmclock

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.Calendar

class AddTaskActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }

        val taskEdit = findViewById<EditText>(R.id.taskEditText)
        val timePicker = findViewById<TimePicker>(R.id.timePicker)
        val saveBtn = findViewById<Button>(R.id.saveTaskButton)

        saveBtn.setOnClickListener {
            val task = taskEdit.text.toString()
            val hour = timePicker.hour
            val minute = timePicker.minute
            val time = String.format("%02d:%02d", hour, minute)

            if (task.isEmpty()) {
                Toast.makeText(this, "Please enter a task", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val taskDescription = "$task ($time)"
            saveTask(taskDescription)
            
            // 🔥 Fixed: Call the notification scheduler here
            scheduleTaskNotification(task, hour, minute)

            Toast.makeText(this, "Task Added", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun scheduleTaskNotification(
        taskName: String,
        hour: Int,
        minute: Int
    ) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(this, TaskNotificationReceiver::class.java)
        intent.putExtra("taskName", taskName)

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            System.currentTimeMillis().toInt(), // Unique request code
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)

        // 🔥 IMPORTANT: FUTURE TIME CHECK
        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        // Use setExactAndAllowWhileIdle for more reliability
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    private fun saveTask(taskText: String) {
        val prefs = getSharedPreferences("TASK_PREFS", Context.MODE_PRIVATE)
        val existingTasks = prefs.getStringSet("TASKS", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        existingTasks.add(taskText)
        prefs.edit().putStringSet("TASKS", existingTasks).apply()
    }
}
