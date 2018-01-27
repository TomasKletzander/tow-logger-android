package cz.dmn.towlogger.core

import android.location.Location
import cz.dmn.towlogger.data.TowProgress
import cz.dmn.towlogger.data.db.TowRecord
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.disposables.Disposable
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TowDetector @Inject constructor(private val locationMonitor: LocationMonitor,
                                      private val settings: Settings,
                                      private val towAttributes: TowAttributes) {

    private var towInProgress = false
    private var towStartTime = 0L
    private var towMinAltitude = 0.0
    private var towMaxAltitude = 0.0
    private val emitters = mutableListOf<ObservableEmitter<TowProgress>>()
    private var locationDisposable: Disposable? = null

    fun observe(): Observable<TowProgress> = Observable.create { emitter ->
        if (emitters.isEmpty()) {
            locationDisposable = locationMonitor.observe().subscribe {
                handleLocation(it)
            }
        }
        emitters.add(emitter)
        emitter.setDisposable(object : Disposable {
            override fun isDisposed() = emitters.isEmpty()

            override fun dispose() {
                emitters.remove(emitter)
                if (emitters.isEmpty()) {
                    locationDisposable?.dispose()
                    locationDisposable = null
                    towInProgress = false
                }
            }
        })
    }

    private fun handleLocation(location: Location) {
        //  Handle location differently based on current state
        if (towInProgress) {
            handleTowLocation(location)
        } else {
            handleIdleLocation(location)
        }
    }

    private fun handleTowLocation(location: Location) {

        if (location.speed < settings.landingSpeed) {

            //  Finalize tow
            val currentDate = Date()
            val date = (currentDate.year + 1900) * 10000 +
                    (currentDate.month + 1) * 100 +
                    currentDate.day
            val time = currentDate.hours * 100 +
                    currentDate.minutes
            val towRecord = TowRecord(0, towAttributes.pilotName, towAttributes.payerName, date, time,
                    System.currentTimeMillis() - towStartTime, towMaxAltitude - towMinAltitude)
            towInProgress = false
            publishTowProgress(TowProgress.createTowFinished(towRecord))
        } else {
            towMinAltitude = Math.min(towMinAltitude, location.altitude)
            towMaxAltitude = Math.max(towMaxAltitude, location.altitude)
            publishTowProgress(TowProgress.createTowing(System.currentTimeMillis() - towStartTime,
                    (towMaxAltitude - towMinAltitude).toInt()))
        }
    }

    private fun handleIdleLocation(location: Location) {

        if (location.speed > settings.takeoffSpeed) {

            //  Save all takeoff information
            towStartTime = System.currentTimeMillis()
            towMinAltitude = location.altitude
            towMaxAltitude = location.altitude
            towInProgress = true
            publishTowProgress(TowProgress.createTowing(0, 0))
        } else {
            publishTowProgress(TowProgress.createIdle())
        }
    }

    private fun publishTowProgress(towProgress: TowProgress) {
        emitters.forEach { it.onNext(towProgress) }
    }
}
