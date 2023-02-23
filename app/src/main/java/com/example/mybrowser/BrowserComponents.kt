package com.example.mybrowser

import android.content.Context
import mozilla.components.browser.engine.system.SystemEngine
import mozilla.components.browser.session.storage.SessionStorage
import mozilla.components.browser.state.engine.EngineMiddleware
import mozilla.components.browser.state.store.BrowserStore
import mozilla.components.browser.thumbnails.storage.ThumbnailStorage
import mozilla.components.concept.engine.DefaultSettings
import mozilla.components.concept.engine.Engine
import mozilla.components.concept.engine.mediaquery.PreferredColorScheme
import mozilla.components.feature.session.SessionUseCases
import mozilla.components.feature.tabs.TabsUseCases

class BrowserComponents(applicationContext: Context) {

    private val engineSettings by lazy {
        DefaultSettings().apply {
            remoteDebuggingEnabled = true
            supportMultipleWindows = true
            preferredColorScheme = PreferredColorScheme.System
            httpsOnlyMode = Engine.HttpsOnlyMode.ENABLED
        }
    }

    val engine: Engine by lazy {
        SystemEngine(applicationContext, engineSettings)
    }

    val sessionStorage by lazy { SessionStorage(applicationContext, engine) }

    val thumbnailStorage by lazy { ThumbnailStorage(applicationContext) }

    val store by lazy {
        BrowserStore(middleware = EngineMiddleware.create(engine))
    }

    val sessionUseCases by lazy { SessionUseCases(store) }

    // TODO: Initialize tabsUseCases
    val tabsUseCases: TabsUseCases by lazy {
        TabsUseCases(store)
    }
}