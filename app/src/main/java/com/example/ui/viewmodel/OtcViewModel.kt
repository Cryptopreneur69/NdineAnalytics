package com.example.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiClient
import com.example.data.api.TelegramClient
import com.example.data.database.AppDatabase
import com.example.data.database.OtcStockEntity
import com.example.data.database.OtcWeeklyDataEntity
import com.example.data.database.TradingSignalEntity
import com.example.data.database.ChatMessageEntity
import com.example.data.repository.OtcRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class OtcViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "OtcViewModel"
    private val repository = OtcRepository(AppDatabase.getDatabase(application))

    val allStocks: StateFlow<List<OtcStockEntity>> = repository.allStocksFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSignals: StateFlow<List<TradingSignalEntity>> = repository.allSignalsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Settings State
    val telegramBotToken = MutableStateFlow("")
    val telegramChatId = MutableStateFlow("")
    val autoSendTelegram = MutableStateFlow(false)

    // Mode Selector State: Simple Newcomer vs Advanced Pro
    val isAdvancedMode = MutableStateFlow(false)

    // Portfolio state: maps ticker -> Pair(shares, averageEntryPrice)
    val portfolioHoldings = MutableStateFlow<Map<String, Pair<Double, Double>>>(emptyMap())

    fun updateHolding(ticker: String, shares: Double, entryPrice: Double) {
        val current = portfolioHoldings.value.toMutableMap()
        if (shares <= 0.0) {
            current.remove(ticker)
        } else {
            current[ticker] = Pair(shares, entryPrice)
        }
        portfolioHoldings.value = current
    }

    // UI Interactive States
    private val _selectedStock = MutableStateFlow<OtcStockEntity?>(null)
    val selectedStock = _selectedStock.asStateFlow()

    // AI Assistant States
    val chatMessages: StateFlow<List<ChatMessageEntity>> = repository.allChatMessagesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isAssistantResponding = MutableStateFlow(false)
    val isAssistantResponding = _isAssistantResponding.asStateFlow()

    private val _selectedWeeklyData = MutableStateFlow<List<OtcWeeklyDataEntity>>(emptyList())
    val selectedWeeklyData = _selectedWeeklyData.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing = _isAnalyzing.asStateFlow()

    private val _analysisStatus = MutableStateFlow("")
    val analysisStatus = _analysisStatus.asStateFlow()

    private val _telegramStatus = MutableStateFlow<String?>(null)
    val telegramStatus = _telegramStatus.asStateFlow()

    init {
        // Load settings
        viewModelScope.launch {
            telegramBotToken.value = repository.getSettingByKey("telegram_bot_token") ?: ""
            telegramChatId.value = repository.getSettingByKey("telegram_chat_id") ?: ""
            autoSendTelegram.value = (repository.getSettingByKey("auto_send_telegram") ?: "false").toBoolean()
            
            // Seed default stocks if empty
            val currentStocks = repository.allStocksFlow.first()
            if (currentStocks.isEmpty()) {
                seedDefaultData()
            }
        }
    }

    fun selectStock(stock: OtcStockEntity) {
        _selectedStock.value = stock
        viewModelScope.launch {
            repository.getWeeklyDataForStock(stock.ticker).collect { list ->
                _selectedWeeklyData.value = list
            }
        }
    }

    fun saveTelegramSettings(token: String, chatId: String, autoSend: Boolean) {
        viewModelScope.launch {
            telegramBotToken.value = token
            telegramChatId.value = chatId
            autoSendTelegram.value = autoSend
            repository.saveSetting("telegram_bot_token", token)
            repository.saveSetting("telegram_chat_id", chatId)
            repository.saveSetting("auto_send_telegram", autoSend.toString())
        }
    }

    fun addStock(ticker: String, companyName: String, assetClass: String = "Stocks", priceChange: Double? = null) {
        viewModelScope.launch {
            val formattedTicker = ticker.uppercase().trim()
            if (formattedTicker.isBlank()) return@launch

            // Generate realistic randomized market data
            val price = Random.nextDouble(10.0, 350.0)
            val baseVol = Random.nextLong(1_000_000, 50_000_000)
            val rsi = Random.nextDouble(30.0, 75.0)
            val calculatedChange = priceChange ?: (Math.round(Random.nextDouble(-5.0, 5.0) * 100.0) / 100.0)
            
            val stock = OtcStockEntity(
                ticker = formattedTicker,
                companyName = companyName.ifBlank { "$formattedTicker Corp" },
                realTimePrice = Math.round(price * 100.0) / 100.0,
                realTimeVolume = baseVol,
                technicalSma20 = Math.round(price * (1.0 + Random.nextDouble(-0.03, 0.03)) * 100.0) / 100.0,
                technicalSma50 = Math.round(price * (1.0 + Random.nextDouble(-0.06, 0.06)) * 100.0) / 100.0,
                technicalRsi = Math.round(rsi * 10.0) / 10.0,
                sentimentScore = Math.round(Random.nextDouble(-0.8, 0.8) * 100.0) / 100.0,
                sentimentSummary = "Mixed off-exchange flow with standard momentum trading patterns.",
                institutionalPressure = "NEUTRAL",
                assetClass = assetClass,
                priceChangePercent = calculatedChange
            )

            repository.insertStock(stock)

            // Add corresponding randomized FINRA OTC Weekly Data
            val atsVol = (baseVol * Random.nextDouble(0.12, 0.22)).toLong()
            val nonAtsVol = (baseVol * Random.nextDouble(0.18, 0.32)).toLong()
            val totalMarketVol = (baseVol * 5) // Weekly total approx 5x daily
            val venues = listOf("UBS ATS", "Barclays LX", "Sigma X2", "Instinet CBX", "Level ATS", "Crossfinder")
            val highestVenue = venues.random()
            val highestVenueShares = (atsVol * Random.nextDouble(0.3, 0.55)).toLong()

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val dateStr = sdf.format(Date(System.currentTimeMillis() - 86400000L * 4)) // 4 days ago

            val weeklyData = OtcWeeklyDataEntity(
                ticker = formattedTicker,
                atsVolume = atsVol,
                atsTrades = (atsVol / Random.nextInt(100, 300)).toLong(),
                nonAtsVolume = nonAtsVol,
                nonAtsTrades = (nonAtsVol / Random.nextInt(200, 500)).toLong(),
                totalMarketVolume = totalMarketVol,
                highestVolumeAts = highestVenue,
                highestVolumeAtsShares = highestVenueShares,
                weekEndDate = dateStr
            )

            repository.insertWeeklyData(weeklyData)

            if (_selectedStock.value == null) {
                selectStock(stock)
            }
        }
    }

    fun deleteStock(ticker: String) {
        viewModelScope.launch {
            repository.deleteStock(ticker)
            if (_selectedStock.value?.ticker == ticker) {
                _selectedStock.value = null
                _selectedWeeklyData.value = emptyList()
            }
        }
    }

    fun runAgentAnalysis(stock: OtcStockEntity) {
        viewModelScope.launch {
            _isAnalyzing.value = true
            _analysisStatus.value = "Ingesting FINRA OTC weekly stats..."
            
            val weeklyData = repository.getLatestWeeklyData(stock.ticker)
            if (weeklyData == null) {
                _isAnalyzing.value = false
                _analysisStatus.value = "Error: No FINRA OTC weekly data found for ${stock.ticker}."
                return@launch
            }

            _analysisStatus.value = "Analyzing real-time exchange feeds..."
            
            // Format data into an expert quant prompt
            val atsRatio = (weeklyData.atsVolume.toDouble() / weeklyData.totalMarketVolume.toDouble()) * 100
            val nonAtsRatio = (weeklyData.nonAtsVolume.toDouble() / weeklyData.totalMarketVolume.toDouble()) * 100
            val darkPoolPercentage = atsRatio + nonAtsRatio

            val prompt = """
                You are an elite quantitative OTC Trading Agent. Analyze this market structure and OTC transparency data to identify hidden institutional buying or selling pressure specifically for a high-speed scalping strategy.
                
                Ticker: ${stock.ticker} (${stock.companyName})
                
                1. REAL-TIME EXCHANGE FEED DATA:
                - Last Price: $${stock.realTimePrice}
                - Daily Volume: ${String.format("%,d", stock.realTimeVolume)} shares
                
                2. TECHNICAL INDICATORS (1-Minute / 5-Minute Scalping Interval):
                - SMA 20: $${stock.technicalSma20}
                - SMA 50: $${stock.technicalSma50}
                - RSI (14): ${stock.technicalRsi}
                
                3. FINRA OTC TRANSPARENCY WEEKLY DATA:
                - ATS (Alternative Trading System) Volume: ${String.format("%,d", weeklyData.atsVolume)} shares
                - ATS Trades Count: ${String.format("%,d", weeklyData.atsTrades)} trades
                - Non-ATS Off-Exchange Volume: ${String.format("%,d", weeklyData.nonAtsVolume)} shares
                - Non-ATS Trades Count: ${String.format("%,d", weeklyData.nonAtsTrades)} trades
                - Total Market Volume (Public + Off-Exchange): ${String.format("%,d", weeklyData.totalMarketVolume)} shares
                - Calculated ATS ratio: ${String.format("%.2f", atsRatio)}%
                - Calculated Non-ATS ratio: ${String.format("%.2f", nonAtsRatio)}%
                - Total Off-Exchange / Dark Pool Ratio: ${String.format("%.2f", darkPoolPercentage)}%
                - Highest Volume ATS Venue for this stock: ${weeklyData.highestVolumeAts}
                - Highest Volume ATS Share Volume: ${String.format("%,d", weeklyData.highestVolumeAtsShares)} shares (${String.format("%.2f", (weeklyData.highestVolumeAtsShares.toDouble() / weeklyData.atsVolume.toDouble()) * 100)}% of total ATS volume)
                
                4. REAL-TIME NEWS & SOCIAL REACTION DATA:
                - Current Sentiment Score: ${stock.sentimentScore}
                - Current News Summary: ${stock.sentimentSummary}
                
                Determine:
                1. If there is hidden institutional buying/selling pressure (indicated by disproportionately high ATS/non-ATS block volume occurring in dark pools relative to the standard public exchange trend, especially when price consolidates).
                2. Formulate a high-probability scalping trading signal (BUY, SELL, or HOLD) with exact strength (0 to 100%) and a concise institutional rationale suitable for active day traders.
                
                YOUR OUTPUT MUST COMPLY WITH THE FOLLOWING FORMAT EXACTLY so my parser can process it. Do not include extra text outside this format:
                [INSTITUTIONAL_PRESSURE] <One of: HIGH BUYING, MILD BUYING, NEUTRAL, MILD SELLING, HIGH SELLING>
                [SENTIMENT_SCORE] <A float between -1.0 and 1.0>
                [SIGNAL_TYPE] <One of: BUY, SELL, HOLD>
                [STRENGTH] <An integer between 0 and 100>
                [RATIONALE]
                <A detailed markdown rationale with sections:
                * **Dark Pool Accumulation vs Distribution**: Compare ATS/Non-ATS to Total Market Volume.
                * **Highest ATS Venue Dominance**: Explain ${weeklyData.highestVolumeAts}'s impact of ${String.format("%,d", weeklyData.highestVolumeAtsShares)} shares.
                * **Scalping Trade Execution Strategy**: Combine RSI (${stock.technicalRsi}), SMAs, price, and sentiment for exact 1-to-5 minute scalping triggers.
                * **Summary & Risk warning**.>
            """.trimIndent()

            _analysisStatus.value = "Consulting Gemini AI model..."
            val result = GeminiClient.generateAnalysis(prompt)
            
            if (result.startsWith("Error:")) {
                _isAnalyzing.value = false
                _analysisStatus.value = result
                return@launch
            }

            _analysisStatus.value = "Parsing AI trading intelligence..."
            parseAndUpdateSignal(stock, result, weeklyData)
        }
    }

    private suspend fun parseAndUpdateSignal(stock: OtcStockEntity, aiResponse: String, weeklyData: OtcWeeklyDataEntity) {
        try {
            var instPressure = "NEUTRAL"
            var sentScore = stock.sentimentScore
            var signalType = "HOLD"
            var strength = 50
            var rationale = aiResponse

            // Parsing the AI Response structured format
            val lines = aiResponse.split("\n")
            for (line in lines) {
                when {
                    line.startsWith("[INSTITUTIONAL_PRESSURE]") -> {
                        instPressure = line.replace("[INSTITUTIONAL_PRESSURE]", "").trim()
                    }
                    line.startsWith("[SENTIMENT_SCORE]") -> {
                        sentScore = line.replace("[SENTIMENT_SCORE]", "").trim().toDoubleOrNull() ?: sentScore
                    }
                    line.startsWith("[SIGNAL_TYPE]") -> {
                        signalType = line.replace("[SIGNAL_TYPE]", "").trim()
                    }
                    line.startsWith("[STRENGTH]") -> {
                        strength = line.replace("[STRENGTH]", "").trim().toIntOrNull() ?: strength
                    }
                }
            }

            // Cleanup tags in rationale if any, or just use the text from [RATIONALE] onwards
            val rationaleIndex = aiResponse.indexOf("[RATIONALE]")
            if (rationaleIndex != -1) {
                rationale = aiResponse.substring(rationaleIndex + "[RATIONALE]".length).trim()
            }

            // Update Stock in DB
            val updatedStock = stock.copy(
                lastAnalyzed = System.currentTimeMillis(),
                sentimentScore = sentScore,
                institutionalPressure = instPressure
            )
            repository.updateStock(updatedStock)
            _selectedStock.value = updatedStock

            // Insert Signal to DB
            val signal = TradingSignalEntity(
                ticker = stock.ticker,
                signalType = signalType,
                strength = strength,
                scalphRationale = rationale,
                telegramSent = false
            )
            val signalId = repository.insertSignal(signal)

            _isAnalyzing.value = false
            _analysisStatus.value = "Analysis complete!"

            // Check auto-telegram send
            if (autoSendTelegram.value) {
                _analysisStatus.value = "Transmitting signal to Telegram..."
                sendTelegramSignal(signal.copy(id = signalId.toInt()), updatedStock, weeklyData)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing AI response", e)
            _isAnalyzing.value = false
            _analysisStatus.value = "Error parsing AI output: ${e.localizedMessage}"
        }
    }

    fun triggerTelegramSend(signal: TradingSignalEntity) {
        viewModelScope.launch {
            _telegramStatus.value = "Transmitting alert to Telegram..."
            val stock = repository.getStockByTicker(signal.ticker)
            val weeklyData = repository.getLatestWeeklyData(signal.ticker)
            if (stock == null || weeklyData == null) {
                _telegramStatus.value = "Failed: Missing Stock or OTC Weekly data."
                return@launch
            }
            sendTelegramSignal(signal, stock, weeklyData)
        }
    }

    private suspend fun sendTelegramSignal(signal: TradingSignalEntity, stock: OtcStockEntity, weeklyData: OtcWeeklyDataEntity) {
        val token = telegramBotToken.value
        val chatId = telegramChatId.value

        if (token.isBlank() || chatId.isBlank()) {
            _telegramStatus.value = "Failed: Bot Token or Chat ID is not configured in Settings."
            return
        }

        val atsRatio = (weeklyData.atsVolume.toDouble() / weeklyData.totalMarketVolume.toDouble()) * 100
        val nonAtsRatio = (weeklyData.nonAtsVolume.toDouble() / weeklyData.totalMarketVolume.toDouble()) * 100

        val emoji = when (signal.signalType) {
            "BUY" -> "🟢 BUY"
            "SELL" -> "🔴 SELL"
            else -> "🟡 HOLD"
        }

        val formattedMessage = """
            🚨 *NDINE ANALYTICS AGENT TRADING SIGNAL* 🚨
            
            *Stock:* ${stock.ticker} - ${stock.companyName}
            *Signal:* $emoji (${signal.strength}% Confidence)
            *Real-Time Price:* $${stock.realTimePrice}
            *Daily Vol:* ${String.format("%,d", stock.realTimeVolume)} shares
            
            📊 *Market Structure Analysis:*
            • Off-Exchange Volume Ratio: ${String.format("%.1f", atsRatio + nonAtsRatio)}%
            • Volume Volume Trend: ${String.format("%,d", weeklyData.totalMarketVolume)}
            
            🧠 *Institutional Flow & Momentum:*
            • Pressure: *${stock.institutionalPressure}*
            • Tech RSI(14): ${stock.technicalRsi}
            • Sentiment Score: ${stock.sentimentScore}
            
            ⚡ *Quant Scalper Rationale:*
            ${signal.scalphRationale.take(1200)}...
            
            _Powered by Ndine Analytics Agent AI_
        """.trimIndent()

        val success = TelegramClient.sendMessage(token, chatId, formattedMessage)
        if (success) {
            repository.updateTelegramSent(signal.id, true)
            _telegramStatus.value = "Sent successfully to Telegram!"
        } else {
            _telegramStatus.value = "Failed: Check Bot Token / Chat ID, or bot setup."
        }
    }

    fun clearTelegramStatus() {
        _telegramStatus.value = null
    }

    fun sendChatMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch {
            repository.insertChatMessage(ChatMessageEntity(sender = "user", content = trimmed))
            _isAssistantResponding.value = true

            val systemInstructions = """
                You are the elite professional market analyst AI Assistant embedded within the "Ndine Analytics Agent" platform.
                Your purpose is to provide real-time, public-domain analytics for the most prominent and thriving stocks, commodities, metals, cryptocurrencies, and forex by sourcing information from the live web.
                
                You must comply with the following instructions:
                1. DELIVER DIRECT AND CONCISE ANSWERS. No unnecessary filler, jargon-heavy waffle, or redundant preambles. Get straight to the analysis.
                2. ALWAYS include a clear visual disclaimer stating that:
                   - All insights are for informational purposes only.
                   - They do not constitute personalized financial advice.
                   - All information is sourced from public domains.
                3. Be accurate and base your reasoning on current public-domain market indicators.
            """.trimIndent()

            val prompt = """
                $systemInstructions
                
                User query: $trimmed
            """.trimIndent()

            val response = GeminiClient.generateAnalysis(prompt)
            repository.insertChatMessage(ChatMessageEntity(sender = "assistant", content = response))
            _isAssistantResponding.value = false
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            repository.deleteAllChatMessages()
        }
    }

    fun sendAssistantMessageToTelegram(messageText: String) {
        viewModelScope.launch {
            _telegramStatus.value = "Transmitting AI Insight to Telegram..."
            val token = telegramBotToken.value
            val chatId = telegramChatId.value

            if (token.isBlank() || chatId.isBlank()) {
                _telegramStatus.value = "Failed: Bot Token or Chat ID is not configured in Settings."
                return@launch
            }

            val formattedMessage = """
                🤖 *NDINE ANALYTICS AGENT - AI INSIGHT REPORT* 🤖
                
                ${messageText}
                
                ⚠️ *DISCLAIMER:* All insights are for informational purposes only, do not constitute personalized financial advice, and are sourced from public domains.
                
                _Powered by Ndine Analytics Agent AI_
            """.trimIndent()

            val success = TelegramClient.sendMessage(token, chatId, formattedMessage)
            if (success) {
                _telegramStatus.value = "Sent successfully to Telegram!"
            } else {
                _telegramStatus.value = "Failed: Check Bot Token / Chat ID, or bot setup."
            }
        }
    }

    fun clearAllSignals() {
        viewModelScope.launch {
            repository.deleteAllSignals()
        }
    }

    private suspend fun seedDefaultData() {
        // --- SEED STOCKS ---
        val tsla = OtcStockEntity(
            ticker = "TSLA",
            companyName = "Tesla, Inc.",
            realTimePrice = 248.50,
            realTimeVolume = 45_200_000L,
            technicalSma20 = 245.30,
            technicalSma50 = 242.10,
            technicalRsi = 62.4,
            sentimentScore = 0.45,
            sentimentSummary = "Strong off-exchange block accumulation noted near crucial psychological supports. News is positive regarding robotaxi beta updates.",
            institutionalPressure = "MILD BUYING",
            assetClass = "Stocks",
            priceChangePercent = 3.4
        )
        repository.insertStock(tsla)
        repository.insertWeeklyData(OtcWeeklyDataEntity(ticker = "TSLA", atsVolume = 9_850_000L, atsTrades = 45_120L, nonAtsVolume = 15_400_000L, nonAtsTrades = 62_890L, totalMarketVolume = 112_000_000L, highestVolumeAts = "UBS ATS", highestVolumeAtsShares = 3_120_000L, weekEndDate = "2026-07-04"))

        val nvda = OtcStockEntity(
            ticker = "NVDA",
            companyName = "NVIDIA Corporation",
            realTimePrice = 124.75,
            realTimeVolume = 120_500_000L,
            technicalSma20 = 128.20,
            technicalSma50 = 131.50,
            technicalRsi = 32.1,
            sentimentScore = -0.32,
            sentimentSummary = "Heavy dark pool distribution crossing at the bid. Retail panic on social media over delayed Blackwell chip shipments, but institutional block trade remains elevated.",
            institutionalPressure = "MILD SELLING",
            assetClass = "Stocks",
            priceChangePercent = -2.1
        )
        repository.insertStock(nvda)
        repository.insertWeeklyData(OtcWeeklyDataEntity(ticker = "NVDA", atsVolume = 28_450_000L, atsTrades = 112_040L, nonAtsVolume = 42_110_000L, nonAtsTrades = 180_450L, totalMarketVolume = 310_000_000L, highestVolumeAts = "Barclays LX", highestVolumeAtsShares = 9_410_000L, weekEndDate = "2026-07-04"))

        val pltr = OtcStockEntity(
            ticker = "PLTR",
            companyName = "Palantir Technologies Inc.",
            realTimePrice = 28.15,
            realTimeVolume = 18_400_000L,
            technicalSma20 = 26.80,
            technicalSma50 = 25.10,
            technicalRsi = 73.8,
            sentimentScore = 0.78,
            sentimentSummary = "Exceptional dark pool institutional prints observed with negligible retail involvement. Sentiment is extremely bullish following multiple government contracts announcement.",
            institutionalPressure = "HIGH BUYING",
            assetClass = "Stocks",
            priceChangePercent = 8.7
        )
        repository.insertStock(pltr)
        repository.insertWeeklyData(OtcWeeklyDataEntity(ticker = "PLTR", atsVolume = 4_110_000L, atsTrades = 18_220L, nonAtsVolume = 6_890_000L, nonAtsTrades = 31_040L, totalMarketVolume = 38_000_000L, highestVolumeAts = "Instinet CBX", highestVolumeAtsShares = 1_850_000L, weekEndDate = "2026-07-04"))

        val aapl = OtcStockEntity(
            ticker = "AAPL",
            companyName = "Apple Inc.",
            realTimePrice = 210.30,
            realTimeVolume = 54_800_000L,
            technicalSma20 = 208.10,
            technicalSma50 = 205.40,
            technicalRsi = 54.5,
            sentimentScore = 0.15,
            sentimentSummary = "Balanced institutional crossings via Barclays LX. High institutional support around $200 level.",
            institutionalPressure = "NEUTRAL",
            assetClass = "Stocks",
            priceChangePercent = 1.2
        )
        repository.insertStock(aapl)
        repository.insertWeeklyData(OtcWeeklyDataEntity(ticker = "AAPL", atsVolume = 12_400_000L, atsTrades = 58_000L, nonAtsVolume = 18_500_000L, nonAtsTrades = 84_000L, totalMarketVolume = 145_000_000L, highestVolumeAts = "Barclays LX", highestVolumeAtsShares = 4_100_000L, weekEndDate = "2026-07-04"))

        // --- SEED COMMODITIES ---
        val usoil = OtcStockEntity(
            ticker = "USOIL",
            companyName = "WTI Crude Oil (Spot)",
            realTimePrice = 74.50,
            realTimeVolume = 24_500_000L,
            technicalSma20 = 76.10,
            technicalSma50 = 77.80,
            technicalRsi = 38.4,
            sentimentScore = -0.45,
            sentimentSummary = "Elevated global supply pressures and inventory builds lead to mild distribution off-exchange.",
            institutionalPressure = "MILD SELLING",
            assetClass = "Commodities",
            priceChangePercent = -1.8
        )
        repository.insertStock(usoil)
        repository.insertWeeklyData(OtcWeeklyDataEntity(ticker = "USOIL", atsVolume = 5_200_000L, atsTrades = 24_000L, nonAtsVolume = 7_400_000L, nonAtsTrades = 36_000L, totalMarketVolume = 62_000_000L, highestVolumeAts = "UBS ATS", highestVolumeAtsShares = 1_850_000L, weekEndDate = "2026-07-04"))

        val natgas = OtcStockEntity(
            ticker = "NATGAS",
            companyName = "Natural Gas Spot",
            realTimePrice = 2.45,
            realTimeVolume = 14_200_000L,
            technicalSma20 = 2.65,
            technicalSma50 = 2.80,
            technicalRsi = 28.5,
            sentimentScore = -0.68,
            sentimentSummary = "Severe oversold condition reaching key psychological support. Bearish storage projections are fully priced.",
            institutionalPressure = "HIGH SELLING",
            assetClass = "Commodities",
            priceChangePercent = -4.5
        )
        repository.insertStock(natgas)
        repository.insertWeeklyData(OtcWeeklyDataEntity(ticker = "NATGAS", atsVolume = 3_100_000L, atsTrades = 15_000L, nonAtsVolume = 4_900_000L, nonAtsTrades = 22_000L, totalMarketVolume = 38_000_000L, highestVolumeAts = "Instinet CBX", highestVolumeAtsShares = 1_100_000L, weekEndDate = "2026-07-04"))

        // --- SEED METALS ---
        val gold = OtcStockEntity(
            ticker = "GOLD",
            companyName = "Gold Ounce Spot",
            realTimePrice = 2350.80,
            realTimeVolume = 8_900_000L,
            technicalSma20 = 2335.50,
            technicalSma50 = 2310.00,
            technicalRsi = 58.2,
            sentimentScore = 0.35,
            sentimentSummary = "Robust sovereign demand and defensive safe-haven asset allocation patterns continue off-exchange.",
            institutionalPressure = "MILD BUYING",
            assetClass = "Metals",
            priceChangePercent = 0.95
        )
        repository.insertStock(gold)
        repository.insertWeeklyData(OtcWeeklyDataEntity(ticker = "GOLD", atsVolume = 1_900_000L, atsTrades = 9_500L, nonAtsVolume = 2_800_000L, nonAtsTrades = 14_000L, totalMarketVolume = 22_000_000L, highestVolumeAts = "UBS ATS", highestVolumeAtsShares = 720_000L, weekEndDate = "2026-07-04"))

        val silver = OtcStockEntity(
            ticker = "SILVER",
            companyName = "Silver Ounce Spot",
            realTimePrice = 30.15,
            realTimeVolume = 12_400_000L,
            technicalSma20 = 29.50,
            technicalSma50 = 28.90,
            technicalRsi = 64.1,
            sentimentScore = 0.52,
            sentimentSummary = "Industrial demand projections coupled with gold safe-haven tailwinds. Block crossings indicate active accumulation.",
            institutionalPressure = "MILD BUYING",
            assetClass = "Metals",
            priceChangePercent = 2.1
        )
        repository.insertStock(silver)
        repository.insertWeeklyData(OtcWeeklyDataEntity(ticker = "SILVER", atsVolume = 2_800_000L, atsTrades = 12_000L, nonAtsVolume = 3_900_000L, nonAtsTrades = 18_000L, totalMarketVolume = 31_000_000L, highestVolumeAts = "Barclays LX", highestVolumeAtsShares = 1_050_000L, weekEndDate = "2026-07-04"))

        // --- SEED CRYPTO ---
        val btc = OtcStockEntity(
            ticker = "BTC/USD",
            companyName = "Bitcoin / US Dollar",
            realTimePrice = 57800.00,
            realTimeVolume = 38_500_000L,
            technicalSma20 = 55600.00,
            technicalSma50 = 58200.00,
            technicalRsi = 56.5,
            sentimentScore = 0.48,
            sentimentSummary = "ETF inflow trends combined with off-exchange OTC desk transfers indicating strong block absorption.",
            institutionalPressure = "MILD BUYING",
            assetClass = "Crypto",
            priceChangePercent = 5.8
        )
        repository.insertStock(btc)
        repository.insertWeeklyData(OtcWeeklyDataEntity(ticker = "BTC/USD", atsVolume = 8_200_000L, atsTrades = 42_000L, nonAtsVolume = 11_400_000L, nonAtsTrades = 68_000L, totalMarketVolume = 95_000_000L, highestVolumeAts = "Barclays LX", highestVolumeAtsShares = 3_100_000L, weekEndDate = "2026-07-04"))

        val eth = OtcStockEntity(
            ticker = "ETH/USD",
            companyName = "Ethereum / US Dollar",
            realTimePrice = 3120.50,
            realTimeVolume = 22_400_000L,
            technicalSma20 = 2980.00,
            technicalSma50 = 3240.00,
            technicalRsi = 54.2,
            sentimentScore = 0.38,
            sentimentSummary = "Decentralized staking ratio increases. Institutions active via institutional custody OTC crossings.",
            institutionalPressure = "MILD BUYING",
            assetClass = "Crypto",
            priceChangePercent = 4.2
        )
        repository.insertStock(eth)
        repository.insertWeeklyData(OtcWeeklyDataEntity(ticker = "ETH/USD", atsVolume = 4_800_000L, atsTrades = 24_000L, nonAtsVolume = 6_900_000L, nonAtsTrades = 38_000L, totalMarketVolume = 55_000_000L, highestVolumeAts = "UBS ATS", highestVolumeAtsShares = 1_950_000L, weekEndDate = "2026-07-04"))

        val sol = OtcStockEntity(
            ticker = "SOL/USD",
            companyName = "Solana / US Dollar",
            realTimePrice = 142.10,
            realTimeVolume = 18_200_000L,
            technicalSma20 = 130.50,
            technicalSma50 = 124.80,
            technicalRsi = 71.2,
            sentimentScore = 0.85,
            sentimentSummary = "Extremely high DeFi transaction speeds and retail speculation driving heavy decentralized network fees. High OTC institutional demand.",
            institutionalPressure = "HIGH BUYING",
            assetClass = "Crypto",
            priceChangePercent = 9.5
        )
        repository.insertStock(sol)
        repository.insertWeeklyData(OtcWeeklyDataEntity(ticker = "SOL/USD", atsVolume = 3_900_000L, atsTrades = 19_500L, nonAtsVolume = 5_400_000L, nonAtsTrades = 29_000L, totalMarketVolume = 42_000_000L, highestVolumeAts = "Instinet CBX", highestVolumeAtsShares = 1_450_000L, weekEndDate = "2026-07-04"))

        // --- SEED FOREX ---
        val eurusd = OtcStockEntity(
            ticker = "EUR/USD",
            companyName = "Euro / US Dollar",
            realTimePrice = 1.0825,
            realTimeVolume = 150_000_000L,
            technicalSma20 = 1.0850,
            technicalSma50 = 1.0890,
            technicalRsi = 41.2,
            sentimentScore = -0.18,
            sentimentSummary = "ECB policy divergence and US yield support consolidates pricing lower. Central bank desks showing balanced distributions.",
            institutionalPressure = "NEUTRAL",
            assetClass = "Forex",
            priceChangePercent = -0.15
        )
        repository.insertStock(eurusd)
        repository.insertWeeklyData(OtcWeeklyDataEntity(ticker = "EUR/USD", atsVolume = 35_000_000L, atsTrades = 120_000L, nonAtsVolume = 48_000_000L, nonAtsTrades = 190_000L, totalMarketVolume = 410_000_000L, highestVolumeAts = "Barclays LX", highestVolumeAtsShares = 12_500_000L, weekEndDate = "2026-07-04"))

        val gbpusd = OtcStockEntity(
            ticker = "GBP/USD",
            companyName = "Pound Sterling / US Dollar",
            realTimePrice = 1.2780,
            realTimeVolume = 95_000_000L,
            technicalSma20 = 1.2750,
            technicalSma50 = 1.2710,
            technicalRsi = 52.8,
            sentimentScore = 0.12,
            sentimentSummary = "Bank of England hawkish stance supports sterling resilience against broad-based dollar consolidation.",
            institutionalPressure = "NEUTRAL",
            assetClass = "Forex",
            priceChangePercent = 0.10
        )
        repository.insertStock(gbpusd)
        repository.insertWeeklyData(OtcWeeklyDataEntity(ticker = "GBP/USD", atsVolume = 22_000_000L, atsTrades = 85_000L, nonAtsVolume = 31_000_000L, nonAtsTrades = 110_000L, totalMarketVolume = 280_000_000L, highestVolumeAts = "UBS ATS", highestVolumeAtsShares = 7_900_000L, weekEndDate = "2026-07-04"))

        val usdjpy = OtcStockEntity(
            ticker = "USD/JPY",
            companyName = "US Dollar / Japanese Yen",
            realTimePrice = 161.40,
            realTimeVolume = 180_000_000L,
            technicalSma20 = 159.80,
            technicalSma50 = 157.50,
            technicalRsi = 68.7,
            sentimentScore = 0.42,
            sentimentSummary = "Widening yield differentials and intervention risks. Institutional accounts driving capital out of Yen into high-yielding Dollar carry trades.",
            institutionalPressure = "MILD BUYING",
            assetClass = "Forex",
            priceChangePercent = 0.35
        )
        repository.insertStock(usdjpy)
        repository.insertWeeklyData(OtcWeeklyDataEntity(ticker = "USD/JPY", atsVolume = 41_000_000L, atsTrades = 160_000L, nonAtsVolume = 54_000_000L, nonAtsTrades = 220_000L, totalMarketVolume = 480_000_000L, highestVolumeAts = "Instinet CBX", highestVolumeAtsShares = 14_800_000L, weekEndDate = "2026-07-04"))

        selectStock(tsla)
    }
}
