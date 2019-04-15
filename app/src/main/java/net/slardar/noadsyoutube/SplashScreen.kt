package net.slardar.noadsyoutube

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView

class SplashScreen : AppCompatActivity() {
    private lateinit var launchYouTube: TextView
    private lateinit var acceptButton: Button
    private var shareURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        launchYouTube = findViewById(R.id.launch_YouTube)
        acceptButton = findViewById(R.id.accept_button)

        //Set Listener
    }

    override fun onStart() {
        super.onStart()
        val intent = intent
        if (intent.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            shareURL = intent.getStringExtra(Intent.EXTRA_TEXT)
        } else {
            shareURL = ""
        }
    }
}
