package net.slardar.noadsyoutube

import android.app.Activity
import android.os.Handler
import android.os.Message
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import java.lang.ref.WeakReference

class UpdateHandler : Handler {
    private val mActivity: WeakReference<Activity>?
    private val mainActivity: MainActivity?
    private var homeContent: JSONObject? = null
    private var trendingContent: JSONObject? = null
    private var libraryContent: JSONObject? = null
    private var subscriptionsContent: JSONObject? = null

    constructor() : super() {
        mActivity = null
        mainActivity = null
    }

    constructor(activity: MainActivity) : super() {
        mActivity = WeakReference(activity)
        this.mainActivity = activity
    }


    override fun handleMessage(msg: Message) {
        when (mActivity == null || mainActivity == null) {
            true -> {
                return
            }
        }
        when (msg.arg1) {
            //Get list error
            -1 -> {
                Toast.makeText(mActivity!!.get(), msg.obj as String, Toast.LENGTH_LONG).show()
            }
            //Get list json
            0 -> {
                val htmlText: String? = msg.obj as String
                val startIndex: Int? = htmlText!!.indexOf("<div id=\"initial-data\"><!-- ", 0, true) + 28
                val endIndex: Int? = htmlText.indexOf("-->", startIndex!!)
                val jsonList: JSONObject
                when (startIndex == -1 || endIndex == -1) {
                    true -> {
                        Toast.makeText(
                            mainActivity,
                            mainActivity!!.getText(R.string.load_error),
                            Toast.LENGTH_LONG
                        ).show()
                        return
                    }
                    false -> {
                        jsonList = JSONObject(htmlText.substring(startIndex, endIndex!!))
                        val tabs: JSONArray? = if (jsonList.has("contents")) {
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

                        if (tabs != null) {
                            for (index in 0..(tabs.length() - 1)) {
                                when (tabs.getJSONObject(index).getJSONObject("tabRenderer").getString("title")) {
                                    mainActivity!!.getString(R.string.home) -> {
                                        homeContent = tabs.getJSONObject(index).getJSONObject("tabRenderer")
                                    }
                                    mainActivity.getString(R.string.trending) -> {
                                        trendingContent = tabs.getJSONObject(index).getJSONObject("tabRenderer")
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(
                                mainActivity,
                                mainActivity!!.getText(R.string.load_error),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
                when (homeContent == null || trendingContent == null) {
                    true -> {
                        Toast.makeText(mainActivity, mainActivity!!.getString(R.string.load_error), Toast.LENGTH_LONG)
                            .show()
                    }

                    false -> {

                    }
                }
            }
        }
    }

}