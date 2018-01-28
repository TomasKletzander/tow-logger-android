package cz.dmn.towlogger.core

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class TowAttributes @Inject constructor(@Named("TowAttributes") private val prefs: SharedPreferences) {

    companion object {
        val PREF_TOW_PILOT = "towPilot"
        val PREF_GLIDER_PILOT = "gliderPilot"
        val PREF_PAYER = "payer"
        val PREF_GLIDER = "glider"
    }

    private var _towPilot = prefs.getString(PREF_TOW_PILOT, "")
    private var _gliderPilot = prefs.getString(PREF_GLIDER_PILOT, "")
    private var _payer = prefs.getString(PREF_PAYER, "")
    private var _glider = prefs.getString(PREF_GLIDER, "")

    var towPilot: String
    get() = _towPilot
    set(value) {
        _towPilot = value
        prefs.edit().putString(PREF_TOW_PILOT, value).apply()
    }

    var gliderPilot: String
    get() = _gliderPilot
    set(value) {
        _gliderPilot = value
        prefs.edit().putString(PREF_GLIDER_PILOT, value).apply()
    }

    var payer: String
    get() = _payer
    set(value) {
        _payer = value
        prefs.edit().putString(PREF_PAYER, value).apply()
    }

    var glider: String
    get() = _glider
    set(value) {
        _glider = value
        prefs.edit().putString(PREF_GLIDER, value).apply()
    }
}
