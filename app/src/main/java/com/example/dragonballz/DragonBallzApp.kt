package com.example.dragonballz

import android.app.Application
import com.example.dragonballz.dipendencyinjection.databaseModule
import com.example.dragonballz.dipendencyinjection.networkModule
import com.example.dragonballz.dipendencyinjection.repositoryModule
import com.example.dragonballz.dipendencyinjection.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class DragonBallzApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@DragonBallzApp)
            modules(
                networkModule,
                databaseModule,
                repositoryModule,
                viewModelModule
            )
        }
    }
}