package com.miniweather.di

import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides

@Module
interface AppModule {

    companion object {

        @Provides
        fun provideResources(context: Context): Resources = context.resources

    }

}


