package com.example.mathalarmclock

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class OverallProgressActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overall_progress)

        val barChart = findViewById<BarChart>(R.id.barChart)
        loadWeeklyProgress(barChart)
    }

    private fun loadWeeklyProgress(barChart: BarChart) {

        val prefs = getSharedPreferences("ProgressPrefs", MODE_PRIVATE)
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        val calendar = Calendar.getInstance()

        // Move calendar to SUNDAY of current week
        calendar.firstDayOfWeek = Calendar.SUNDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

        val keyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val labelFormat = SimpleDateFormat("EEE", Locale.getDefault())

        for (i in 0..6) {
            val dateKey = keyFormat.format(calendar.time)
            val progress = prefs.getInt(dateKey, 0)

            entries.add(BarEntry(i.toFloat(), progress.toFloat()))
            labels.add(labelFormat.format(calendar.time))

            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val dataSet = BarDataSet(entries, "Weekly Progress (%)")
        dataSet.color = ContextCompat.getColor(this, android.R.color.holo_green_dark)
        dataSet.valueTextSize = 12f

        val data = BarData(dataSet)
        data.barWidth = 0.9f

        barChart.data = data
        
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.labelCount = 7
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)

        barChart.setFitBars(true)
        barChart.description.isEnabled = false
        barChart.animateY(800)
        barChart.invalidate()
    }
}