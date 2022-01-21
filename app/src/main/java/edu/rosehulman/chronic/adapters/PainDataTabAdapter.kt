package edu.rosehulman.chronic.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import edu.rosehulman.chronic.ui.DataCalenderFragment
import edu.rosehulman.chronic.ui.DataListFragment

class PainDataTabAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        var outputFragment: Fragment = DataCalenderFragment()

        when (position) {
            0 -> {
                outputFragment = DataCalenderFragment()
            }
            1 -> {
                outputFragment = DataListFragment()
            }
            else -> {
                outputFragment = DataCalenderFragment()
            }
        }
        return outputFragment
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Calender View"
            1 -> "List View"
            else -> {
                return "Calender View"
            }
        }
    }
}