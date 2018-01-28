package cz.dmn.towlogger.ui.main

import cz.dmn.towlogger.R
import cz.dmn.towlogger.TowLoggerApplication
import cz.dmn.towlogger.core.TowAttributes
import cz.dmn.towlogger.core.TowDetector
import cz.dmn.towlogger.data.TowProgress
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
@Ignore //  Ignore this unit test until I find solution for mocking lambda calls
class MainActivityModelTest {

    companion object {
        val TOW_PILOT = "Pavel Schoř"
        val GLIDER_PILOT = "Tomáš Kletzander"
        val PAYER = "Tomáš Kletzander"
        val GLIDER = "Astir CS (OK-1234)"
        val NOT_SET = "Not set"
    }

    lateinit var model: MainActivityModel
    @Mock lateinit var application: TowLoggerApplication
    @Mock lateinit var towAttributes: TowAttributes
    @Mock lateinit var towDetector: TowDetector
    @Mock lateinit var progressObservable: Observable<TowProgress>
    @Mock lateinit var progressDisposable: Disposable
    @Mock lateinit var subscribeCall: (TowProgress) -> Unit

    @Before fun setUp() {
        `when`(application.getString(R.string.notSet)).thenReturn(NOT_SET)
        `when`(towAttributes.towPilot).thenReturn(TOW_PILOT)
        `when`(towAttributes.gliderPilot).thenReturn(GLIDER_PILOT)
        `when`(towAttributes.payer).thenReturn(PAYER)
        `when`(towAttributes.glider).thenReturn(GLIDER)
        `when`(towDetector.progress).thenReturn(progressObservable)
        `when`(progressObservable.subscribe(subscribeCall)).thenReturn(progressDisposable)
        model = MainActivityModel(application, towAttributes, towDetector)
    }

    @Test fun shouldSubscribeAndUnsubscribe() {
        verify(progressObservable).subscribe { }
        model.dispose()
        verify(progressDisposable).dispose()
    }

    @Test fun shouldDisplayTowAttributes() {
        model.updateFromTowAttributes()
        assertEquals(model.towPilot.get(), TOW_PILOT)
        assertEquals(model.gliderPilot.get(), GLIDER_PILOT)
        assertEquals(model.payer.get(), PAYER)
        assertEquals(model.glider.get(), GLIDER)
    }

    @Test fun shouldDisplayNotSetText() {
        `when`(towAttributes.towPilot).thenReturn("")
        `when`(towAttributes.gliderPilot).thenReturn("")
        `when`(towAttributes.payer).thenReturn("")
        `when`(towAttributes.glider).thenReturn("")
        model.updateFromTowAttributes()
        assertEquals(model.towPilot.get(), NOT_SET)
        assertEquals(model.gliderPilot.get(), NOT_SET)
        assertEquals(model.payer.get(), NOT_SET)
        assertEquals(model.glider.get(), NOT_SET)
    }
}
