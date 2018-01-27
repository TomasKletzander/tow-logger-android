package cz.dmn.towlogger.core

import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IgcLogger @Inject constructor() {

    companion object {
        val formatFilename = SimpleDateFormat("yyyyMMdd'.igc'")
    }

    private fun prepareFile(): File {
        val towsFolder = File(Environment.getExternalStorageDirectory(), "Tows")
        if (!towsFolder.exists()) {
            towsFolder.mkdirs()
        }
        val file = File(towsFolder, formatFilename.format(Date()))
        if (file.length() == 0L) {
            file.appendText("AXXX 123456 Tow logger 1.0\n")
            file.appendText("HFFXA035\n")
            file.appendText("HFPLTPILOTINCHARGE: Medlanky tow pilot\n")
            file.appendText("HFCM2CREW2: not recorded\n")
            file.appendText("HFGTYGLIDERTYPE:\n")
            file.appendText("HFGIDGLIDERID:\n")
            file.appendText("HFDTM100GPSDATUM: WGS-1984\n")
            file.appendText("HFRFWFIRMWAREVERSION: 1.0\n")
            file.appendText("HFRHWHARDWAREVERSION: 1.0\n")
            file.appendText("HFFTYFRTYPE: Tow Logger for Android by Tomas Kletzander\n")
            file.appendText("HFGPSGPS: Internal GPS module\n")
            file.appendText("HFPRSPRESSALTSENSOR: not recorded\n")
            file.appendText("HFCIDCOMPETITIONID:\n")
            file.appendText("HFCCLCOMPETITIONCLASS\n")
        }
        return file
    }

    fun logLocation(latitude: Double, longitude: Double, altitude: Double) {
        val file = prepareFile()
        val timestamp = Date()
        val lat1 = Math.floor(latitude).toInt()
        val lat2 = (Math.round((latitude - lat1.toDouble()) * 60000.0)).toInt()
        val lon1 = Math.floor(longitude).toInt()
        val lon2 = (Math.round((longitude - lon1.toDouble()) * 60000.0)).toInt()
        val alt = Math.round(altitude).toInt()
        file.appendText(String.format("B%02d%02d%02d%02d%05dN%03d%05dEA%05d%05d\n", timestamp.hours, timestamp.minutes, timestamp.seconds, lat1, lat2, lon1, lon2, alt, alt))
    }
}
