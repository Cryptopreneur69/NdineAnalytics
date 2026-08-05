package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "otc_stocks")
data class OtcStockEntity(
    @PrimaryKey val ticker: String,
    val companyName: String,
    val lastAnalyzed: Long = 0L,
    val realTimePrice: Double = 0.0,
    val realTimeVolume: Long = 0,
    val technicalSma20: Double = 0.0,
    val technicalSma50: Double = 0.0,
    val technicalRsi: Double = 50.0,
    val sentimentScore: Double = 0.0, // -1.0 to 1.0
    val sentimentSummary: String = "",
    val institutionalPressure: String = "NEUTRAL", // "HIGH BUYING", "MILD BUYING", "NEUTRAL", "MILD SELLING", "HIGH SELLING"
    val assetClass: String = "Stocks", // "Stocks", "Commodities", "Metals", "Crypto", "Forex"
    val priceChangePercent: Double = 0.0 // Percentage change for top performs highlights
)

@Entity(tableName = "otc_weekly_data")
data class OtcWeeklyDataEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ticker: String,
    val atsVolume: Long,
    val atsTrades: Long,
    val nonAtsVolume: Long,
    val nonAtsTrades: Long,
    val totalMarketVolume: Long,
    val highestVolumeAts: String,
    val highestVolumeAtsShares: Long,
    val weekEndDate: String
)

@Entity(tableName = "trading_signals")
data class TradingSignalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ticker: String,
    val signalType: String, // "BUY", "SELL", "HOLD"
    val strength: Int, // 0 to 100
    val scalphRationale: String,
    val timestamp: Long = System.currentTimeMillis(),
    val telegramSent: Boolean = false
)

@Entity(tableName = "app_settings")
data class AppSettingEntity(
    @PrimaryKey val key: String,
    val value: String
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sender: String, // "user" or "assistant"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)
