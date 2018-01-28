package cz.dmn.towlogger.ui.main

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import cz.dmn.towlogger.R
import cz.dmn.towlogger.TowLoggerApplication
import cz.dmn.towlogger.core.TowAttributes
import cz.dmn.towlogger.core.TowDetector
import cz.dmn.towlogger.data.TowState
import cz.dmn.towlogger.util.ObservableBackground
import io.reactivex.disposables.Disposable
import java.text.SimpleDateFormat
import javax.inject.Inject

class MainActivityModel @Inject constructor(private val application: TowLoggerApplication,
                                            private val towAttributes: TowAttributes,
                                            towDetector: TowDetector) {

    companion object {
        val formatDuration = SimpleDateFormat("hh:mm")
    }

    val towInfoVisible = ObservableBoolean(false)
    val stateText = ObservableField(application.getString(R.string.stateOff))
    val towPilot = ObservableField("")
    val gliderPilot = ObservableField("")
    val payer = ObservableField("")
    val glider = ObservableField("")
    val duration = ObservableField("")
    val altitude = ObservableField("")
    val progressBackground = ObservableBackground()
    private val disposable: Disposable

    init {
        disposable = towDetector.progress.subscribe { progress ->
            when (progress.state) {
                TowState.Off -> {
                    towInfoVisible.set(false)
                    stateText.set(application.getString(R.string.stateOff))
                    progressBackground.colorResId = R.color.off
                }
                TowState.Idle, TowState.TowFinished -> {
                    towInfoVisible.set(false)
                    stateText.set(application.getString(R.string.stateIdle))
                    progressBackground.colorResId = R.color.idle
                }
                TowState.Towing -> {
                    towInfoVisible.set(true)
                    stateText.set(application.getString(R.string.stateTowing))
                    duration.set(formatDuration.format(progress.towDuration))
                    altitude.set(String.format("%d m", progress.towAltitude))
                    progressBackground.colorResId = R.color.active
                }
            }
        }
    }

    fun updateFromTowAttributes() {
        towPilot.set(valueOrNotSet(towAttributes.towPilot))
        gliderPilot.set(valueOrNotSet(towAttributes.gliderPilot))
        payer.set(valueOrNotSet(towAttributes.payer))
        glider.set(valueOrNotSet(towAttributes.glider))
    }

    fun dispose() {
        disposable.dispose()
    }

    private fun valueOrNotSet(value: String): String {
        if (value.isEmpty()) return application.getString(R.string.notSet)
        return value
    }
}
