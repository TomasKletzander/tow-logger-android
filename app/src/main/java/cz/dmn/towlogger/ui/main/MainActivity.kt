package cz.dmn.towlogger.ui.main

import android.app.Activity
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import cz.dmn.towlogger.R
import cz.dmn.towlogger.core.LogService
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
    lateinit var disposable : Disposable
    lateinit var binding: ActivityMainBinding
    var logServiceController: LogService.Controller? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        MenuInflater(this).inflate(R.menu.main_activity, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        logServiceController?.isRunning?.subscribe { isRunning ->
            menu.findItem(R.id.actionStart).setVisible(!isRunning)
            menu.findItem(R.id.actionStop).setVisible(isRunning)
        }?.dispose()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (super.onOptionsItemSelected(item)) return true
        when (item.itemId) {
            R.id.actionStart -> logServiceController?.start()
            R.id.actionStop -> logServiceController?.stop()
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        logServiceConnector.attach().subscribe { controller ->
            logServiceController = controller
            invalidateOptionsMenu()
            disposable = logServiceConnector.runningObservable.subscribe { running ->
                invalidateOptionsMenu()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        logServiceController = null
        logServiceConnector.detach()
        disposable.dispose()
    }
}
