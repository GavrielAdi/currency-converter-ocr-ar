package com.adios.conversee.initializer

import android.content.Context
import androidx.startup.Initializer
import com.unity3d.ads.UnityAds
import dagger.hilt.android.qualifiers.ApplicationContext
import com.adios.conversee.BuildConfig
import com.adios.conversee.di.getEntryPoint
import javax.inject.Inject

class UnityAdsInitializer : Initializer<Unit> {
    @[Inject ApplicationContext]
    lateinit var context: Context

    override fun create(context: Context) {
        context.getEntryPoint().inject(this)
        UnityAds.initialize(
            context = context,
            gameId = BuildConfig.ADS_UNITYGAMEID,
            testMode = BuildConfig.DEBUG
        )
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}
