// MainActivity.kt
package com.example.mathalarmclock

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    override fun onResume() {
        super.onResume()

        // Streak text exists only if present in layout
        val streakText = findViewById<TextView?>(R.id.tvStreak)
        val streak = StreakManager.getStreak(this)
        streakText?.text = "🔥 Streak: $streak"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Backend logic – untouched
        scheduleDailyCleanup()

        // Bottom Navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_home -> {
                    true // already on home
                }

                R.id.nav_alarm -> {
                    startActivity(Intent(this, AlarmActivity::class.java))
                    true
                }

                R.id.nav_todo -> {
                    startActivity(Intent(this, TodoActivity::class.java))
                    true
                }

                R.id.nav_deepwork -> {
                    startActivity(Intent(this, DeepWorkActivity::class.java))
                    true
                }

                else -> false
            }
        }
    }

    // Backend cleanup logic (safe)
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
