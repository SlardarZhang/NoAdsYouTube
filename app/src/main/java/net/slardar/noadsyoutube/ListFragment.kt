package net.slardar.noadsyoutube

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView

abstract class ListFragment(protected var mainActivity: MainActivity) : Fragment() {
    private lateinit var rootView: FrameLayout
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var itemsList: LinearLayout
    private lateinit var scrollView: ScrollView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.list_fragment, container, false) as FrameLayout
        loadingProgressBar = rootView.findViewById(R.id.loadingProgressBar)
        itemsList = rootView.findViewById(R.id.root_view)
        scrollView = rootView.findViewById(R.id.scrollView) as ScrollView
        when (mainActivity.displayTheme) {
            0 -> {
                rootView.setBackgroundResource((R.color.dark_background_color))
            }

            1 -> {
                rootView.setBackgroundResource((R.color.light_background_color))
            }
        }
        return rootView
    }

    fun addItem(videoItem: VideoItem) {
        VideoItem.addVideoItemToView(mainActivity.applicationContext, videoItem, itemsList)
        when (loadingProgressBar.visibility) {
            View.VISIBLE -> {
                scrollView.visibility = View.VISIBLE
                loadingProgressBar.visibility = View.GONE
            }
        }
    }

}