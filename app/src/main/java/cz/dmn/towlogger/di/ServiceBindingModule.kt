package cz.dmn.towlogger.di

import cz.dmn.towlogger.core.LogService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServiceBindingModule {

    @ContributesAndroidInjector
    abstract fun contributeLogServiceInjector(): LogService
}
