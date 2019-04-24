package net.slardar.noadsyoutube

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import net.slardar.widget.SlardarFragmentPagerAdapter
import net.slardar.widget.SlardarHTTPSGet
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    private lateinit var navigationBar: TabLayout
    private lateinit var mainViewPager: ViewPager
    private lateinit var fragmentPagerAdapter: SlardarFragmentPagerAdapter
    internal var displayTheme: Int = 0
    private var isLogin: Boolean? = false


    lateinit var homeFragment: HomeFragment
    lateinit var trendingFragment: TrendingFragment
    lateinit var subscriptionsFragment: SubscriptionsFragment
    lateinit var libraryFragment: LibraryFragment

    private lateinit var updateHandler: Handler

    internal var homeItem: JSONObject? = null
    internal var trendingItem: JSONObject? = null
    internal var subscriptionsContent: JSONObject? = null
    internal var libraryContent: JSONObject? = null

    internal val homeItemList: ArrayList<VideoItem> = ArrayList()
    internal val trendingItemList: ArrayList<VideoItem> = ArrayList()

    companion object {
        private const val PERMISSION_CODE: Int = 10250
        private val YOUTUBE_HEADER: Array<String> =
            arrayOf(
                "User-Agent", "Mozilla/5.0 (Linux; Android 5.0)",
                "X-YouTube-Client-Name", "2",
                "X-YouTube-Client-Version", "2.20190419"
            )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Associate views
        mainViewPager = findViewById(R.id.main_viewPager)
        navigationBar = findViewById(R.id.navigation_bar)

        //Initial FragmentPagerAdaptor
        fragmentPagerAdapter = SlardarFragmentPagerAdapter(supportFragmentManager, this)

        homeFragment = HomeFragment(this)
        trendingFragment = TrendingFragment(this)
        subscriptionsFragment = SubscriptionsFragment(this)
        libraryFragment = LibraryFragment()


        fragmentPagerAdapter.addItem(homeFragment)
        fragmentPagerAdapter.addItem(trendingFragment)
        fragmentPagerAdapter.addItem(subscriptionsFragment)
        fragmentPagerAdapter.addItem(libraryFragment)

        //Setup tab with adapter
        navigationBar.setupWithViewPager(mainViewPager)
        mainViewPager.adapter = fragmentPagerAdapter


        //Setup ViewPager Listener
        navigationBar.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        when (displayTheme) {
                            0 -> tab.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_tab_home_light)
                            1 -> tab.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_tab_home_red)
                        }
                    }
                    1 -> {
                        when (displayTheme) {
                            0 -> tab.icon =
                                ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_tab_trending_light)
                            1 -> tab.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_tab_trending_red)
                        }
                    }
                    2 -> {
                        when (displayTheme) {
                            0 -> tab.icon =
                                ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_tab_subscriptions_light)
                            1 -> tab.icon =
                                ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_tab_subscriptions_red)
                        }
                    }
                    3 -> {
                        when (displayTheme) {
                            0 -> tab.icon =
                                ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_tab_library_light)
                            1 -> tab.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_tab_library_red)
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        tab.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_tab_home_dark)
                    }
                    1 -> {
                        tab.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_tab_trending_dark)
                    }
                    2 -> {
                        tab.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_tab_subscriptions_dark)
                    }
                    3 -> {
                        tab.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_tab_library_dark)
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })

        //Load Settings
        val sharedPreferences: SharedPreferences? = this.getPreferences(Context.MODE_PRIVATE)
        if (sharedPreferences != null) {
            displayTheme = sharedPreferences.getInt(getString(R.string.theme), -1)
            isLogin = sharedPreferences.getBoolean("isLogin", false)
            if (displayTheme == -1) {
                with(sharedPreferences.edit()) {
                    putInt(getString(R.string.theme), 0)
                    apply()
                }
            }
            displayTheme = 0
        } else {
            displayTheme = 0
        }

        displayTheme = 1

        //Setup TabLayout
        navigationBar.getTabAt(0)?.text = getText(R.string.home)
        navigationBar.getTabAt(1)?.text = getText(R.string.trending)
        navigationBar.getTabAt(2)?.text = getText(R.string.subscriptions)
        navigationBar.getTabAt(3)?.text = getText(R.string.library)

        when (displayTheme) {
            0 -> {
                navigationBar.setBackgroundResource(R.drawable.navigation_bar_dark_background)

                navigationBar.setTabTextColors(
                    ContextCompat.getColor(
                        this@MainActivity,
                        R.color.dark_grey
                    ), ContextCompat.getColor(
                        this@MainActivity,
                        R.color.light_background_color
                    )
                )


                with(navigationBar.getTabAt(0)) {
                    this?.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_tab_home_light)
                }
                with(navigationBar.getTabAt(1)) {
                    this?.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_tab_trending_light)
                }
                with(navigationBar.getTabAt(2)) {
                    this?.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_tab_subscriptions_light)
                }
                with(navigationBar.getTabAt(3)) {
                    this?.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_tab_library_light)
                }
            }

            1 -> {
                navigationBar.setBackgroundResource(R.drawable.navigation_bar_light_background)

                navigationBar.setTabTextColors(
                    ContextCompat.getColor(
                        this@MainActivity,
                        R.color.dark_grey
                    ), ContextCompat.getColor(
                        this@MainActivity,
                        R.color.red
                    )
                )

                with(navigationBar.getTabAt(0)) {
                    this?.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_tab_home_red)
                }
                with(navigationBar.getTabAt(1)) {
                    this?.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_tab_trending_dark)
                }
                with(navigationBar.getTabAt(2)) {
                    this?.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_tab_subscriptions_dark)
                }
                with(navigationBar.getTabAt(3)) {
                    this?.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_tab_library_dark)
                }
            }
        }

        //Check permission
        if (android.os.Build.VERSION.SDK_INT >= 24)
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.INTERNET
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                this.requestPermissions(
                    arrayOf(android.Manifest.permission.INTERNET, android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_CODE
                )
            }

        //Set load list Data Handler
        updateHandler = MainUpdateHandler(this)

        //Load data from YouTube
        reloadData()
    }

    private fun reloadData() {
        if (isLogin!!) {
            //Is Login

        } else {
            //Not Login
            SlardarHTTPSGet.getHTML("https://m.youtube.com", updateHandler, 0)
        }
    }

    internal fun reloadHomeItem() {

        val homeURL: String =
            "https://m.youtube.com/?itct=" + homeItem!!.getString("continuation") + "&ctoken=" + homeItem!!.getString(
                "clickTrackingParams"
            ) + "&pbj=1"
        val header: ArrayList<Pair<String, String>> = ArrayList()
        var index = 0
        while (index < YOUTUBE_HEADER.size) {
            header.add(Pair(YOUTUBE_HEADER[index], YOUTUBE_HEADER[index + 1]))
            index += 2
        }
        SlardarHTTPSGet.getHTML(homeURL, header, updateHandler, 1)
    }

    internal fun reloadTrending() {

        val trendingURL: String =
            "https://m.youtube.com/?itct=" + trendingItem!!.getString("continuation") + "&ctoken=" + trendingItem!!.getString(
                "clickTrackingParams"
            ) + "&pbj=1"
        val header: ArrayList<Pair<String, String>> = ArrayList()
        var index = 0
        while (index < YOUTUBE_HEADER.size) {
            header.add(Pair(YOUTUBE_HEADER[index], YOUTUBE_HEADER[index + 1]))
            index += 2
        }
        SlardarHTTPSGet.getHTML(trendingURL, header, updateHandler, 2)
    }

    internal fun refreshItemList() {
        try {
            reloadHomeItem()
            reloadTrending()
        } catch (ex: Exception) {
            Toast.makeText(this, ex.message, Toast.LENGTH_SHORT).show()
        }
    }

}
