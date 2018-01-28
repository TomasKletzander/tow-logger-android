package cz.dmn.towlogger.core

import android.location.Location
import cz.dmn.towlogger.data.TowProgress
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class TowDetectorTest {

    @Mock lateinit var locationMonitor: LocationMonitor
    @Mock lateinit var settings: Settings
    @Mock lateinit var towAttributes: TowAttributes
    @Mock lateinit var locationObservable: Observable<Location>
    @Mock lateinit var locationDisposable: Disposable
    lateinit var towDetector: TowDetector
    val testObserver = TestObserver<TowProgress>()

    @Before fun setUp() {
        towDetector = TowDetector(locationMonitor, settings, towAttributes)
        `when`(locationObservable.subscribe(ArgumentMatchers.any<(t: Location) -> Unit>())).thenReturn(locationDisposable)
        `when`(locationMonitor.observe()).thenReturn(locationObservable)
    }

    @Test fun shouldDisposeLocationMonitorCorrectly() {
        val observable = towDetector.progress
        verifyNoMoreInteractions(locationMonitor)
        observable.subscribe(testObserver)
        verifyNoMoreInteractions(locationMonitor)
        towDetector.start()
        verify(locationMonitor).observe()
        verifyNoMoreInteractions(locationMonitor)
        testObserver.cancel()
        verifyNoMoreInteractions(locationMonitor)
        towDetector.stop()
        verify(locationDisposable).dispose()
    }
}
