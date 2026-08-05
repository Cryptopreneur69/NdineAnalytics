package com.example.data.repository

import com.example.data.database.AppDatabase
import com.example.data.database.AppSettingEntity
import com.example.data.database.OtcStockEntity
import com.example.data.database.OtcWeeklyDataEntity
import com.example.data.database.TradingSignalEntity
import com.example.data.database.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

class OtcRepository(private val database: AppDatabase) {
    private val otcStockDao = database.otcStockDao()
    private val otcWeeklyDataDao = database.otcWeeklyDataDao()
    private val tradingSignalDao = database.tradingSignalDao()
    private val appSettingDao = database.appSettingDao()

    val allStocksFlow: Flow<List<OtcStockEntity>> = otcStockDao.getAllStocksFlow()
    val allSignalsFlow: Flow<List<TradingSignalEntity>> = tradingSignalDao.getAllSignalsFlow()

    suspend fun getStockByTicker(ticker: String): OtcStockEntity? {
        return otcStockDao.getStockByTicker(ticker)
    }

    suspend fun insertStock(stock: OtcStockEntity) {
        otcStockDao.insertStock(stock)
    }

    suspend fun updateStock(stock: OtcStockEntity) {
        otcStockDao.updateStock(stock)
    }

    suspend fun deleteStock(ticker: String) {
        otcStockDao.deleteStock(ticker)
        otcWeeklyDataDao.deleteWeeklyDataForStock(ticker)
    }

    fun getWeeklyDataForStock(ticker: String): Flow<List<OtcWeeklyDataEntity>> {
        return otcWeeklyDataDao.getWeeklyDataForStockFlow(ticker)
    }

    suspend fun getLatestWeeklyData(ticker: String): OtcWeeklyDataEntity? {
        return otcWeeklyDataDao.getLatestWeeklyData(ticker)
    }

    suspend fun insertWeeklyData(data: OtcWeeklyDataEntity) {
        otcWeeklyDataDao.insertWeeklyData(data)
    }

    suspend fun insertSignal(signal: TradingSignalEntity): Long {
        return tradingSignalDao.insertSignal(signal)
    }

    suspend fun updateTelegramSent(id: Int, sent: Boolean) {
        tradingSignalDao.updateTelegramSent(id, sent)
    }

    suspend fun deleteAllSignals() {
        tradingSignalDao.deleteAllSignals()
    }

    suspend fun getSettingByKey(key: String): String? {
        return appSettingDao.getSettingByKey(key)?.value
    }

    fun getSettingFlow(key: String): Flow<AppSettingEntity?> {
        return appSettingDao.getSettingByKeyFlow(key)
    }

    private val chatMessageDao = database.chatMessageDao()

    val allChatMessagesFlow: Flow<List<ChatMessageEntity>> = chatMessageDao.getAllMessagesFlow()

    suspend fun insertChatMessage(message: ChatMessageEntity): Long {
        return chatMessageDao.insertMessage(message)
    }

    suspend fun deleteAllChatMessages() {
        chatMessageDao.deleteAllMessages()
    }

    suspend fun saveSetting(key: String, value: String) {
        appSettingDao.saveSetting(AppSettingEntity(key, value))
    }
}
