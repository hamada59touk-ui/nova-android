package com.example.nova

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : Activity() {

    private lateinit var antwort: TextView
    private lateinit var eingabe: EditText
    private lateinit var senden: Button

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

        antwort = TextView(this).apply {
            text = "Hallo! Ich bin Nova."
            textSize = 18f
        }

        eingabe = EditText(this).apply {
            hint = "Schreib Nova etwas..."
        }

        senden = Button(this).apply {
            text = "Senden"
        }

        val mikrofon = Button(this).apply {
            text = "🎤 Mit Nova sprechen"
        }

        senden.setOnClickListener {

            val nachricht = eingabe.text.toString().trim()

            if (nachricht.isNotEmpty()) {

                antwort.text = "Nova denkt..."
                senden.isEnabled = false
                eingabe.text.clear()

                frageNova(nachricht)
            }
        }

        mikrofon.setOnClickListener {
            antwort.text = "🎤 Sprache kommt als Nächstes."
        }

        layout.addView(titel)
        layout.addView(antwort)
        layout.addView(eingabe)
        layout.addView(senden)
        layout.addView(mikrofon)

        setContentView(layout)
    }


    private fun frageNova(nachricht: String) {

        Thread {

            try {

                val url = URL(
                    "https://mein-ai-agent.onrender.com/api/chat"
                )

                val verbindung =
                    url.openConnection() as HttpURLConnection

                verbindung.requestMethod = "POST"

                verbindung.setRequestProperty(
                    "Content-Type",
                    "application/json"
                )

                verbindung.connectTimeout = 60000
                verbindung.readTimeout = 120000

                verbindung.doOutput = true


                val json = JSONObject()

                json.put(
                    "message",
                    nachricht
                )


                verbindung.outputStream.use { output ->

                    output.write(
                        json.toString()
                            .toByteArray(Charsets.UTF_8)
                    )
                }


                val status =
                    verbindung.responseCode


                if (status in 200..299) {

                    val text =
                        verbindung.inputStream
                            .bufferedReader()
                            .use {
                                it.readText()
                            }


                    val antwortJson =
                        JSONObject(text)

                    val novaAntwort =
                        antwortJson.getString("reply")


                    runOnUiThread {

                        antwort.text =
                            "Nova:\n\n$novaAntwort"

                        senden.isEnabled = true
                    }

                } else {

                    runOnUiThread {

                        antwort.text =
                            "Fehler vom Server: $status"

                        senden.isEnabled = true
                    }
                }


                verbindung.disconnect()


            } catch (fehler: Exception) {

                runOnUiThread {

                    antwort.text =
                        "Verbindungsfehler:\n${fehler.message}"

                    senden.isEnabled = true
                }
            }

        }.start()
    }
}
