package com.gifapplication.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.gifapplication.ui.savedgif.SavedGifFragment
import com.gifapplication.ui.trendinggif.TrendingGifFragment

class PagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                TrendingGifFragment()
            }
            1 -> {
                SavedGifFragment()
            }
            else -> {
                TrendingGifFragment()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> {
                return "Trending Gifs"
            }
            1 -> {
                return "Saved Gifs"
            }
        }
        return super.getPageTitle(position)
    }

}