package cz.dmn.towlogger.di

import cz.dmn.towlogger.ui.main.MainActivity
import cz.dmn.towlogger.ui.pickpilot.PickPilotActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

    @ContributesAndroidInjector(modules = arrayOf(MainActivity.InjectModule::class))
    abstract fun contributeMainActivityInjector(): MainActivity

    @ContributesAndroidInjector
    abstract fun contributePickPilotActivityInjector(): PickPilotActivity
}
