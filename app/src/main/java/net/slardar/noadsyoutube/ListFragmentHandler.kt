package net.slardar.noadsyoutube

import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject

class ListFragmentHandler(private val listFragment: ListFragment) : Handler() {
    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        when (msg.arg1) {
            -1 -> {
                Log.wtf("Load Fragment List error", msg.obj as String)
            }
            1 -> {
                try {
                    val responseJSONObject = JSONObject(msg.obj as String)
                    if (responseJSONObject.has("reload"))
                        if (responseJSONObject.getString("reload").toLowerCase().compareTo("now") == 0) {
                            listFragment.load(listFragment.itemJSONObject!!)
                            return
                        }
                    jsonObjToVideoItemList(responseJSONObject)
                    listFragment.setNext(
                        responseJSONObject.getJSONObject("response")
                            .getJSONObject("continuationContents")
                            .getJSONObject("sectionListContinuation")
                            .getJSONArray("continuations")
                            .getJSONObject(0)
                            .getJSONObject("nextContinuationData")
                    )
                    listFragment.setLoading(false)
                    listFragment.loadMore()
                } catch (ex: Exception) {
                    Log.wtf("Load list error", ex)
                    Log.wtf("Load list error", "Load $listFragment.toString() list error")
                    Toast.makeText(
                        listFragment.baseContext,
                        "Load list error$ex.message",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            2 -> {
                listFragment.addVideoItem(msg.obj as VideoItem)
            }
        }
    }


    private fun jsonObjToVideoItemList(responseJSONObject: JSONObject) {
        try {
            val contents: JSONArray =
                responseJSONObject.getJSONObject("response")
                    .getJSONObject("continuationContents")
                    .getJSONObject("sectionListContinuation")
                    .getJSONArray("contents")
            for (i in 0 until contents.length()) {
                if (contents.getJSONObject(i).getJSONObject("itemSectionRenderer").getJSONArray("contents")
                        .getJSONObject(0).has("shelfRenderer")
                ) {
                    val subContents: JSONArray =
                        contents.getJSONObject(i).getJSONObject("itemSectionRenderer").getJSONArray("contents")
                            .getJSONObject(0).getJSONObject("shelfRenderer").getJSONObject("content")
                            .getJSONObject("verticalListRenderer").getJSONArray("items")
                    for (j in 0 until subContents.length()) {
                        listFragment.addItem(
                            VideoItem(subContents.getJSONObject(j).getJSONObject("compactVideoRenderer"))
                        )
                    }
                }
            }
        } catch (ex: Exception) {
            Log.wtf("Load List Fragment error", ex)
        }
    }
}