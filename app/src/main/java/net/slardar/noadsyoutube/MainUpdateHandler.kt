package net.slardar.noadsyoutube

import android.os.Handler
import android.os.Message
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject

class MainUpdateHandler(activity: MainActivity) : Handler(activity.mainLooper) {
    private val mainActivity: MainActivity = activity

    override fun handleMessage(msg: Message) {
        if (msg.arg1 == -1) {
            Toast.makeText(mainActivity, msg.obj as String, Toast.LENGTH_SHORT).show()
        } else {
            objToTabJSONArray(msg.obj as String)?.run {
                mainActivity.reloaded(this, msg.arg1)
            }
        }
    }

    private fun objToTabJSONArray(obj: String): JSONArray? {
        val startIndex: Int? = obj.indexOf("<div id=\"initial-data\"><!-- ", 0, true) + 28
        val endIndex: Int? = obj.indexOf("-->", startIndex!!)
        val jsonList: JSONObject
        when (startIndex == -1 || endIndex == -1) {
            true -> {
                Toast.makeText(mainActivity, R.string.load_error, Toast.LENGTH_SHORT).show()
                return null
            }
            false -> {
                jsonList = JSONObject(obj.substring(startIndex, endIndex!!))
                return if (jsonList.has("contents")) {
                    if (jsonList.getJSONObject("contents").has("singleColumnBrowseResultsRenderer")) {
                        if (jsonList.getJSONObject("contents").getJSONObject("singleColumnBrowseResultsRenderer").has(
                                "tabs"
                            )
                        ) {
                            jsonList.getJSONObject("contents")
                                .getJSONObject("singleColumnBrowseResultsRenderer").getJSONArray("tabs")
                        } else {
                            null
                        }
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
        }
    }

}