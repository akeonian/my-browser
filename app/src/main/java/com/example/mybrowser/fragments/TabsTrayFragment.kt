package com.example.mybrowser.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mybrowser.R
import mozilla.components.ui.icons.R as iconsR
import com.example.mybrowser.databinding.FragmentTabsTrayBinding
import mozilla.components.feature.tabs.tabstray.TabsFeature
import mozilla.components.support.base.feature.ViewBoundFeatureWrapper

class TabsTrayFragment: Fragment() {

    private val tabsFeature = ViewBoundFeatureWrapper<TabsFeature>()
    private val tabsToolbarFeature = ViewBoundFeatureWrapper<TabsFeature>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentTabsTrayBinding.inflate(
            inflater, container, false)

        binding.toolbar.setNavigationIcon(iconsR.drawable.mozac_ic_back)
        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.new_tab -> {
                    TODO("Not yet implemented")
                }
                else -> false
            }
        }


        return binding.root
    }
}