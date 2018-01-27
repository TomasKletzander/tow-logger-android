package cz.dmn.towlogger.core

import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.disposables.Disposable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationMonitor @Inject constructor(private val locationProviderClient: FusedLocationProviderClient) : LocationCallback() {

    private val emitters = mutableListOf<ObservableEmitter<Location>>()

    override fun onLocationResult(locationResult: LocationResult) {
        emitters.forEach { emitter -> emitter.onNext(locationResult.lastLocation) }
    }

    fun observe(): Observable<Location> {
        return Observable.create<Location> { emitter ->
            if (emitters.isEmpty()) {
                val locationRequest = LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setFastestInterval(0L)
                        .setInterval(0L)
                locationProviderClient.requestLocationUpdates(locationRequest, this, null)
            }
            emitters.add(emitter)
            emitter.setDisposable(object : Disposable {
                override fun isDisposed() = emitters.contains(emitter)

                override fun dispose() {
                    emitters.remove(emitter)
                    if (emitters.isEmpty()) {
                        locationProviderClient.removeLocationUpdates(this@LocationMonitor)
                    }
                }
            })
        }
    }
}
