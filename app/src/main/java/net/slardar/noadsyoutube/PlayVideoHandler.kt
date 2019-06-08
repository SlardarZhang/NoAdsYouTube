package net.slardar.noadsyoutube

import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import java.net.URLDecoder

class PlayVideoHandler(private val playVideoActivity: PlayVideo) : Handler() {
    private val qualityPattern: Regex = "^\\d*p$".toRegex()

    override fun handleMessage(msg: Message) {
        val videoInfo: String = msg.obj as String
        if (videoInfo.contains("reason=Video+unavailable")) {
            Toast.makeText(this.playVideoActivity, R.string.video_unavailable, Toast.LENGTH_LONG).show()
            this.playVideoActivity.onBackPressed()
        }
        val startIndex: Int = videoInfo.indexOf("adaptive_fmts") + 14
        val endIndex: Int = videoInfo.indexOf("&", startIndex)
        if (startIndex == 13 || endIndex == -1)
            return
        val adaptiveFmts: String = URLDecoder.decode(videoInfo.substring(startIndex, endIndex), "UTF-8")

        val qualityList: HashMap<String, String> = HashMap()
        val originalQualityList: List<String> = adaptiveFmts.split("quality_label")

        originalQualityList.forEach {
            if (it.indexOf("mp4") != -1) {
                val mp4URL = it
                val quality = mp4URL.substring(1, mp4URL.indexOf("&"))
                if (qualityPattern.containsMatchIn(quality)) {
                    val urlIndex = mp4URL.indexOf("url=", 0, true) + 4
                    val url =
                        URLDecoder.decode(mp4URL.substring(urlIndex, mp4URL.indexOf("&", urlIndex, true)), "UTF-8")
                    if (!qualityList.containsKey(quality) && url.contains("mime=video")) {
                        qualityList.put(quality, url)
                        Log.wtf("QU", quality + "/" + url)
                    } else if (url.contains("mime=audio")) {
                        qualityList.put("AUDIO", url)
                        Log.wtf("AU", "AUDIO/" + url)
                    }
                } else {
                    Log.wtf("OTHER", it)
                }
            } else {
                Log.wtf("OTHERFMT", it)
            }
        }
        super.handleMessage(msg)
    }
}