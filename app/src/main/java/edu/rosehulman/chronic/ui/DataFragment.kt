package edu.rosehulman.chronic.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.rosehulman.chronic.adapters.PainDataTabAdapter
import edu.rosehulman.chronic.databinding.FragmentDataBinding
import android.util.Log
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager

import com.google.android.material.tabs.TabLayout
import edu.rosehulman.chronic.R


class DataFragment : Fragment() {

    private lateinit var binding: FragmentDataBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDataBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val fragmentAdapter = PainDataTabAdapter(childFragmentManager)
        binding.ViewPager.adapter = fragmentAdapter


        val tabLayout = binding.Tabs
        tabLayout.setupWithViewPager(binding.ViewPager)
        binding.ViewPager.offscreenPageLimit = 0

        binding.ViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
                Log.d("Chronic","Tab Scrolled")
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                Log.d("Chronic","On Page Scrolled")
                //val oldPosition = binding.ViewPager.currentItem
                //val oldFragment = binding.ViewPager.adapter?.instantiateItem(binding.ViewPager, oldPosition)
                //oldFragment.onPauseStuff() // Hint: do as here call onPause
                //val newFragment = binding.ViewPager.adapter?.instantiateItem(binding.ViewPager, position)
                //newFragment.onResumeStuff() // Hint: do as here call onResume


            }
            override fun onPageSelected(position: Int) {
                Log.d("Chronic","On Page Selected")
                //Tell the adapter to upadate
                fragmentAdapter.notifyDataSetChanged()
            }

        })

        return root
    }
}