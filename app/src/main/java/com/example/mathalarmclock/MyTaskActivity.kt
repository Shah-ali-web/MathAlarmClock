package com.example.mathalarmclock

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Paint
import android.os.Bundle
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MyTaskActivity : AppCompatActivity() {

    private lateinit var tasksContainer: LinearLayout
    private lateinit var taskList: MutableList<String>
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_task)

        tasksContainer = findViewById(R.id.tasksContainer)
        prefs = getSharedPreferences("TASK_PREFS", Context.MODE_PRIVATE)

        loadTasks()
        displayTasks()
    }

    private fun loadTasks() {
        // Load the task list from SharedPreferences
        val set = prefs.getStringSet("TASKS", mutableSetOf())
        taskList = set?.toMutableList() ?: mutableListOf()
    }

    private fun displayTasks() {
        tasksContainer.removeAllViews()
        
        // We create a copy to avoid ConcurrentModificationException if needed, 
        // but here we just iterate and build the UI
        for (task in taskList) {
            val taskView = layoutInflater.inflate(R.layout.task_row, tasksContainer, false)

            val taskText = taskView.findViewById<TextView>(R.id.taskText)
            val checkBox = taskView.findViewById<CheckBox>(R.id.checkTask)
            val deleteButton = taskView.findViewById<ImageButton>(R.id.deleteTask)

            // Handle tasks that might or might not have completion state stored in string
            // For now, keeping it simple as just the task name
            taskText.text = task

            deleteButton.setOnClickListener {
                taskList.remove(task)
                saveTasks()
                displayTasks() // Refresh list
            }

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    taskText.paintFlags = taskText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    taskText.paintFlags = taskText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
            }

            tasksContainer.addView(taskView)
        }
    }

    private fun saveTasks() {
        prefs.edit().putStringSet("TASKS", taskList.toSet()).apply()
    }
}
