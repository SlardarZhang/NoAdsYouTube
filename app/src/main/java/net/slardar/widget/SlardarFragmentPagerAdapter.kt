package net.slardar.widget

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.SparseArray

class SlardarFragmentPagerAdapter : FragmentPagerAdapter {

    private var fragments: SparseArray<Fragment?>
    private lateinit var context: Context

    constructor(fragmentManager: FragmentManager?, context: Context?) : super(fragmentManager) {
        if (context != null)
            this.context = context
        fragments = SparseArray()
    }

    override fun getCount(): Int {
        return fragments.size()
    }

    override fun getItem(index: Int): Fragment? {
        return fragments.get(index)
    }

    fun addItem(fragment: Fragment?) {
        fragments.put(fragments.size(), fragment)
    }

    fun addItem(fragment: Fragment?, index: Int?) {
        if (index == null) {
            throw Exception("Index is null")
        } else {
            fragments.put(index, fragment)
        }
    }
}