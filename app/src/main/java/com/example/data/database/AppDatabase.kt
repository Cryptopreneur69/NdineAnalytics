package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        OtcStockEntity::class,
        OtcWeeklyDataEntity::class,
        TradingSignalEntity::class,
        AppSettingEntity::class,
        ChatMessageEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun otcStockDao(): OtcStockDao
    abstract fun otcWeeklyDataDao(): OtcWeeklyDataDao
    abstract fun tradingSignalDao(): TradingSignalDao
    abstract fun appSettingDao(): AppSettingDao
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "otc_alpha_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
