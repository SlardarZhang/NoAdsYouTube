package net.slardar.noadsyoutube

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView

class SplashScreen : AppCompatActivity() {
    private val permissionCode: Int = 10250
    private lateinit var launchYouTube: TextView
    private lateinit var acceptButton: Button
    private var videoID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        launchYouTube = findViewById(R.id.launch_YouTube)
        acceptButton = findViewById(R.id.accept_button)

        //Set Listener
        launchYouTube.setOnClickListener {
            var intent: Intent? = packageManager.getLaunchIntentForPackage("com.google.android.youtube")
            when (intent != null) {
                // Set Flags to start YouTube
                true -> {
                    when (videoID.isNotEmpty()) {
                        true -> {
                            intent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoID"))
                        }
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

                // Launch Google Play Store
                false -> {
                    intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(getString(R.string.youtube_market))
                }
            }
            startActivity(intent)
        }

        acceptButton.setOnClickListener {
            val activityIntent = if (videoID.isNotEmpty()) {
                Intent(baseContext, PlayVideo::class.java)
            } else {
                Intent(baseContext, MainActivity::class.java)
            }
            if (videoID.isNotEmpty()) {
                activityIntent.putExtra("VID", videoID)
            }
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            this.startActivity(activityIntent)
            this.finish()
        }
    }

    override fun onStart() {
        super.onStart()
        val intent: Intent = intent

        when (intent.action) {
            Intent.ACTION_SEND -> {
                videoID = if (intent.type == "text/plain") {
                    intent.getStringExtra(Intent.EXTRA_TEXT)
                } else {
                    ""
                }
                if (videoID.isNotBlank()) {
                    videoID = if (videoID.lastIndexOf("/") != -1) {
                        videoID.substring(videoID.lastIndexOf("/") + 1)
                    } else {
                        ""
                    }
                }
            }

            Intent.ACTION_MAIN -> {
                videoID = ""
            }

            Intent.ACTION_VIEW -> {
                videoID = if (intent.data != null) {
                    intent.data!!.toString()
                } else {
                    ""
                }
                if (videoID.isNotBlank()) {
                    videoID = if (videoID.lastIndexOf("?v=") != -1) {
                        videoID.substring(videoID.lastIndexOf("?v=") + 3)
                    } else {
                        ""
                    }
                }
            }

            else -> {
                videoID = ""
            }
        }


        //Check permission
        if (android.os.Build.VERSION.SDK_INT >= 24) {
            val permissionList: ArrayList<String> = ArrayList()

            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.INTERNET
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionList.add(android.Manifest.permission.INTERNET)
            }


            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionList.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }


            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }

            if (permissionList.count() > 1) {

                val permissionArray = arrayOfNulls<String>(permissionList.count())
                permissionList.toArray(permissionArray)
                this.requestPermissions(permissionArray, permissionCode)
            }

        }
    }
}
