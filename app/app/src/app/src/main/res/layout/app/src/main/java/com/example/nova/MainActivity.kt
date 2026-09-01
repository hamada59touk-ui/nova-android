package com.example.nova

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val answerText = findViewById<TextView>(R.id.answerText)
        val messageInput = findViewById<EditText>(R.id.messageInput)
        val sendButton = findViewById<Button>(R.id.sendButton)
        val micButton = findViewById<Button>(R.id.micButton)

        sendButton.setOnClickListener {

            val nachricht = messageInput.text.toString()

            if (nachricht.isNotBlank()) {
                answerText.text = "Du: $nachricht\n\nNova: Verbindung zum Agenten kommt als Nächstes."
                messageInput.text.clear()
            }
        }

        micButton.setOnClickListener {
            answerText.text = "🎤 Sprachsteuerung wird vorbereitet..."
        }
    }
}
