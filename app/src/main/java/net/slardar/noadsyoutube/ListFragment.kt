package net.slardar.noadsyoutube

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.json.JSONObject

abstract class ListFragment : Fragment() {
    private lateinit var rootView: FrameLayout
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var itemsList: LinearLayout
    private lateinit var scrollView: ScrollView
    private var content: JSONObject? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_home, container, false) as FrameLayout
        loadingProgressBar = rootView.findViewById(R.id.loadingProgressBar)
        itemsList = rootView.findViewById(R.id.root_view)
        scrollView = rootView.findViewById(R.id.scrollView)
        return rootView
    }

    fun setContent(content: JSONObject) {
        this.content = content
    }

    override fun onResume() {
        super.onResume()
        when (content == null) {
            true -> {
                loadingProgressBar.visibility = View.GONE
                itemsList.visibility = View.GONE
            }
            false -> {
                loadingProgressBar.visibility = View.GONE
                itemsList.visibility = View.GONE
                Toast.makeText(this.context, getText(R.string.load_error), Toast.LENGTH_LONG).show()
            }
        }
    }
}