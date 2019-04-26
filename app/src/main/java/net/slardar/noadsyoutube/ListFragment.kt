package net.slardar.noadsyoutube

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import net.slardar.widget.SlardarHTTPSGet
import net.slardar.widget.SlardarScrollView
import org.json.JSONObject

abstract class ListFragment : Fragment() {
    private lateinit var rootView: FrameLayout
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var itemsList: LinearLayout
    private lateinit var scrollView: SlardarScrollView

    internal var itemJSONObject: JSONObject? = null
    private var videoItems: ArrayList<VideoItem> = ArrayList()
    private lateinit var mainActivity: MainActivity
    private lateinit var listFragmentHandler: ListFragmentHandler
    private var loadedItems: Int = 0
    private var displayTheme: Int = 0
    private var topRefresh: ((SlardarScrollView) -> Unit)? = null

    internal var baseContext: Context? = null

    private val header: ArrayList<Pair<String, String>> = ArrayList()

    private val YOUTUBE_HEADER: Array<String> =
        arrayOf(
            "User-Agent", "Mozilla/5.0 (Linux; Android 5.0)",
            "X-YouTube-Client-Name", "2",
            "X-YouTube-Client-Version", "2.20190419"
        )
    private var loadingItems: Int = 0
    private var loadingItemLayout: LinearLayout? = null


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        this.rootView = inflater.inflate(R.layout.list_fragment, container, false) as FrameLayout
        this.loadingProgressBar = rootView.findViewById(R.id.loadingProgressBar)
        this.itemsList = rootView.findViewById(R.id.root_view)
        this.scrollView = rootView.findViewById(R.id.scrollView) as SlardarScrollView

        //Set Theme
        when (displayTheme) {
            0 -> {
                rootView.setBackgroundResource((R.color.dark_background_color))
                loadingProgressBar.indeterminateDrawable.setColorFilter(
                    ContextCompat.getColor(
                        context!!, if (displayTheme == 0) {
                            R.color.light_background_color
                        } else {
                            R.color.dark_background_color
                        }
                    ), PorterDuff.Mode.SRC_IN
                )
            }

            1 -> {
                rootView.setBackgroundResource((R.color.light_background_color))
                loadingProgressBar.indeterminateDrawable.setColorFilter(
                    ContextCompat.getColor(
                        context!!, if (displayTheme == 0) {
                            R.color.light_background_color
                        } else {
                            R.color.dark_background_color
                        }
                    ), PorterDuff.Mode.SRC_IN
                )
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
                VideoItem.addVideoItemToView(this.requireContext(), videoItems[index], itemsList, displayTheme)?.run {
                    this.setOnClickListener {
                        playVideo(it as LinearLayout)
                    }
                }
            }
            setLoading(false)
        }

        //Init variable
        mainActivity = activity as MainActivity
        scrollView.setShowBottomRefreshIcon(false)
        listFragmentHandler = ListFragmentHandler(this)

        topRefresh?.run {
            scrollView.setTopRefresh(this)
        }
        scrollView.setReachBottom {
            loadMore()
        }

