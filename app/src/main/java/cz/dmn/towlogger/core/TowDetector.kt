package cz.dmn.towlogger.core

import android.location.Location
import cz.dmn.towlogger.data.TowProgress
import cz.dmn.towlogger.data.db.TowRecord
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import java.util.Date
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
    private val progressSubject = BehaviorSubject.createDefault(TowProgress.createOff())
    private var locationDisposable: Disposable? = null
    val progress: Observable<TowProgress>
    get() = progressSubject

    fun start() {
        if (locationDisposable != null) return
        progressSubject.onNext(TowProgress.createIdle())
        locationDisposable = locationMonitor.observe().subscribe {
            handleLocation(it)
        }
    }

    fun stop() {
        locationDisposable?.dispose()
        locationDisposable = null
        towInProgress = false
        progressSubject.onNext(TowProgress.createOff())
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
            val towRecord = TowRecord(0, towAttributes.towPilot, towAttributes.gliderPilot, towAttributes.payer, date, time,
                    System.currentTimeMillis() - towStartTime, towMaxAltitude - towMinAltitude)
            towInProgress = false
            progressSubject.onNext(TowProgress.createTowFinished(towRecord))
        } else {
            towMinAltitude = Math.min(towMinAltitude, location.altitude)
            towMaxAltitude = Math.max(towMaxAltitude, location.altitude)
            progressSubject.onNext(TowProgress.createTowing(System.currentTimeMillis() - towStartTime,
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
            progressSubject.onNext(TowProgress.createTowing(0, 0))
        } else {
            progressSubject.onNext(TowProgress.createIdle())
        }
    }
}
