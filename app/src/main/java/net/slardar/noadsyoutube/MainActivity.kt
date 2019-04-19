package net.slardar.noadsyoutube

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import net.slardar.widget.SlardarFragmentPagerAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var navigationBar: TabLayout
    private lateinit var mainViewPager: ViewPager
    private lateinit var fragmentPagerAdapter: SlardarFragmentPagerAdapter

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

        //Setup TabLayout
        navigationBar.getTabAt(0)?.text = getText(R.string.home)
        navigationBar.getTabAt(1)?.text = getText(R.string.trending)
        navigationBar.getTabAt(2)?.text = getText(R.string.subscriptions)
        navigationBar.getTabAt(3)?.text = getText(R.string.library)

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            navigationBar.getTabAt(0)?.icon = resources.getDrawable(R.drawable.ic_tab_home, theme)
            navigationBar.getTabAt(1)?.icon = resources.getDrawable(R.drawable.ic_tab_trending, theme)
            navigationBar.getTabAt(2)?.icon = resources.getDrawable(R.drawable.ic_tab_subscriptions, theme)
            navigationBar.getTabAt(3)?.icon = resources.getDrawable(R.drawable.ic_tab_library, theme)
        } else {
            navigationBar.getTabAt(0)?.icon = resources.getDrawable(R.drawable.ic_tab_home)
            navigationBar.getTabAt(1)?.icon = resources.getDrawable(R.drawable.ic_tab_trending)
            navigationBar.getTabAt(2)?.icon = resources.getDrawable(R.drawable.ic_tab_subscriptions)
            navigationBar.getTabAt(3)?.icon = resources.getDrawable(R.drawable.ic_tab_library)
        }


        //Setup ViewPager Listener
        navigationBar.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                Log.wtf("onTabSelected", tab.text.toString())
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                Log.wtf("onTabUnselected", tab.text.toString())
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                Log.wtf("onTabReselected", tab.text.toString())
            }
        })

    }
}