        return rootView
    }

    fun setTopRefresh(topRefresh: ((SlardarScrollView) -> Unit)?) {
        this.topRefresh = topRefresh
        if (::scrollView.isInitialized) {
            this.scrollView.setTopRefresh(topRefresh)
        }
    }

    fun setTheme(displayTheme: Int) {
        this.displayTheme = displayTheme
        if (::loadingProgressBar.isInitialized) {
            loadingProgressBar.indeterminateDrawable.setColorFilter(
                ContextCompat.getColor(
                    context!!, if (displayTheme == 0) {
                        R.color.light_background_color
                    } else {
                        R.color.dark_background_color
                    }
                ), PorterDuff.Mode.SRC_IN
            )
        }
    }

    fun load(item: JSONObject) {
        itemJSONObject = item
        videoItems = ArrayList()
        itemsList.removeAllViews()
        val url: String =
            "https://m.youtube.com/?itct=" + item.getString("continuation") + "&ctoken=" + item.getString(
                "clickTrackingParams"
            ) + "&pbj=1"
        listFragmentHandler = ListFragmentHandler(this)
        if (header.count() != YOUTUBE_HEADER.size / 2) {
            var index = 0
            while (index < YOUTUBE_HEADER.size) {
                header.add(Pair(YOUTUBE_HEADER[index], YOUTUBE_HEADER[index + 1]))
                index += 2
            }
        }
        SlardarHTTPSGet.getStringThread(url, header, listFragmentHandler, 1)
    }

    fun setNext(item: JSONObject) {
        itemJSONObject = item
    }

    fun addItem(videoItem: VideoItem) {
        videoItems.add(videoItem)
    }

    fun setLoading(isLoading: Boolean) {
        if (::loadingProgressBar.isInitialized && ::scrollView.isInitialized) {
            if (isLoading) {
                this.loadingProgressBar.visibility = View.VISIBLE
                this.scrollView.visibility = View.GONE
            } else {
                this.loadingProgressBar.visibility = View.GONE
                this.scrollView.visibility = View.VISIBLE
            }
        }
    }

    fun loadMore() {
        if (loadingItems > 0)
            return
        if (loadedItems < videoItems.count() - 1) {
            val step = if (loadedItems < videoItems.count() - 6) {
                5
            } else {
                videoItems.count() - 1 - loadedItems
            }
            loadingItems += step
            for (index in loadedItems until loadedItems + step) {
                if (videoItems[index].isLoaded()) {
                    addVideoItem(videoItems[index])
                } else {
                    val msg = Message()
                    msg.arg1 = 2
                    msg.obj = videoItems[index]
                    videoItems[index].loadThumb(listFragmentHandler, msg)
                }
            }
            loadedItems += step

        } else {
            if (itemJSONObject != null && loadingItemLayout == null) {
                val url: String =
                    "https://m.youtube.com/?itct=" + itemJSONObject!!.getString("continuation") + "&ctoken=" + itemJSONObject!!.getString(
                        "clickTrackingParams"
                    ) + "&pbj=1"
                loadingItemLayout =
                    LayoutInflater.from(requireContext()).inflate(
                        R.layout.load_waiting_item,
                        itemsList,
                        false
                    ) as LinearLayout

                (loadingItemLayout!!.getChildAt(0) as ProgressBar).indeterminateDrawable.setColorFilter(
                    ContextCompat.getColor(
                        context!!, if (displayTheme == 0) {
                            R.color.light_background_color
                        } else {
                            R.color.dark_background_color
                        }
                    ), PorterDuff.Mode.SRC_IN
                )
                scrollView.scrollTo(0, scrollView.getScrollBottom())
                itemsList.addView(loadingItemLayout)
                SlardarHTTPSGet.getStringThread(url, header, listFragmentHandler, 1)
            }
        }
    }

    fun addVideoItem(videoItem: VideoItem) {
        loadingItems--

        while (true) {
            if (itemsList.getChildAt(itemsList.childCount - 1) == null) {
                break
            } else if (itemsList.getChildAt(itemsList.childCount - 1).tag == null) {
                break
            } else if ((itemsList.getChildAt(itemsList.childCount - 1).tag as String).compareTo("waiting") != 0) {
                break
            } else {
                itemsList.removeViewAt(itemsList.childCount - 1)
            }
        }

        VideoItem.addVideoItemToView(
            requireContext(),
            videoItem,
            itemsList,
            displayTheme
        )?.run {
            this.setOnClickListener {
                playVideo(it as LinearLayout)
            }
        }

        if (loadingItemLayout != null) {
            loadingItemLayout = null
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("itemJSONObject", itemJSONObject.toString())
        outState.putSerializable("header", header)
        super.onSaveInstanceState(outState)
    }

    private fun playVideo(view: LinearLayout) {
        if (view.tag == null) {
            Toast.makeText(context, R.string.id_missing, Toast.LENGTH_SHORT).show()
        } else {
            val videoID: String = view.tag as String
            var intent: Intent? = context!!.packageManager.getLaunchIntentForPackage("com.google.android.youtube")
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
    }
}