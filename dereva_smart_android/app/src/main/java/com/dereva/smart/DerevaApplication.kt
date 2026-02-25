package com.dereva.smart

import android.app.Application
import com.dereva.smart.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class DerevaApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        // Initialize Koin
        startKoin {
            androidLogger()
            androidContext(this@DerevaApplication)
            modules(appModule)
        }
        
        Timber.d("Dereva Smart Application initialized")
    }
}
