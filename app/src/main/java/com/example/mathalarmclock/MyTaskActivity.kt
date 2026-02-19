package com.example.mathalarmclock

import android.animation.ObjectAnimator
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Paint
import android.os.Bundle
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MyTaskActivity : AppCompatActivity() {

    private lateinit var tasksContainer: LinearLayout
    private lateinit var taskList: MutableList<String>
    private lateinit var prefs: SharedPreferences
    private lateinit var progressBar: ProgressBar
    private lateinit var progressPercent: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_task)
        progressBar = findViewById(R.id.progressBar)
        progressPercent = findViewById(R.id.progressPercent)
        tasksContainer = findViewById(R.id.tasksContainer)
        
        prefs = getSharedPreferences("TASK_PREFS", Context.MODE_PRIVATE)

        loadTasks()
        displayTasks()
        updateProgress()
    }

    private fun loadTasks() {
        val set = prefs.getStringSet("TASKS", mutableSetOf())
        taskList = set?.toMutableList() ?: mutableListOf()
    }

    private fun displayTasks() {
        tasksContainer.removeAllViews()
        
        // We use a temporary list of indices to avoid modification issues while iterating
        for (i in taskList.indices) {
            val taskRaw = taskList[i]
            val taskView = layoutInflater.inflate(R.layout.task_row, tasksContainer, false)

            val taskText = taskView.findViewById<TextView>(R.id.taskText)
            val checkBox = taskView.findViewById<CheckBox>(R.id.checkTask)
            val deleteButton = taskView.findViewById<ImageButton>(R.id.deleteTask)

            // Parse "Task Name|true/false"
            val parts = taskRaw.split("|")
            val name = parts[0]
            val isDone = if (parts.size > 1) parts[1].toBoolean() else false

            taskText.text = name
            checkBox.isChecked = isDone
            if (isDone) {
                taskText.paintFlags = taskText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                // Update completion status in the list
                taskList[i] = "$name|$isChecked"
                
                if (isChecked) {
                    taskText.paintFlags = taskText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    taskText.paintFlags = taskText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
                
                saveTasks()
                updateProgress()
            }

            deleteButton.setOnClickListener {
                taskList.removeAt(i)
                saveTasks()
                displayTasks() // Refresh list to update indices
                updateProgress()
            }

            tasksContainer.addView(taskView)
        }
    }

    private fun saveTasks() {
        prefs.edit().putStringSet("TASKS", taskList.toSet()).apply()
    }

    private fun updateProgress() {
        if (taskList.isEmpty()) {
            animateProgress(0)
            progressPercent.text = "0%"
            return
        }

        var completedCount = 0
        for (task in taskList) {
            val parts = task.split("|")
            if (parts.size > 1 && parts[1].toBoolean()) {
                completedCount++
            }
        }

        val progress = (completedCount * 100) / taskList.size
        animateProgress(progress)
        progressPercent.text = "$progress%"
        
        // Save the current progress to ProgressManager for OverallProgressActivity
        ProgressManager.saveDailyProgress(this, progress)
    }

    private fun animateProgress(targetProgress: Int) {
        val animator = ObjectAnimator.ofInt(
            progressBar,
            "progress",
            progressBar.progress,
            targetProgress
        )
        animator.duration = 600
        animator.start()
    }
}
