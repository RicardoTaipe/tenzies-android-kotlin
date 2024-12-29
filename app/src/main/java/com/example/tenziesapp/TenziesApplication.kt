package com.example.tenziesapp

import android.app.Application

class TenziesApplication : Application() {
    lateinit var diceGenerator: DiceGenerator
    override fun onCreate() {
        super.onCreate()
        diceGenerator = ServiceLocator.provideDiceGenerator()
    }
}