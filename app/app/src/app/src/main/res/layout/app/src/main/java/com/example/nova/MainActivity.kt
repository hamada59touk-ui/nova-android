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

        val mikrofon = Button(this).apply {
            text = "Mit Nova sprechen"
        }

        senden.setOnClickListener {
            val text = eingabe.text.toString()

            if (text.isNotBlank()) {
                antwort.text = "Du: $text\n\nNova: Ich funktioniere!"
                eingabe.text.clear()
            }
        }

        mikrofon.setOnClickListener {
            antwort.text = "Sprachfunktion kommt als Nächstes."
        }

        layout.addView(titel)
        layout.addView(antwort)
        layout.addView(eingabe)
        layout.addView(senden)
        layout.addView(mikrofon)

        setContentView(layout)
    }
}
