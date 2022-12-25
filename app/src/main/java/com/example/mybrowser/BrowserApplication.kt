package com.example.mybrowser

import android.app.Application

class BrowserApplication: Application() {

    val browserComponents by lazy { BrowserComponents(this) }

}