package cz.dmn.towlogger.data.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "tows")
data class TowRecord(@PrimaryKey
                     @JvmField var id: Long,
                     @JvmField var pilotName: String,
                     @JvmField var payerName: String,
                     @JvmField var flightDate: Int,
                     @JvmField var takeoffTime: Int,
                     @JvmField var duration: Long,
                     @JvmField var elevation: Double)
