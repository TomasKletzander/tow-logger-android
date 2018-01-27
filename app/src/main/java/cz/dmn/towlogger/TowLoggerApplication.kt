package cz.dmn.towlogger

import android.app.Activity
import android.app.Application
import android.app.Service
import com.crashlytics.android.Crashlytics
import cz.dmn.towlogger.di.ApplicationModule
import cz.dmn.towlogger.di.DaggerApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasServiceInjector
import io.fabric.sdk.android.Fabric
import javax.inject.Inject

class TowLoggerApplication : Application(), HasActivityInjector, HasServiceInjector {

    @Inject
    lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var dispatchingServiceInjector: DispatchingAndroidInjector<Service>

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
        DaggerApplicationComponent.builder().applicationModule(ApplicationModule(this)).build().inject(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> = dispatchingActivityInjector

    override fun serviceInjector(): AndroidInjector<Service> = dispatchingServiceInjector
}
