package cz.dmn.towlogger.core

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LogServiceConnectorTest {

    @Mock lateinit var activity: Activity
    @Mock lateinit var controller: LogService.Controller
    @Mock lateinit var componentName: ComponentName
    lateinit var logServiceConnector: LogServiceConnector
    val testObserver = TestObserver<LogService.Controller>()

    @Before fun setUp() {
        logServiceConnector = LogServiceConnector(activity)
    }

    @Test fun testAttachAndDetach() {
        `when`(activity.bindService(ArgumentMatchers.any<Intent>(), any<ServiceConnection>(), ArgumentMatchers.anyInt()))
                .thenReturn(true)
        val observable = logServiceConnector.attach()
        verifyNoMoreInteractions(activity)
        observable.subscribe(testObserver)
        verify(activity).bindService(ArgumentMatchers.any<Intent>(), ArgumentMatchers.eq(logServiceConnector), ArgumentMatchers.anyInt())
        testObserver.assertNoValues()
        logServiceConnector.onServiceConnected(componentName, controller)
        testObserver.assertComplete()
        testObserver.assertNoErrors()
        logServiceConnector.detach()
        verify(activity).unbindService(logServiceConnector)
    }

    @Test(expected = IllegalStateException::class) fun testDuplicateAttach() {
        `when`(activity.bindService(ArgumentMatchers.any<Intent>(), any<ServiceConnection>(), ArgumentMatchers.anyInt()))
                .thenReturn(true)
        logServiceConnector.attach().subscribe(testObserver)
        logServiceConnector.onServiceConnected(componentName, controller)
        logServiceConnector.attach()
    }

    @Test fun textAttachError() {
        `when`(activity.bindService(ArgumentMatchers.any<Intent>(), any<ServiceConnection>(), ArgumentMatchers.anyInt()))
                .thenReturn(false)
        val observable = logServiceConnector.attach()
        verifyNoMoreInteractions(activity)
        observable.subscribe(testObserver)
        verify(activity).bindService(ArgumentMatchers.any<Intent>(), ArgumentMatchers.eq(logServiceConnector), ArgumentMatchers.anyInt())
        testObserver.assertError { true }
    }
}
