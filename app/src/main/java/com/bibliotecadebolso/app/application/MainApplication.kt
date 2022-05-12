package com.bibliotecadebolso.app.application

import android.app.Application
import com.bibliotecadebolso.app.util.WifiService

class MainApplication : Application() {
    companion object {
        lateinit var instance: MainApplication
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        setupService()
    }

    private fun setupService() {
        WifiService.instance.initializeWithApplicationContext(this)
    }
}