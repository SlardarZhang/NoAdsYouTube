package net.slardar.noadsyoutube

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Message
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
        description =
            jsonObject.getJSONObject("longBylineText").getJSONArray("runs").getJSONObject(0).getString("text") + "·" +
                    jsonObject.getJSONObject("shortViewCountText").getJSONArray("runs").getJSONObject(0).getString("text") + "·" +
                    jsonObject.getJSONObject("publishedTimeText").getJSONArray("runs").getJSONObject(0).getString("text")

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
        fun addVideoItemToView(context: Context, videoItem: VideoItem, itemsList: LinearLayout, displayTheme: Int) {
            when ((videoItem.channelThumbnail != null) && (videoItem.thumbnail != null)) {
                true -> {
                    val childView: LinearLayout =
                        LayoutInflater.from(context).inflate(R.layout.video_item, itemsList) as LinearLayout

                    childView.findViewById<ImageView>(R.id.thumbnail).setImageBitmap(videoItem.thumbnail)
                    childView.findViewById<ImageView>(R.id.channelThumbnail).setImageBitmap(videoItem.channelThumbnail)
                    childView.findViewById<TextView>(R.id.lengthText).text = videoItem.lengthText
                    childView.findViewById<TextView>(R.id.title).text = videoItem.title
                    childView.findViewById<TextView>(R.id.description).text = videoItem.description

                    childView.tag = videoItem.videoId

                    when (displayTheme) {
                        0 -> {
                            childView.findViewById<TextView>(R.id.title)
                                .setTextColor(getColorById(context, R.color.light_background_color))
                            childView.findViewById<TextView>(R.id.description)
                                .setTextColor(getColorById(context, R.color.light_grey))
                            itemsList.setBackgroundColor(getColorById(context, R.color.dark_background_color))
                            childView.findViewById<ImageView>(R.id.channelThumbnailCover)
                                .setImageDrawable(getDrawableById(context, R.drawable.chanel_dark))

                        }
                        1 -> {
                            childView.findViewById<TextView>(R.id.title)
                                .setTextColor(getColorById(context, R.color.dark_background_color))
                            childView.findViewById<TextView>(R.id.description)
                                .setTextColor(getColorById(context, R.color.dark_grey))
                            itemsList.setBackgroundColor(getColorById(context, R.color.light_background_color))
                            childView.findViewById<ImageView>(R.id.channelThumbnailCover)
                                .setImageDrawable(getDrawableById(context, R.drawable.chanel_light))
                        }
                    }
                    itemsList.addView(childView)
                }
            }
        }

        private fun getColorById(context: Context, id: Int): Int {
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                return context.getColor(id)
            } else {
                return context.resources.getColor(id)
            }
        }

        private fun getDrawableById(context: Context, id: Int): Drawable? {
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                return context.getDrawable(id)

            } else {
                return context.resources.getDrawable(id)
            }
        }
    }
}