package net.slardar.noadsyoutube

import android.content.Context
import android.os.Bundle
import android.os.Message
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import net.slardar.widget.SlardarHTTPSGet
import org.json.JSONObject

abstract class ListFragment : Fragment() {
    private lateinit var rootView: FrameLayout
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var itemsList: LinearLayout
    private lateinit var scrollView: ScrollView

    internal var itemJSONObject: JSONObject? = null
    private var videoItems: ArrayList<VideoItem> = ArrayList()
    private lateinit var mainActivity: MainActivity
    private var listFragmentHandler: ListFragmentHandler? = null
    private var loadedItems: Int = 0
    private var displayTheme: Int = 0

    internal var baseContext: Context? = null

    private val header: ArrayList<Pair<String, String>> = ArrayList()

    private val YOUTUBE_HEADER: Array<String> =
        arrayOf(
            "User-Agent", "Mozilla/5.0 (Linux; Android 5.0)",
            "X-YouTube-Client-Name", "2",
            "X-YouTube-Client-Version", "2.20190419"
        )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        this.rootView = inflater.inflate(R.layout.list_fragment, container, false) as FrameLayout
        this.loadingProgressBar = rootView.findViewById(R.id.loadingProgressBar)
        this.itemsList = rootView.findViewById(R.id.root_view)
        this.scrollView = rootView.findViewById(R.id.scrollView) as ScrollView

        //Set Theme
        when ((this.activity as MainActivity).getDisplayTheme()) {
            0 -> {
                rootView.setBackgroundResource((R.color.dark_background_color))
            }

            1 -> {
                rootView.setBackgroundResource((R.color.light_background_color))
            }
        }

        //Load saved InstanceState
        itemJSONObject = if (savedInstanceState?.getString("itemJSONObject") != null) {
            JSONObject(savedInstanceState.getString("itemJSONObject"))
        } else {
            null
        }

        if (videoItems.count() > 0) {
            for (index: Int in 0 until loadedItems) {
                VideoItem.addVideoItemToView(this.requireContext(), videoItems[index], itemsList, displayTheme)
            }
            setLoading(false)
        }
        Log.wtf("loadedItems", loadedItems.toString() + "/" + videoItems.count().toString())
        //Init variable
        mainActivity = activity as MainActivity
        listFragmentHandler = ListFragmentHandler(this@ListFragment)
        return rootView
    }

    fun setTheme(displayTheme: Int) {
        this.displayTheme = displayTheme
    }

    fun reload(item: JSONObject) {
        itemJSONObject = item
        videoItems = ArrayList()
        itemsList.removeAllViews()
        val url: String =
            "https://m.youtube.com/?itct=" + item.getString("continuation") + "&ctoken=" + item.getString(
                "clickTrackingParams"
            ) + "&pbj=1"
        if (listFragmentHandler == null)
            listFragmentHandler = ListFragmentHandler(this)
        if (header.count() != YOUTUBE_HEADER.size / 2) {
            var index = 0
            while (index < YOUTUBE_HEADER.size) {
                header.add(Pair(YOUTUBE_HEADER[index], YOUTUBE_HEADER[index + 1]))
                index += 2
            }
        }
        SlardarHTTPSGet.getHTML(url, header, listFragmentHandler!!, 1)
    }

    fun setNext(item: JSONObject) {
        itemJSONObject = item
    }

    fun addItem(videoItem: VideoItem) {
        videoItems.add(videoItem)
    }

    fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            this.loadingProgressBar.visibility = View.VISIBLE
            this.scrollView.visibility = View.GONE
        } else {
            this.loadingProgressBar.visibility = View.GONE
            this.scrollView.visibility = View.VISIBLE
        }
    }

    fun loadMore() {
        if (loadedItems < videoItems.count() - 1) {
            val step = if (loadedItems < videoItems.count() - 6) {
                5
            } else {
                videoItems.count() - 1 - loadedItems
            }
            for (index in loadedItems until loadedItems + step) {
                if (videoItems[index].isLoaded()) {
                    addVideoItem(videoItems[index])
                } else {
                    val msg = Message()
                    msg.arg1 = 2
                    msg.obj = videoItems[index]
                    videoItems[index].loadThumb(listFragmentHandler!!, msg)
                }
            }
            loadedItems += step

        } else {
            if (itemJSONObject != null) {
                val url: String =
                    "https://m.youtube.com/?itct=" + itemJSONObject!!.getString("continuation") + "&ctoken=" + itemJSONObject!!.getString(
                        "clickTrackingParams"
                    ) + "&pbj=1"
                SlardarHTTPSGet.getHTML(url, header, listFragmentHandler!!, 1)
            }
        }
    }

    fun addVideoItem(videoItem: VideoItem) {
        VideoItem.addVideoItemToView(
            requireContext(),
            videoItem,
            itemsList,
            displayTheme
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("itemJSONObject", itemJSONObject.toString())
        outState.putSerializable("header", header)
        super.onSaveInstanceState(outState)
    }

}