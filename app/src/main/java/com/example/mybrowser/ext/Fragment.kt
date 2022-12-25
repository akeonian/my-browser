package com.example.mybrowser.ext

import androidx.fragment.app.Fragment
import com.example.mybrowser.BrowserComponents

val Fragment.browserComponents: BrowserComponents
    get() = context!!.browserComponents