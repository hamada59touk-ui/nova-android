package com.example.nova

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
        }

        val titel = TextView(this).apply {
            text = "NOVA"
            textSize = 30f
        }

        val antwort = TextView(this).apply {
            text = "Hallo! Ich bin Nova."
            textSize = 18f
        }

        val eingabe = EditText(this).apply {
            hint = "Schreib Nova etwas..."
        }

        val senden = Button(this).apply {
            text = "Senden"
        }

        senden.setOnClickListener {
            val text = eingabe.text.toString()

            if (text.isNotBlank()) {
                antwort.text = "Du: $text\n\nNova: Ich funktioniere!"
                eingabe.text.clear()
            }
        }

        layout.addView(titel)
        layout.addView(antwort)
        layout.addView(eingabe)
        layout.addView(senden)

        setContentView(layout)
    }
}
