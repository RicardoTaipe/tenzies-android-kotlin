package com.example.tenziesapp

object ServiceLocator {
    fun provideAppContainer(): AppContainer = AppContainerImp()
}