package net.slardar.noadsyoutube

import android.content.Intent
import android.net.Uri
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
        launchYouTube.setOnClickListener {
            var intent = packageManager.getLaunchIntentForPackage("com.google.android.youtube")
            if (intent != null) {
                // Set Flags to start YouTube
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            } else {
                // Launch Google Play Store
                intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(getString(R.string.youtube_market))
            }
            startActivity(intent)
        }

        acceptButton.setOnClickListener {
            val intent = Intent(baseContext, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            if (shareURL.isNotEmpty()) {
                intent.putExtra("shareURL", shareURL)
            }
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        val intent: Intent = intent
        if (intent.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            shareURL = intent.getStringExtra(Intent.EXTRA_TEXT)
        } else {
            shareURL = ""
        }
    }
}
