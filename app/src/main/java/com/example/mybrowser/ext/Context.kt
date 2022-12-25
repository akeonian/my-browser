package com.example.mybrowser.ext

import android.content.Context
import com.example.mybrowser.BrowserApplication
import com.example.mybrowser.BrowserComponents

val Context.browserApplication: BrowserApplication
    get() = applicationContext as BrowserApplication

val Context.browserComponents: BrowserComponents
    get() = browserApplication.browserComponents