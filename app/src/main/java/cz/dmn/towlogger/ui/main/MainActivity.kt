package cz.dmn.towlogger.ui.main

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import cz.dmn.towlogger.R
import cz.dmn.towlogger.core.LogService
import cz.dmn.towlogger.core.LogServiceConnector
import cz.dmn.towlogger.core.TowAttributes
import cz.dmn.towlogger.databinding.ActivityMainBinding
import cz.dmn.towlogger.ui.BaseActivity
import cz.dmn.towlogger.ui.Navigator
import cz.dmn.towlogger.ui.pickpilot.PickPilotActivity
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Module
    class InjectModule {
        
        @Provides
        fun provideActivity(activity: MainActivity): Activity = activity
    }

    @Inject lateinit var logServiceConnector: LogServiceConnector
    @Inject lateinit var model: MainActivityModel
    @Inject lateinit var navigator: Navigator
    @Inject lateinit var towAttributes: TowAttributes
    var runningDisposable: Disposable? = null
    lateinit var binding: ActivityMainBinding
    var logServiceController: LogService.Controller? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setTitle(R.string.app_name)
        model.updateFromTowAttributes()
        binding.model = model
        binding.setTowPilotClickListener {
            navigator.pickTowPilot(towAttributes.towPilot)
        }
        binding.setGliderPilotClickListener {
            navigator.pickGliderPilot(towAttributes.gliderPilot)
        }
        binding.setPayerClickListener {
            navigator.pickPayer(towAttributes.payer)
        }
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
            R.id.actionStop -> {
                logServiceController?.stop()
                model.stateText.set(getString(R.string.stateOff))
            }
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        logServiceConnector.attach().subscribe { controller ->
            logServiceController = controller
            invalidateOptionsMenu()
            runningDisposable = logServiceConnector.runningObservable.subscribe { running ->
                invalidateOptionsMenu()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        logServiceController = null
        logServiceConnector.detach()
        runningDisposable?.dispose()
        runningDisposable = null
    }

    override fun onDestroy() {
        super.onDestroy()
        model.dispose()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Navigator.REQUEST_ID_PICK_TOW_PILOT -> towAttributes.towPilot = data?.getStringExtra(PickPilotActivity.EXTRA_PILOT_NAME) ?: ""
                Navigator.REQUEST_ID_PICK_GLIDER_PILOT -> {
                    towAttributes.gliderPilot = data?.getStringExtra(PickPilotActivity.EXTRA_PILOT_NAME) ?: ""
                    towAttributes.payer = towAttributes.gliderPilot
                }
                Navigator.REQUEST_ID_PICK_PAYER -> towAttributes.payer = data?.getStringExtra(PickPilotActivity.EXTRA_PILOT_NAME) ?: ""
            }
            model.updateFromTowAttributes()
        }
    }
}
