package com.example.movieapp

import android.app.Application
import com.example.movieapp.di.appModule
import com.example.movieapp.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
class MovieApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@MovieApp)
            modules(listOf(appModule, viewModelModule))
        }
    }
}