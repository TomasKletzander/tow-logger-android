package cz.dmn.towlogger.data.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "tows")
data class TowRecord(@PrimaryKey
                     @JvmField var id: Long,
                     @JvmField var towPilot: String,
                     @JvmField var gliderPilot: String,
                     @JvmField var payer: String,
                     @JvmField var date: Int,
                     @JvmField var time: Int,
                     @JvmField var duration: Long,
                     @JvmField var elevation: Double)
