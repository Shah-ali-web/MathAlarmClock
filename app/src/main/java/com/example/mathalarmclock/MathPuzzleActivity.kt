package com.example.mathalarmclock

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlin.random.Random

class MathPuzzleActivity : AppCompatActivity() {

    private lateinit var puzzleTextView: TextView
    private lateinit var answerEditText: EditText
    private lateinit var submitButton: Button

    private var correctAnswer: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_math_puzzle)

        puzzleTextView = findViewById(R.id.puzzleTextView)
        answerEditText = findViewById(R.id.answerEditText)
        submitButton = findViewById(R.id.submitButton)

        generateAndDisplayProblem()

        submitButton.setOnClickListener {
            checkAnswer()
        }
    }

    private fun generateAndDisplayProblem() {
        val num1 = Random.nextInt(1, 10)
        val num2 = Random.nextInt(1, 10)
        correctAnswer = num1 + num2
        puzzleTextView.text = "$num1 + $num2 = ?"
    }


    private fun checkAnswer() {
        val userAnswerText = answerEditText.text.toString()
        val userAnswer = userAnswerText.toIntOrNull()

        if (userAnswer == null) {
            Toast.makeText(this, "Please enter a valid number.", Toast.LENGTH_SHORT).show()
            return
        }

        if (userAnswer == correctAnswer) {
            stopAlarmAndDismiss()
        } else {
            Toast.makeText(this, "Wrong answer! Try again.", Toast.LENGTH_SHORT).show()
            answerEditText.text.clear()
        }
    }

    private fun saveTask(taskText: String) {
        val prefs = getSharedPreferences("TASK_PREFS", Context.MODE_PRIVATE)
        val existingTasks = prefs.getStringSet("TASKS", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        existingTasks.add(taskText)
        prefs.edit().putStringSet("TASKS", existingTasks).apply()
    }

    private fun stopAlarmAndDismiss() {

        val stopIntent = Intent(this, AlarmService::class.java)
        stopService(stopIntent)

        // ✅ UPDATE STREAK HERE
        StreakManager.updateStreak(this)

        Toast.makeText(
            this,
            "Alarm Dismissed! Streak updated 🔥",
            Toast.LENGTH_LONG
        ).show()

        finish()
    }

}