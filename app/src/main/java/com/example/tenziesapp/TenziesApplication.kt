package com.example.tenziesapp

import android.app.Application

class TenziesApplication : Application() {
    val gameManager: GameManager
        get() = ServiceLocator.provideGameManager()

    override fun onCreate() {
        super.onCreate()
    }
}