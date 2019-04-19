package net.slardar.noadsyoutube

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import net.slardar.widget.SlardarFragmentPagerAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var navigationBar: TabLayout
    private lateinit var mainViewPager: ViewPager
    private lateinit var fragmentPagerAdapter: SlardarFragmentPagerAdapter
    private var displayTheme: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Associate views
        mainViewPager = findViewById(R.id.main_viewPager)
        navigationBar = findViewById(R.id.navigation_bar)

        //Initial FragmentPagerAdaptor
        fragmentPagerAdapter = SlardarFragmentPagerAdapter(supportFragmentManager, this)
        fragmentPagerAdapter.addItem(HomeFragment())
        fragmentPagerAdapter.addItem(TrendingFragment())
        fragmentPagerAdapter.addItem(SubscriptionsFragment())
        fragmentPagerAdapter.addItem(LibraryFragment())

        //Setup tab with adapter
        navigationBar.setupWithViewPager(mainViewPager)
        mainViewPager.adapter = fragmentPagerAdapter


        refreshHomeList()

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
        val sharedPreferences: SharedPreferences? = this.getPreferences(Context.MODE_PRIVATE)
        //Load Settings
        if (sharedPreferences != null) {
            displayTheme = sharedPreferences.getInt(getString(R.string.theme), -1)
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


        //Setup TabLayout
        navigationBar.getTabAt(0)?.text = getText(R.string.home)
        navigationBar.getTabAt(1)?.text = getText(R.string.trending)
        navigationBar.getTabAt(2)?.text = getText(R.string.subscriptions)
        navigationBar.getTabAt(3)?.text = getText(R.string.library)

        Log.wtf("Theme", displayTheme.toString())
        when (displayTheme) {
            0 -> {
                navigationBar.setBackgroundResource(R.color.dark_background_color)

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
    }

    fun refreshHomeList() {

    }
}
