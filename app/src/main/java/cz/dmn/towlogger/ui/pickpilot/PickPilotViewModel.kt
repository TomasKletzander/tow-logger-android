package cz.dmn.towlogger.ui.pickpilot

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import cz.dmn.towlogger.core.io.BaseLoaderSubscriber
import cz.dmn.towlogger.core.io.api.PilotsLoader
import cz.dmn.towlogger.core.io.api.models.Pilot
import javax.inject.Inject

class PickPilotViewModel @Inject constructor(private val loader: PilotsLoader) : ViewModel() {

    val pilots = MutableLiveData<List<Pilot>>()
    val error = MutableLiveData<Boolean>()
    val loading = ObservableBoolean()
    private val subscriber = PilotsLoaderSubscriber()
    var showTow = true
    var showGld = true

    init {
        loadPilots()
    }

    override fun onCleared() {
        super.onCleared()
        subscriber.dispose()
    }

    private fun loadPilots() {
        loading.set(true)
        error.value = false
        loader.execute(subscriber)
    }

    inner class PilotsLoaderSubscriber : BaseLoaderSubscriber<List<Pilot>>() {

        override fun onNext(pilots: List<Pilot>) {
            loading.set(false)
            error.value = false
            this@PickPilotViewModel.pilots.value = pilots
                    .sortedWith(Comparator { pilot1, pilot2 ->
                        pilot1.name.compareTo(pilot2.name, true)
                    })
                    .filter {
                        (it.qualifications.gld && showGld) || (it.qualifications.tow && showTow)
                    }
        }

        override fun onError(e: Throwable) {
            loading.set(false)
            error.value = true
            pilots.value = emptyList()
        }
    }
}
