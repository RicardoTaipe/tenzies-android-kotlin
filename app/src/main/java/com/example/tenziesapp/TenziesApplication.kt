package com.example.tenziesapp

import android.app.Application

class TenziesApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = ServiceLocator.provideAppContainer()
    }
}