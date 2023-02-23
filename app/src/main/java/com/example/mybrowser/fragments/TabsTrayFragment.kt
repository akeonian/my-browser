package com.example.mybrowser.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mybrowser.R
import mozilla.components.ui.icons.R as iconsR
import com.example.mybrowser.databinding.FragmentTabsTrayBinding
import com.example.mybrowser.ext.browserComponents
import com.google.android.material.snackbar.Snackbar
import mozilla.components.browser.state.state.TabSessionState
import mozilla.components.feature.tabs.TabsUseCases
import mozilla.components.feature.tabs.tabstray.TabsFeature
import mozilla.components.browser.tabstray.TabsAdapter
import mozilla.components.browser.tabstray.TabsTray
import mozilla.components.browser.thumbnails.loader.ThumbnailLoader
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
        binding.toolbar.setNavigationOnClickListener {
            closeTabsTray()
        }
        binding.toolbar.inflateMenu(R.menu.tabs_tray)
        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.new_tab -> {
                    browserComponents.tabsUseCases.addTab.invoke("about:blank", selectTab = true)
                    closeTabsTray()
                }
            }
            true
        }

        val tabsAdapter = createTabsAdapter(binding.root)

        tabsFeature.set(
            feature = TabsFeature(
                tabsTray = tabsAdapter,
                store = browserComponents.store,
                onCloseTray = ::closeTabsTray
            ),
            owner = this,
            view = binding.root
        )
        return binding.root
    }

    private fun createTabsAdapter(view: View): TabsAdapter {
        val removeUseCase = RemoveTabWithUndoUseCase(
            browserComponents.tabsUseCases.removeTab,
            view,
            browserComponents.tabsUseCases.undo
        )
        return TabsAdapter(
            thumbnailLoader = ThumbnailLoader(browserComponents.thumbnailStorage),
            delegate = object : TabsTray.Delegate {

                override fun onTabSelected(tab: TabSessionState, source: String?) {
                    browserComponents.tabsUseCases.selectTab(tab.id)
                }

                override fun onTabClosed(tab: TabSessionState, source: String?) {
                    removeUseCase.invoke(tab.id)
                }
            }
        )
    }

    private fun closeTabsTray() {
        findNavController().navigateUp()
    }

}

class RemoveTabWithUndoUseCase(
    private val actual: TabsUseCases.RemoveTabUseCase,
    private val view: View,
    private val undo: TabsUseCases.UndoTabRemovalUseCase,
) : TabsUseCases.RemoveTabUseCase {

    override fun invoke(tabId: String) {
        actual.invoke(tabId)
        showSnackbar()
    }

    private fun showSnackbar() {
        Snackbar.make(
            view,
            "Tab removed",
            Snackbar.LENGTH_SHORT
        ).setAction("Undo") {
            undo.invoke()
        }.show()
    }
}
