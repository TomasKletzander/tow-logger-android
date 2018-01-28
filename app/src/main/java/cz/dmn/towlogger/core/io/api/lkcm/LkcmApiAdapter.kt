package cz.dmn.towlogger.core.io.api.lkcm

import cz.dmn.towlogger.core.io.api.ApiAdapter
import cz.dmn.towlogger.core.io.api.models.Pilot
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LkcmApiAdapter @Inject constructor(private val lkcmApi: LkcmApi) : ApiAdapter {

    override fun getPilots(): Observable<List<Pilot>> = lkcmApi.getPilots()
}
