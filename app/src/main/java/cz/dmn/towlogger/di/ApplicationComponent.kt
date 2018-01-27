package cz.dmn.towlogger.di

import com.google.gson.Gson
import cz.dmn.towlogger.TowLoggerApplication
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AndroidInjectionModule::class, ActivityBindingModule::class, ApplicationModule::class, ServiceBindingModule::class))
interface ApplicationComponent {
    fun inject(application: TowLoggerApplication)
    fun gson(): Gson
}
