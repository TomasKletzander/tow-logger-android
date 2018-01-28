package cz.dmn.towlogger.core.io.api

import cz.dmn.towlogger.core.io.api.models.Pilot
import io.reactivex.Observable

interface ApiAdapter {

    fun getPilots(): Observable<List<Pilot>>
}
