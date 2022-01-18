package edu.rosehulman.chronic.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.rosehulman.chronic.adapters.PainDataTabAdapter
import edu.rosehulman.chronic.databinding.FragmentDataBinding
import android.R

import com.google.android.material.tabs.TabLayout




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





        return root
    }
}