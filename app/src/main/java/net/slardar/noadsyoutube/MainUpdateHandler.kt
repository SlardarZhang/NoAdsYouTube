package net.slardar.noadsyoutube

import android.os.Handler
import android.os.Message
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject

class MainUpdateHandler(activity: MainActivity) : Handler(activity.mainLooper) {
    private val mainActivity: MainActivity = activity

    override fun handleMessage(msg: Message) {
        when (msg.arg1) {
            //Get list error
            -1 -> {
                Toast.makeText(mainActivity, msg.obj as String, Toast.LENGTH_SHORT).show()
            }

            //Get list json
            0 -> {
                val htmlText: String? = msg.obj as String
                val startIndex: Int? = htmlText!!.indexOf("<div id=\"initial-data\"><!-- ", 0, true) + 28
                val endIndex: Int? = htmlText.indexOf("-->", startIndex!!)
                val jsonList: JSONObject
                when (startIndex == -1 || endIndex == -1) {
                    true -> {
                        Toast.makeText(mainActivity, R.string.load_error, Toast.LENGTH_SHORT).show()
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
                            mainActivity.reloaded(tabs)
                        }
                    }
                }
            }

            /*Get Home list
            1 -> {
                try {
                    val responseJSONObject = JSONObject(msg.obj as String)
                    responseJSONObject?.run {
                        mainActivity.homeFragment.setItemJSONObject(
                            responseJSONObject.getJSONObject("response")
                                .getJSONObject("continuationContents")
                                .getJSONObject("sectionListContinuation")
                                .getJSONArray("continuations")
                                .getJSONObject(0)
                                .getJSONObject("nextContinuationData")
                        )
                    }
                } catch (ex: Exception) {
                    Toast.makeText(
                        mainActivity,
                        "Load home list error$ex.message",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.wtf("Load home list error", ex)
                }
            }

            //Get Trending list
            2 -> {
                try {
                    val responseJSONObject = JSONObject(msg.obj as String)
                    responseJSONObject?.run {
                        mainActivity.trendingFragment
                    }
                    mainActivity.trendingItem =
                        responseJSONObject.getJSONObject("response").getJSONObject("continuationContents")
                            .getJSONObject("sectionListContinuation").getJSONArray("continuations").getJSONObject(0)
                            .getJSONObject("nextContinuationData")
                    mainActivity.trendingItemList.addAll(
                        jsonObjToVideoItemList(
                            responseJSONObject,
                            mainActivity.trendingFragment
                        )
                    )
                } catch (ex: Exception) {
                    Toast.makeText(
                        mainActivity,
                        mainActivity.getString(R.string.load_error) + ex.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.wtf("Load trending list error", ex)
                }
            }*/
        }

    }


}