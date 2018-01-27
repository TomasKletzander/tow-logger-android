package cz.dmn.towlogger.core

import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LocationMonitorTest {

    @Mock lateinit var locationProviderClient: FusedLocationProviderClient
    lateinit var locationMonitor: LocationMonitor

    @Before
    fun setUp() {
        locationMonitor = LocationMonitor(locationProviderClient)
    }

    @Test
    fun shouldUnregisterWhenUnsubscribedAll() {
        locationMonitor.observe()
        verify(locationProviderClient, times(0)).requestLocationUpdates(ArgumentMatchers.any<LocationRequest>(),
                ArgumentMatchers.any<LocationCallback>(), ArgumentMatchers.any<Looper>())
        val disposable1 = locationMonitor.observe().subscribe()
        verify(locationProviderClient).requestLocationUpdates(ArgumentMatchers.any<LocationRequest>(),
                ArgumentMatchers.eq<LocationCallback>(locationMonitor), ArgumentMatchers.eq<Looper>(null))
        verifyNoMoreInteractions(locationProviderClient)
        val disposable2 = locationMonitor.observe().subscribe()
        verifyNoMoreInteractions(locationProviderClient)
        disposable1.dispose()
        verifyNoMoreInteractions(locationProviderClient)
        disposable2.dispose()
        verify(locationProviderClient).removeLocationUpdates(locationMonitor)
        verifyNoMoreInteractions(locationProviderClient)
    }
}
