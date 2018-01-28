package cz.dmn.towlogger.di

import android.app.Application
import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import cz.dmn.towlogger.TowLoggerApplication
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = arrayOf(AndroidInjectionModule::class))
class ApplicationModule(private val application: TowLoggerApplication) {

    @Provides
    @Singleton
    fun provideTowLoggerApplication() = application

    @Provides
    @Singleton
    fun provideApplication(): Application = application

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient() = FusedLocationProviderClient(application)

    @Provides
    @Named("TowAttributes")
    fun provideTowAttributesPreferences() = application.getSharedPreferences("TowAttributes", Context.MODE_PRIVATE)
}
