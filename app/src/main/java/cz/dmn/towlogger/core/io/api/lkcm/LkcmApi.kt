package cz.dmn.towlogger.core.io.api.lkcm

import cz.dmn.towlogger.core.io.api.models.Pilot
import io.reactivex.Observable
import retrofit2.http.GET

interface LkcmApi {

    @GET("pilots.json") fun getPilots(): Observable<List<Pilot>>
}
