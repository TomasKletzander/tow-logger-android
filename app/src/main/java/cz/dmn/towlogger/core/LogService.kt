package cz.dmn.towlogger.core

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import android.support.v4.app.NotificationCompat
import cz.dmn.towlogger.MONITORING_CHANNEL_ID
import cz.dmn.towlogger.NOTIFICATION_ID_MONITORING
import cz.dmn.towlogger.R
import cz.dmn.towlogger.ui.main.MainActivity
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class LogService : Service() {

    inner class Controller : android.os.Binder() {

        fun start() {
            isRunning.take(1).subscribe { running ->
                if (!running) {
                    startService(Intent(this@LogService, LogService::class.java))
                    this@LogService.isRunningSubject.onNext(true)
                }
            }
        }

        fun stop() {
            isRunning.take(1).subscribe { running ->
                if (running) {
                    cleanupMonitoring()
                }
            }
        }

        val isRunning: Observable<Boolean>
        get() = this@LogService.isRunningSubject
    }

    val isRunningSubject = BehaviorSubject.createDefault(false)
    lateinit var wakeLock: PowerManager.WakeLock
    @Inject lateinit var towDetector: TowDetector
    @Inject lateinit var locationMonitor: LocationMonitor
    @Inject lateinit var igcLogger: IgcLogger
    @Inject lateinit var towDatabase: TowDatabase
    private val monitoringDisposables = CompositeDisposable()

    override fun onBind(p0: Intent?): IBinder = Controller()

    override fun onCreate() {
        super.onCreate()
        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LogServiceLock")
        AndroidInjection.inject(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initializeMonitoring()
        return START_STICKY
    }

    private fun initializeMonitoring() {
        wakeLock.acquire()
        monitoringDisposables.add(towDetector.observe().subscribe {

            //  Save tow record to database
            it.record?.apply { towDatabase.addRecord(this) }
        })
        val notification = NotificationCompat.Builder(this, MONITORING_CHANNEL_ID)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setColor(resources.getColor(R.color.colorAccent))
                .setContentIntent(PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), 0))
                .setContentTitle(getString(R.string.notification_monitoring_title))
                .setContentText(getString(R.string.notification_monitoring_text))
                .setLocalOnly(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build()
        startForeground(NOTIFICATION_ID_MONITORING, notification)
        monitoringDisposables.add(locationMonitor.observe().subscribe {
            igcLogger.logLocation(it.latitude, it.longitude, it.altitude)
        })
    }

    private fun cleanupMonitoring() {
        stopForeground(true)
        stopSelf()
        monitoringDisposables.dispose()
        this@LogService.isRunningSubject.onNext(false)
        wakeLock.release()
    }
}
