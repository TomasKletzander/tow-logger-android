package cz.dmn.towlogger.core

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.subjects.ReplaySubject
import javax.inject.Inject

class LogServiceConnector @Inject constructor(private val activity: Activity) : ServiceConnection {

    private var serviceController: LogService.Controller? = null
    val runningObservable: Observable<Boolean>
    get() = serviceController?.isRunning ?: Observable.just(false)
    private var connectEmitter: SingleEmitter<LogService.Controller>? = null

    override fun onServiceDisconnected(p0: ComponentName) {
        serviceController = null
    }

    override fun onServiceConnected(p0: ComponentName, p1: IBinder) {
        serviceController = p1 as? LogService.Controller
        serviceController?.let { connectEmitter?.onSuccess(it) }
    }

    fun attach(): Single<LogService.Controller> {
        if (serviceController != null) throw IllegalStateException("Connector already attached")
        return Single.create { emitter ->
            if (!activity.bindService(Intent(activity, LogService::class.java), this, Context.BIND_AUTO_CREATE)) {
                emitter.onError(Error("Failed to connect to service"))
            } else {
                connectEmitter = emitter
            }
        }
    }

    fun detach() {
        if (serviceController != null) {
            serviceController = null
            activity.unbindService(this)
        }
    }
}
