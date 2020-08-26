package com.rideo.coremodule.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.imtamila.sparkoutmachinetask.dataBase.Converters
import com.imtamila.sparkoutmachinetask.dataBase.EmployeeDao
import com.imtamila.sparkoutmachinetask.employees.data.ContactData
import com.imtamila.sparkoutmachinetask.employees.data.EmployeeResponseData

/**
 * The Room database for this app
 */

private const val DATABASE_NAME = "sparkout_machine_task_db"

@Database(
    entities = [EmployeeResponseData::class, ContactData::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun employeeListDao(): EmployeeDao

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .addCallback(object : RoomDatabase.Callback() {
                })
                .build()
        }
    }
}