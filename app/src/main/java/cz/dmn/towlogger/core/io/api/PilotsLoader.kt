package cz.dmn.towlogger.core.io.api

import cz.dmn.towlogger.core.io.BaseLoader
import cz.dmn.towlogger.core.io.api.models.Pilot
import io.reactivex.Observable
import javax.inject.Inject

class PilotsLoader @Inject constructor(private val apiManager: ApiManager) : BaseLoader<List<Pilot>>() {

    override fun buildObservable(): Observable<List<Pilot>> = apiManager.getPilots()
}
