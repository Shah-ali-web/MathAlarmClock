package com.example.mathalarmclock

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class TodoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo)

        val addTaskBtn = findViewById<Button>(R.id.addTaskBtn)
        val myTasksBtn = findViewById<Button>(R.id.myTasksBtn)

        addTaskBtn.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }

        myTasksBtn.setOnClickListener {
            startActivity(Intent(this, MyTaskActivity::class.java))
        }
    }
}
