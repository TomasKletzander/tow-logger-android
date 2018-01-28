package cz.dmn.towlogger.ui

import android.app.Activity
import android.content.Intent
import cz.dmn.towlogger.ui.pickpilot.PickPilotActivity
import javax.inject.Inject

class Navigator @Inject constructor(private val activity: Activity) {

    companion object {
        val REQUEST_ID_PICK_TOW_PILOT = 1
        val REQUEST_ID_PICK_GLIDER_PILOT = 2
        val REQUEST_ID_PICK_PAYER = 3
    }

    fun pickTowPilot(name: String) = activity.startActivityForResult(Intent(activity, PickPilotActivity::class.java)
            .putExtra(PickPilotActivity.EXTRA_PILOT_TYPE, PickPilotActivity.PILOT_TYPE_TOW)
            .putExtra(PickPilotActivity.EXTRA_PILOT_NAME, name), REQUEST_ID_PICK_TOW_PILOT)

    fun pickGliderPilot(name: String) = activity.startActivityForResult(Intent(activity, PickPilotActivity::class.java)
            .putExtra(PickPilotActivity.EXTRA_PILOT_TYPE, PickPilotActivity.PILOT_TYPE_GLD)
            .putExtra(PickPilotActivity.EXTRA_PILOT_NAME, name), REQUEST_ID_PICK_GLIDER_PILOT)

    fun pickPayer(name: String) = activity.startActivityForResult(Intent(activity, PickPilotActivity::class.java)
            .putExtra(PickPilotActivity.EXTRA_PILOT_NAME, name), REQUEST_ID_PICK_PAYER)
}
