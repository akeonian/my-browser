package com.example.mybrowser.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.mybrowser.INITIAL_URL
import com.example.mybrowser.R
import com.example.mybrowser.databinding.FragmentBrowserBinding
import com.example.mybrowser.ext.browserComponents
import mozilla.components.browser.menu.BrowserMenuBuilder
import mozilla.components.browser.menu.item.BrowserMenuItemToolbar
import mozilla.components.browser.state.selector.selectedTab
import mozilla.components.browser.toolbar.BrowserToolbar
import mozilla.components.browser.toolbar.display.DisplayToolbar
import mozilla.components.browser.toolbar.edit.EditToolbar
import mozilla.components.feature.session.SessionFeature
import mozilla.components.feature.toolbar.ToolbarFeature
import mozilla.components.support.base.feature.ViewBoundFeatureWrapper

class BrowserFragment: Fragment() {

    private var _binding: FragmentBrowserBinding? = null
    private val binding get() = _binding!!

    private val sessionFeature = ViewBoundFeatureWrapper<SessionFeature>()
    private val toolbarFeature = ViewBoundFeatureWrapper<ToolbarFeature>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBrowserBinding.inflate(
            inflater, container, false)

        sessionFeature.set(
            feature = SessionFeature(
                browserComponents.store,
                browserComponents.sessionUseCases.goBack,
                binding.engineView
            ),
            owner = this,
            view = binding.root
        )

        toolbarFeature.set(
            feature = ToolbarFeature(
                binding.toolbar,
                browserComponents.store,
                browserComponents.sessionUseCases.loadUrl
            ),
            owner = this,
            view = binding.root
        )

        initializeToolbar()

        browserComponents.sessionUseCases.loadUrl.invoke(INITIAL_URL)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initializeToolbar() {
        binding.toolbar.display.hint = getString(R.string.toolbar_hint)
        // Set colors and backgrounds
        binding.toolbar.display.colors = DisplayToolbar.Colors(
            Color.WHITE,
            Color.WHITE,
            Color.WHITE,
            Color.WHITE,
            Color.WHITE,
            Color.WHITE,
            Color.WHITE,
            Color.WHITE,
            Color.WHITE,
            Color.WHITE,
        )
        binding.toolbar.setBackgroundResource(R.drawable.background_toolbar)
        binding.toolbar.edit.colors = EditToolbar.Colors(
            Color.WHITE,
            Color.WHITE,
            Color.WHITE,
            Color.WHITE,
            Color.WHITE,
            Color.WHITE
        )

        // Set actions, menu and indicators
        binding.toolbar.display.menuBuilder = getBrowserMenuBuilder()
        val back = BrowserToolbar.TwoStateButton(
            primaryImage = ContextCompat.getDrawable(requireContext(), mozilla.components.ui.icons.R.drawable.mozac_ic_back)!!,
            secondaryImage = ContextCompat.getDrawable(requireContext(), mozilla.components.ui.icons.R.drawable.mozac_ic_back)!!,
            secondaryImageTintResource = mozilla.components.browser.toolbar.R.color.material_on_primary_disabled,
            primaryContentDescription = "Back",
            secondaryContentDescription = "Back",
            isInPrimaryState = { browserComponents.store.state.selectedTab?.content?.canGoBack ?: false }
        ) {
            browserComponents.sessionUseCases.goBack.invoke()
        }
        val forward = BrowserToolbar.TwoStateButton(
            primaryImage = ContextCompat.getDrawable(requireContext(), mozilla.components.ui.icons.R.drawable.mozac_ic_forward)!!,
            secondaryImage = ContextCompat.getDrawable(requireContext(), mozilla.components.ui.icons.R.drawable.mozac_ic_forward)!!,
            secondaryImageTintResource = mozilla.components.browser.toolbar.R.color.material_on_primary_disabled,
            primaryContentDescription = "Forward",
            secondaryContentDescription = "Forward",
            isInPrimaryState = { browserComponents.store.state.selectedTab?.content?.canGoForward ?: false }
        ) {
            browserComponents.sessionUseCases.goForward.invoke()
        }
        binding.toolbar.addNavigationAction(back)
        binding.toolbar.addNavigationAction(forward)

        var showCross = false
        val reload = BrowserToolbar.TwoStateButton(
            primaryImage = ContextCompat.getDrawable(requireContext(), mozilla.components.ui.icons.R.drawable.mozac_ic_refresh)!!,
            primaryContentDescription = "Reload",
            secondaryImage = ContextCompat.getDrawable(requireContext(), mozilla.components.ui.icons.R.drawable.mozac_ic_stop)!!,
            secondaryContentDescription = "Stop",
            isInPrimaryState = { !showCross },
            disableInSecondaryState = false
        ) {
            val loading = browserComponents.store.state.selectedTab?.content?.loading ?: false
            if (loading) browserComponents.sessionUseCases.stopLoading.invoke()
            else browserComponents.sessionUseCases.reload.invoke()
            if (loading != showCross) {
                showCross = loading
                binding.toolbar.invalidateActions()
            }
        }

        binding.toolbar.addBrowserAction(reload)
    }

    private fun getBrowserMenuBuilder(): BrowserMenuBuilder {
        return BrowserMenuBuilder(
            listOf(getBrowserMenuItemToolbar()))
    }

    private fun getBrowserMenuItemToolbar(): BrowserMenuItemToolbar {
        val back = BrowserMenuItemToolbar.Button(
            mozilla.components.ui.icons.R.drawable.mozac_ic_back,
            iconTintColorResource = R.color.menu_icon_color,
            contentDescription = "Back"
        ) {
            browserComponents.sessionUseCases.goBack.invoke()
        }

        val forward = BrowserMenuItemToolbar.Button(
            mozilla.components.ui.icons.R.drawable.mozac_ic_forward,
            iconTintColorResource = R.color.menu_icon_color,
            contentDescription = "Forward"
        ) {
            browserComponents.sessionUseCases.goForward.invoke()
        }
        return BrowserMenuItemToolbar(listOf(back, forward))
    }

}