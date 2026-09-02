package com.example.nova

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {

    private lateinit var antwort: TextView
    private lateinit var eingabe: EditText
    private lateinit var senden: Button
    private lateinit var sprechen: Button

    private lateinit var tts: TextToSpeech

    private val SPRACHE_REQUEST = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sprachausgabe starten
        tts = TextToSpeech(this, this)

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

        sprechen = Button(this).apply {
            text = "🎤 Mit Nova sprechen"
        }


        // TEXT SENDEN

        senden.setOnClickListener {

            val nachricht =
                eingabe.text.toString().trim()

            if (nachricht.isNotEmpty()) {

                eingabe.text.clear()

                frageNova(nachricht)
            }
        }


        // MIKROFON

        sprechen.setOnClickListener {

            starteSpracherkennung()
        }


        layout.addView(titel)
        layout.addView(antwort)
        layout.addView(eingabe)
        layout.addView(senden)
        layout.addView(sprechen)

        setContentView(layout)
    }


    // ==============================
    // SPRACHERKENNUNG
    // ==============================

    private fun starteSpracherkennung() {

        val intent = Intent(
            RecognizerIntent.ACTION_RECOGNIZE_SPEECH
        )

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            "de-DE"
        )

        intent.putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            "Sprich mit Nova"
        )

        try {

            startActivityForResult(
                intent,
                SPRACHE_REQUEST
            )

        } catch (fehler: Exception) {

            antwort.text =
                "Spracherkennung ist auf diesem Gerät nicht verfügbar."
        }
    }


    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (
            requestCode == SPRACHE_REQUEST &&
            resultCode == RESULT_OK
        ) {

            val ergebnisse =
                data?.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS
                )

            val gesprochenerText =
                ergebnisse?.firstOrNull()

            if (!gesprochenerText.isNullOrBlank()) {

                eingabe.setText(
                    gesprochenerText
                )

                frageNova(
                    gesprochenerText
                )
            }
        }
    }


    // ==============================
    // NOVA API
    // ==============================

    private fun frageNova(nachricht: String) {

        antwort.text = "Nova denkt..."

        senden.isEnabled = false
        sprechen.isEnabled = false


        Thread {

            try {

                val url = URL(
                    "https://mein-ai-agent.onrender.com/api/chat"
                )

                val verbindung =
                    url.openConnection()
                            as HttpURLConnection

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


                verbindung.outputStream.use {

                    it.write(
                        json.toString()
                            .toByteArray(
                                Charsets.UTF_8
                            )
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


                    val jsonAntwort =
                        JSONObject(text)

                    val novaAntwort =
                        jsonAntwort.getString(
                            "reply"
                        )


                    runOnUiThread {

                        antwort.text =
                            "Nova:\n\n$novaAntwort"

                        // Antwort laut vorlesen
                        tts.speak(
                            novaAntwort,
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            "novaAntwort"
                        )

                        senden.isEnabled = true
                        sprechen.isEnabled = true
                    }


                } else {

                    runOnUiThread {

                        antwort.text =
                            "Serverfehler: $status"

                        senden.isEnabled = true
                        sprechen.isEnabled = true
                    }
                }


                verbindung.disconnect()


            } catch (fehler: Exception) {

                runOnUiThread {

                    antwort.text =
                        "Verbindungsfehler:\n${fehler.message}"

                    senden.isEnabled = true
                    sprechen.isEnabled = true
                }
            }

        }.start()
    }


    // ==============================
    // TEXT-TO-SPEECH
    // ==============================

    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {

            tts.language =
                Locale.GERMANY
        }
    }


    override fun onDestroy() {

        tts.stop()
        tts.shutdown()

        super.onDestroy()
    }
}
