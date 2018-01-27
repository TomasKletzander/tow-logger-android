package cz.dmn.towlogger.core

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Settings @Inject constructor() {
    var takeoffSpeed = 80f
    var landingSpeed = 50f
}
