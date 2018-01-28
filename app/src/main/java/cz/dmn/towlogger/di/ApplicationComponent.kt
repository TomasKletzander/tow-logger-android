package cz.dmn.towlogger.di

import cz.dmn.towlogger.TowLoggerApplication
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AndroidInjectionModule::class, ActivityBindingModule::class, ApplicationModule::class,
        ServiceBindingModule::class, ApiModule::class, ViewModelModule::class))
interface ApplicationComponent {
    fun inject(application: TowLoggerApplication)
}
