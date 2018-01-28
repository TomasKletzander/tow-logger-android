package cz.dmn.towlogger.data

import cz.dmn.towlogger.data.db.TowRecord

data class TowProgress(val state: TowState, val towDuration: Long, val towAltitude: Int, val record: TowRecord?) {

    companion object {
        fun createOff() = TowProgress(TowState.Off, 0, 0, null)
        fun createIdle() = TowProgress(TowState.Idle, 0, 0, null)
        fun createTowing(towDuration: Long, towAltitude: Int) = TowProgress(TowState.Towing, towDuration, towAltitude, null)
        fun createTowFinished(towRecord: TowRecord) = TowProgress(TowState.TowFinished, 0, 0, towRecord)
    }
}
