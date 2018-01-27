package cz.dmn.towlogger.ui.main

import android.app.Activity
import android.databinding.DataBindingUtil
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.view.View
import cz.dmn.towlogger.R
import cz.dmn.towlogger.core.LocationMonitor
import cz.dmn.towlogger.core.LogServiceConnector
import cz.dmn.towlogger.databinding.ActivityMainBinding
import cz.dmn.towlogger.ui.BaseActivity
import dagger.Binds
import dagger.Module
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Module
    abstract class InjectModule {

        @Binds
        abstract fun provideActivity(activity: MainActivity): Activity
    }

    @Inject lateinit var logServiceConnector: LogServiceConnector
    @Inject lateinit var locationMonitor: LocationMonitor
    lateinit var disposable : Disposable
    lateinit var binding: ActivityMainBinding
    var locationDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.isRunning = ObservableBoolean(false)
    }

    override fun onStart() {
        super.onStart()
        logServiceConnector.attach().subscribe { controller ->
            binding.toggleListener = View.OnClickListener {
                if (binding.isRunning!!.get()) {
                    controller.stop()
                } else {
                    controller.start()
                }
            }
            disposable = logServiceConnector.runningObservable.subscribe { running ->
                binding.isRunning!!.set(running)
            }
        }
        locationDisposable = locationMonitor.observe().subscribe { location ->
            binding.latitude = location.latitude.toString()
            binding.longitude = location.longitude.toString()
            binding.executePendingBindings()
        }
        binding.stopListener = View.OnClickListener {
            locationDisposable?.dispose()
        }
    }

    override fun onStop() {
        super.onStop()
        logServiceConnector.detach()
        disposable.dispose()
        locationDisposable?.dispose()
    }
}