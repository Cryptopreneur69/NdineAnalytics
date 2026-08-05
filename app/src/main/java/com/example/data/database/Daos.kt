package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface OtcStockDao {
    @Query("SELECT * FROM otc_stocks ORDER BY ticker ASC")
    fun getAllStocksFlow(): Flow<List<OtcStockEntity>>

    @Query("SELECT * FROM otc_stocks WHERE ticker = :ticker LIMIT 1")
    suspend fun getStockByTicker(ticker: String): OtcStockEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStock(stock: OtcStockEntity)

    @Update
    suspend fun updateStock(stock: OtcStockEntity)

    @Query("DELETE FROM otc_stocks WHERE ticker = :ticker")
    suspend fun deleteStock(ticker: String)
}

@Dao
interface OtcWeeklyDataDao {
    @Query("SELECT * FROM otc_weekly_data WHERE ticker = :ticker ORDER BY weekEndDate DESC")
    fun getWeeklyDataForStockFlow(ticker: String): Flow<List<OtcWeeklyDataEntity>>

    @Query("SELECT * FROM otc_weekly_data WHERE ticker = :ticker ORDER BY weekEndDate DESC LIMIT 1")
    suspend fun getLatestWeeklyData(ticker: String): OtcWeeklyDataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeeklyData(data: OtcWeeklyDataEntity)

    @Query("DELETE FROM otc_weekly_data WHERE ticker = :ticker")
    suspend fun deleteWeeklyDataForStock(ticker: String)
}

@Dao
interface TradingSignalDao {
    @Query("SELECT * FROM trading_signals ORDER BY timestamp DESC")
    fun getAllSignalsFlow(): Flow<List<TradingSignalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSignal(signal: TradingSignalEntity): Long

    @Query("UPDATE trading_signals SET telegramSent = :sent WHERE id = :id")
    suspend fun updateTelegramSent(id: Int, sent: Boolean)

    @Query("DELETE FROM trading_signals")
    suspend fun deleteAllSignals()
}

@Dao
interface AppSettingDao {
    @Query("SELECT * FROM app_settings WHERE `key` = :key LIMIT 1")
    suspend fun getSettingByKey(key: String): AppSettingEntity?

    @Query("SELECT * FROM app_settings WHERE `key` = :key LIMIT 1")
    fun getSettingByKeyFlow(key: String): Flow<AppSettingEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSetting(setting: AppSettingEntity)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessagesFlow(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Query("DELETE FROM chat_messages")
    suspend fun deleteAllMessages()
}
