package net.slardar.noadsyoutube

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import net.slardar.widget.SlardarHTTPSGet
import java.util.*
import kotlin.collections.HashMap

class PlayVideo : AppCompatActivity() {
    private var videoID: String = ""
    private var displayTheme: Int = 0
    private val requestHeader: HashMap<String, String> = HashMap()
    private val playvideoHandler: PlayVideoHandler = PlayVideoHandler(this)


    companion object {
        private val YOUTUBE_HEADER: Array<String> =
            arrayOf(
                "User-Agent", "Mozilla/5.0 (Linux; Android 5.0)",
                "X-YouTube-Client-Name", "2",
                "X-YouTube-Client-Version", "2.20190419"
            )

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_video)

        val sharedPreferences: SharedPreferences? = this.getPreferences(Context.MODE_PRIVATE)

        if (sharedPreferences != null) {
            displayTheme = sharedPreferences.getInt(getString(R.string.theme), 0)
        }


        if (intent.hasExtra("VID")) {
            if (intent.getStringExtra("VID") != null) {
                videoID = intent.getStringExtra("VID")
            }
        }

        for (index in 0 until YOUTUBE_HEADER.size - 1) {
            if (index % 2 == 0)
                requestHeader[YOUTUBE_HEADER[index]] = YOUTUBE_HEADER[index + 1]
        }
        requestHeader["accept-language"] = Locale.getDefault().toString() + ";q=0.9, *;q=0.2"
        SlardarHTTPSGet.getStringThread(
            "https://www.youtube.com/get_video_info?html5=1&app=desktop&video_id=$videoID",
            requestHeader,
            playvideoHandler,
            0
        )
    }

    override fun onStart() {
        super.onStart()
        if (videoID.isEmpty()) {
            onBackPressed()
        }
        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }
}
