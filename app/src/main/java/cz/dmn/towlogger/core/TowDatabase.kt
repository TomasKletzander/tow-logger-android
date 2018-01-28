package cz.dmn.towlogger.core

import android.app.Application
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import cz.dmn.towlogger.data.db.TowRecord
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TowDatabase @Inject constructor(application: Application) {

    val database: TowDatabase.Database

    init {
        database = Room.databaseBuilder(application, Database::class.java, "tow-database").build()
    }

    @android.arch.persistence.room.Database(version = 1, entities = arrayOf(TowRecord::class))
    abstract class Database : RoomDatabase() {
        abstract fun dbInterface(): DbInterface
    }

    @Dao
    interface DbInterface {

        @Insert(onConflict = OnConflictStrategy.FAIL)
        fun insertTowRecords(vararg records: TowRecord)

        @Query("select * from tows where date = :date order by time")
        fun loadTowRecordsForDate(date: Long): Array<TowRecord>
    }

    fun addRecord(towRecord: TowRecord) = database.dbInterface().insertTowRecords(towRecord)

    fun getRecordsForDate(flightDate: Long) = database.dbInterface().loadTowRecordsForDate(flightDate)
}
