package cz.dmn.towlogger.core.io.api

import cz.dmn.towlogger.core.io.api.models.Pilot
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiManager @Inject constructor(private val apiAdapter: ApiAdapter) {

    fun getPilots(): Observable<List<Pilot>> = apiAdapter.getPilots()
}
