package com.adios.conversee.di

import android.content.Context
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import com.adios.conversee.initializer.UnityAdsInitializer

@EntryPoint
@InstallIn(SingletonComponent::class)
interface HiltEntryPoint {
    fun inject(initializer: UnityAdsInitializer)
}

fun Context.getEntryPoint(): HiltEntryPoint =
    EntryPointAccessors.fromApplication(
        context = this,
        entryPoint = HiltEntryPoint::class.java
    )
