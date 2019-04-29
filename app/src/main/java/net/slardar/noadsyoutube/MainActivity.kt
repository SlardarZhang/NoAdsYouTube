package net.slardar.noadsyoutube

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import net.slardar.widget.SlardarFragmentPagerAdapter
import net.slardar.widget.SlardarHTTPSGet
import org.json.JSONArray
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var navigationBar: TabLayout
    private lateinit var mainViewPager: ViewPager
    private lateinit var fragmentPagerAdapter: SlardarFragmentPagerAdapter

    private var displayTheme: Int = 0
    private var isLogin: Boolean = false

    private lateinit var homeFragment: HomeFragment
    private lateinit var trendingFragment: TrendingFragment
    private lateinit var subscriptionsFragment: SubscriptionsFragment
    private lateinit var libraryFragment: LibraryFragment

    private lateinit var updateHandler: MainUpdateHandler

    private var tabs: JSONArray? = null


    companion object {
        private val YOUTUBE_HEADER: Array<String> =
            arrayOf(
                "User-Agent", "Mozilla/5.0 (Linux; Android 5.0)",
                "X-YouTube-Client-Name", "2",
                "X-YouTube-Client-Version", "2.20190419"
            )

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        displayTheme = savedInstanceState?.getInt("displayTheme")!!
        isLogin = savedInstanceState.getBoolean("isLogin")
        if (savedInstanceState.getString("tabs") != null) {
            tabs = JSONArray(savedInstanceState.getString("tabs"))
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putInt("displayTheme", displayTheme)
        outState?.putBoolean("isLogin", isLogin)
        outState?.putString("tabs", tabs.toString())
        super.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Associate views
        mainViewPager = findViewById(R.id.main_viewPager)
        navigationBar = findViewById(R.id.navigation_bar)

        //Initial FragmentPagerAdaptor
        fragmentPagerAdapter = SlardarFragmentPagerAdapter(supportFragmentManager, this)

        homeFragment = HomeFragment()
        trendingFragment = TrendingFragment()
        subscriptionsFragment = SubscriptionsFragment()
        libraryFragment = LibraryFragment()

        homeFragment.setTopRefresh {
            reloadData(1)
        }
        trendingFragment.setTopRefresh {
            reloadData(2)
        }

        subscriptionsFragment.setTopRefresh {
            reloadData(3)
        }


        homeFragment.baseContext = baseContext
        trendingFragment.baseContext = baseContext
        subscriptionsFragment.baseContext = baseContext

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
                            0 -> tab.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_home_light)
                            1 -> tab.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_home_red)
                        }
                    }
                    1 -> {
                        when (displayTheme) {
                            0 -> tab.icon =
                                ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_trending_light)
                            1 -> tab.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_trending_red)
                        }
                    }
                    2 -> {
                        when (displayTheme) {
                            0 -> tab.icon =
                                ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_subscriptions_light)
                            1 -> tab.icon =
                                ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_subscriptions_red)
                        }
                    }
                    3 -> {
                        when (displayTheme) {
                            0 -> tab.icon =
                                ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_library_light)
                            1 -> tab.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_library_red)
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        tab.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_home_dark)
                    }
                    1 -> {
                        when (displayTheme) {
                            0 -> tab.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_trending_dark)
                            1 -> tab.icon =
                                ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_trending_dark_light)
                        }

                    }
                    2 -> {
                        when (displayTheme) {
                            0 -> tab.icon =
                                ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_subscriptions_dark)
                            1 -> tab.icon =
                                ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_subscriptions_dark_light)
                        }

                    }
                    3 -> {
                        tab.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_library_dark)
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

        homeFragment.setTheme(displayTheme)
        trendingFragment.setTheme(displayTheme)
        subscriptionsFragment.setTheme(displayTheme)

        //libraryFragment.setTheme(displayTheme)

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
                    this?.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_home_light)
                }
                with(navigationBar.getTabAt(1)) {
                    this?.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_trending_dark)
                }
                with(navigationBar.getTabAt(2)) {
                    this?.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_subscriptions_dark)
                }
                with(navigationBar.getTabAt(3)) {
                    this?.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_library_dark)
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
                    this?.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_home_red)
                }
                with(navigationBar.getTabAt(1)) {
                    this?.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_trending_dark_light)
                }
                with(navigationBar.getTabAt(2)) {
                    this?.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_subscriptions_dark_light)
                }
                with(navigationBar.getTabAt(3)) {
                    this?.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_library_dark)
                }
            }
        }

        //Set load list Data Handler
        updateHandler = MainUpdateHandler(this)

        //Load data from YouTube
        when (tabs == null) {
            true -> reloadData()
        }
    }

    fun reloadData(updateIndex: Int = 0) {
        when (updateIndex) {
            0 -> {
                homeFragment.setLoading(true)
                trendingFragment.setLoading(true)
                subscriptionsFragment.setLoading(true)
            }
            1 -> homeFragment.setLoading(true)
            2 -> trendingFragment.setLoading(true)
            3 -> subscriptionsFragment.setLoading(true)
        }

        val languageHeader: ArrayList<Pair<String, String>> = ArrayList()
        languageHeader.add(Pair("accept-language", Locale.getDefault().toString()))
        if (isLogin) {
            //Is Login

        } else {
            //Not Login
            SlardarHTTPSGet.getStringThread("https://m.youtube.com", languageHeader, updateHandler, updateIndex)
        }
    }

    fun reloaded(tabs: JSONArray, updateIndex: Int) {
        this.tabs = tabs
        for (index in 0 until tabs.length()) {
            when (tabs.getJSONObject(index).getJSONObject("tabRenderer").getString("title")) {
                resources.getString(R.string.home) -> {
                    if (updateIndex == 0 || updateIndex == 1) {
                        homeFragment.load(
                            tabs.getJSONObject(index).getJSONObject("tabRenderer")
                                .getJSONObject("content").getJSONObject("sectionListRenderer")
                                .getJSONArray("continuations").getJSONObject(0)
                                .getJSONObject("reloadContinuationData")
                        )
                    }
                }
                resources.getString(R.string.trending) -> {
                    if (updateIndex == 0 || updateIndex == 2) {
                        trendingFragment.load(
                            tabs.getJSONObject(index).getJSONObject("tabRenderer")
                                .getJSONObject("content").getJSONObject("sectionListRenderer")
                                .getJSONArray("continuations").getJSONObject(0)
                                .getJSONObject("reloadContinuationData")
                        )
                    }
                }
            }
        }
    }

    fun getDisplayTheme(): Int {
        return displayTheme
    }

}
