package net.slardar.noadsyoutube

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import net.slardar.widget.SlardarHTTPSGet
import org.json.JSONArray
import org.json.JSONObject

class VideoItem(jsonObject: JSONObject) {
    private var thumbnail: Bitmap? = null
    private val thumbnailString: String
    val lengthText: String
    var channelThumbnail: Bitmap? = null
    private val channelThumbnailString: String
    private val title: String
    val videoId: String
    private val description: String
    private var loaded: Boolean = false

    //private val thumbnailHandler: VideoItemThumbnailHandler

    init {
        videoId = jsonObject.getString("videoId")
        thumbnailString = getLargestURL(jsonObject.getJSONObject("thumbnail").getJSONArray("thumbnails"))
        lengthText = jsonObject.getJSONObject("lengthText").getJSONArray("runs").getJSONObject(0).getString("text")
        title =
            jsonObject.getJSONObject("title").getJSONArray("runs").getJSONObject(0).getString("text")
        channelThumbnailString = getLargestURL(jsonObject.getJSONObject("channelThumbnail").getJSONArray("thumbnails"))
        var descriptionTmp =
            jsonObject.getJSONObject("longBylineText").getJSONArray("runs").getJSONObject(0).getString("text")
        descriptionTmp += if (jsonObject.has("shortViewCountText")) {
            "·" + jsonObject.getJSONObject("shortViewCountText").getJSONArray("runs").getJSONObject(0).getString("text")
        } else {
            ""
        }
        descriptionTmp += if (jsonObject.has("publishedTimeText")) {
            "·" + jsonObject.getJSONObject("publishedTimeText").getJSONArray("runs").getJSONObject(0).getString("text")
        } else {
            ""
        }
        description = descriptionTmp
        loaded = false
    }

    fun loadThumb(handler: Handler, msg: Message) {
        when (loaded) {
            true -> {
                handler.sendMessage(msg)
            }
            false ->
                Thread(Runnable {
                    thumbnail = SlardarHTTPSGet.getBitmap(thumbnailString)
                    channelThumbnail = SlardarHTTPSGet.getBitmap(channelThumbnailString)
                    when (thumbnail != null && channelThumbnail != null) {
                        true -> {
                            handler.sendMessage(msg)
                        }
                    }
                    this@VideoItem.loaded = true
                }).start()
        }
    }

    private fun getLargestURL(thumbnailsArray: JSONArray): String {
        var size = 0
        var index = 0
        for (i in 0 until thumbnailsArray.length()) {
            val tempSize =
                thumbnailsArray.getJSONObject(i).getInt("width") * thumbnailsArray.getJSONObject(i).getInt("height")
            size = if (size < tempSize) {
                index = i
                tempSize
            } else {
                size
            }
        }
        return thumbnailsArray.getJSONObject(index).getString("url")
    }

    fun isLoaded(): Boolean {
        return loaded
    }

    companion object {
        fun addVideoItemToView(
            context: Context,
            videoItem: VideoItem,
            itemsList: LinearLayout,
            displayTheme: Int
        ): LinearLayout? {
            when ((videoItem.channelThumbnail != null) && (videoItem.thumbnail != null)) {
                true -> {
                    val childView: LinearLayout =
                        LayoutInflater.from(context).inflate(R.layout.video_item, itemsList, false) as LinearLayout

                    childView.findViewById<ImageView>(R.id.thumbnail).setImageBitmap(videoItem.thumbnail)
                    childView.findViewById<ImageView>(R.id.channelThumbnail).setImageBitmap(videoItem.channelThumbnail)
                    childView.findViewById<TextView>(R.id.lengthText).text = videoItem.lengthText
                    childView.findViewById<TextView>(R.id.title).text = videoItem.title
                    childView.findViewById<TextView>(R.id.description).text = videoItem.description


                    when (displayTheme) {
                        0 -> {
                            childView.findViewById<TextView>(R.id.title)
                                .setTextColor(ContextCompat.getColor(context, R.color.light_background_color))
                            childView.findViewById<TextView>(R.id.description)
                                .setTextColor(ContextCompat.getColor(context, R.color.light_grey))
                            itemsList.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_background_color))
                            childView.findViewById<ImageView>(R.id.channelThumbnailCover)
                                .setImageDrawable(ContextCompat.getDrawable(context, R.drawable.chanel_dark))


                            childView.findViewById<TextView>(R.id.lengthText)
                                .setBackgroundResource(R.drawable.ic_time_background_dark)
                            childView.findViewById<TextView>(R.id.lengthText)
                                .setTextColor(ContextCompat.getColor(context, R.color.light_background_color))
                        }
                        1 -> {
                            childView.findViewById<TextView>(R.id.title)
                                .setTextColor(ContextCompat.getColor(context, R.color.dark_background_color))
                            childView.findViewById<TextView>(R.id.description)
                                .setTextColor(ContextCompat.getColor(context, R.color.dark_grey))
                            itemsList.setBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.light_background_color
                                )
                            )
                            childView.findViewById<ImageView>(R.id.channelThumbnailCover)
                                .setImageDrawable(ContextCompat.getDrawable(context, R.drawable.chanel_light))

                            childView.findViewById<TextView>(R.id.lengthText)
                                .setBackgroundResource(R.drawable.ic_time_background_light)
                            childView.findViewById<TextView>(R.id.lengthText)
                                .setTextColor(ContextCompat.getColor(context, R.color.dark_background_color))
                        }
                    }
                    itemsList.addView(childView)
                    childView.tag = videoItem.videoId
                    return childView
                }
                false -> return null
            }
        }
    }
}