package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.ChatMessageEntity
import com.example.ui.theme.TerminalBlack
import com.example.ui.theme.TerminalCoral
import com.example.ui.theme.TerminalCyan
import com.example.ui.theme.TerminalEmerald
import com.example.ui.theme.TerminalGold
import com.example.ui.theme.TerminalLightGrey
import com.example.ui.theme.TerminalMediumGrey
import com.example.ui.viewmodel.OtcViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun NewsSentimentScreen(
    viewModel: OtcViewModel,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableIntStateOf(0) } // 0: AI Assistant, 1: Technical Bias

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TerminalBlack)
    ) {
        // --- TOP TAB SELECTOR SEGMENTS ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .background(TerminalMediumGrey, RoundedCornerShape(8.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (activeTab == 0) TerminalCyan else Color.Transparent)
                    .clickable { activeTab = 0 }
                    .padding(vertical = 10.dp)
                    .testTag("tab_button_ai_analyst"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "AI ANALYST",
                    fontWeight = FontWeight.Bold,
                    color = if (activeTab == 0) Color.Black else Color.White,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (activeTab == 1) TerminalCyan else Color.Transparent)
                    .clickable { activeTab = 1 }
                    .padding(vertical = 10.dp)
                    .testTag("tab_button_technical_bias"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "TECHNICAL BIAS",
                    fontWeight = FontWeight.Bold,
                    color = if (activeTab == 1) Color.Black else Color.White,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }

        if (activeTab == 0) {
            AiAssistantTab(viewModel = viewModel)
        } else {
            TechnicalBiasTab(viewModel = viewModel)
        }
    }
}

@Composable
fun AiAssistantTab(
    viewModel: OtcViewModel,
    modifier: Modifier = Modifier
) {
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isResponding by viewModel.isAssistantResponding.collectAsState()
    val telegramStatus by viewModel.telegramStatus.collectAsState()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto scroll to bottom when new messages arrive
    LaunchedEffect(chatMessages.size, isResponding) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        // --- DISCLAIMER CARD NOTICE ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .border(1.dp, TerminalGold.copy(alpha = 0.35f), RoundedCornerShape(8.dp)),
            colors = CardDefaults.cardColors(containerColor = TerminalGold.copy(alpha = 0.05f))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Disclaimer Information",
                    tint = TerminalGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "DISCLAIMER: All insights are for informational purposes only, do not constitute personalized financial advice, and are sourced from public domains. Trade at your own risk.",
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    color = TerminalGold,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // --- PRESET QUICK QUERIES OR CHAT LIST ---
        if (chatMessages.isEmpty()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Forum,
                    contentDescription = "Chat Logo",
                    tint = TerminalCyan.copy(alpha = 0.3f),
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "OTC ALPHA INTELLIGENCE ASSISTANT",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Ask about stock, forex, crypto, or commodity trends:",
                    fontSize = 11.sp,
                    color = TerminalLightGrey,
                    modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                )

                // Quick presets
                val presets = listOf(
                    "Stocks" to "What is the consensus and short-term analysis for NVDA stock?",
                    "Forex" to "Perform trend analysis and outlook for EUR/USD Forex pairs.",
                    "Crypto" to "Provide real-time sentiment and technical outlook for BTC/USD.",
                    "Commodities" to "What is the current public domain forecast for Gold pricing?"
                )

                presets.forEach { (label, query) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                viewModel.sendChatMessage(query)
                            },
                        colors = CardDefaults.cardColors(containerColor = TerminalMediumGrey.copy(alpha = 0.5f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TerminalCyan.copy(alpha = 0.15f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(TerminalCyan.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = label.uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.sp,
                                    color = TerminalCyan
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = query,
                                fontSize = 11.sp,
                                color = Color.White,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        } else {
            // --- CHAT STREAM ---
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(chatMessages) { message ->
                        ChatMessageCard(
                            message = message,
                            onTransmitToTelegram = {
                                viewModel.sendAssistantMessageToTelegram(message.content)
                            }
                        )
                    }
                }

                // Telegram transmitting status indicator overlay
                telegramStatus?.let { status ->
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = TerminalCyan),
                        shape = RoundedCornerShape(50)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                strokeWidth = 1.5.dp,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = status,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "[X]",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                modifier = Modifier.clickable { viewModel.clearTelegramStatus() }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- INPUT BAR ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (chatMessages.isNotEmpty()) {
                IconButton(
                    onClick = { viewModel.clearChatHistory() },
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("clear_chat_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear Chat History",
                        tint = TerminalCoral,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
            }

            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("chat_input_field"),
                placeholder = { Text("Query AI Analyst...", fontSize = 12.sp, color = TerminalLightGrey) },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = TerminalMediumGrey,
                    unfocusedContainerColor = TerminalMediumGrey,
                    focusedIndicatorColor = TerminalCyan,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledContainerColor = TerminalMediumGrey
                ),
                shape = RoundedCornerShape(24.dp),
                maxLines = 2,
                enabled = !isResponding
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(if (isResponding || inputText.isBlank()) TerminalMediumGrey else TerminalCyan)
                    .clickable(enabled = !isResponding && inputText.isNotBlank()) {
                        viewModel.sendChatMessage(inputText)
                        inputText = ""
                    }
                    .testTag("send_chat_button"),
                contentAlignment = Alignment.Center
            ) {
                if (isResponding) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send Message",
                        tint = if (inputText.isBlank()) TerminalLightGrey else Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessageCard(
    message: ChatMessageEntity,
    onTransmitToTelegram: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUser = message.sender == "user"
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val containerColor = if (isUser) TerminalCyan.copy(alpha = 0.15f) else TerminalMediumGrey
    val borderColor = if (isUser) TerminalCyan.copy(alpha = 0.4f) else Color.Transparent

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(400)) +
                slideInVertically(
                    initialOffsetY = { 24 },
                    animationSpec = tween(400, easing = FastOutSlowInEasing)
                )
    ) {
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = alignment
        ) {
            Card(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = containerColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isUser) "YOU" else "OTC AI ANALYST",
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            color = if (isUser) TerminalCyan else TerminalGold,
                            letterSpacing = 0.5.sp
                        )

                        val timeString = remember(message.timestamp) {
                            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp))
                        }
                        Text(
                            text = timeString,
                            fontSize = 8.sp,
                            color = TerminalLightGrey
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = message.content,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = Color.White
                    )

                    if (!isUser) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Spacer(modifier = Modifier.height(1.dp).background(Color.White.copy(alpha = 0.1f)).fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onTransmitToTelegram() }
                                .padding(vertical = 4.dp)
                                .testTag("transmit_to_telegram_button"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Transmit Insight to Telegram",
                                tint = TerminalCyan,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "TRANSMIT TO TELEGRAM",
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                color = TerminalCyan
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TechnicalBiasTab(
    viewModel: OtcViewModel,
    modifier: Modifier = Modifier
) {
    val selectedStock by viewModel.selectedStock.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        selectedStock?.let { stock ->
            Text(
                text = "${stock.ticker} REACTION & TECHNICAL MATRIX",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- SENTIMENT GAUGE CARD ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Sentiment Gauge",
                            tint = TerminalCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "REAL-TIME SENTIMENT GAUGE",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SentimentNeedleGauge(score = stock.sentimentScore.toFloat())

                    Spacer(modifier = Modifier.height(12.dp))

                    val sentimentClass = when {
                        stock.sentimentScore > 0.4 -> "Strong Bullish"
                        stock.sentimentScore > 0.1 -> "Bullish Bias"
                        stock.sentimentScore < -0.4 -> "Strong Bearish"
                        stock.sentimentScore < -0.1 -> "Bearish Bias"
                        else -> "Neutral Consensus"
                    }
                    val sentimentColor = when {
                        stock.sentimentScore > 0.1 -> TerminalEmerald
                        stock.sentimentScore < -0.1 -> TerminalCoral
                        else -> TerminalGold
                    }

                    Text(
                        text = sentimentClass.uppercase(),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = sentimentColor
                    )
                    Text(
                        text = "Composite Index Score: ${String.format("%.2f", stock.sentimentScore)}",
                        fontSize = 11.sp,
                        color = TerminalLightGrey
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- TECHNICAL RATIOS MATRIX ---
            Text(
                text = "MOMENTUM OSCILLATORS & AVGS",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = TerminalLightGrey
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val rsi = stock.technicalRsi
                    val rsiStatus = when {
                        rsi < 35 -> "OVERSOLD (SCALPING BUY ACCUMULATION)"
                        rsi > 65 -> "OVERBOUGHT (SCALPING SELL EXHAUSTION)"
                        else -> "NEUTRAL CONSOLIDATION"
                    }
                    val rsiColor = when {
                        rsi < 35 -> TerminalEmerald
                        rsi > 65 -> TerminalCoral
                        else -> TerminalLightGrey
                    }
                    TechnicalIndicatorRow(
                        indicator = "RSI (14)",
                        value = rsi.toString(),
                        statusText = rsiStatus,
                        statusColor = rsiColor
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Spacer(modifier = Modifier.height(1.dp).background(MaterialTheme.colorScheme.surfaceVariant).fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))

                    val crossoverStatus = when {
                        stock.technicalSma20 > stock.technicalSma50 -> "BULLISH SMA ALIGNMENT (UPTREND)"
                        stock.technicalSma20 < stock.technicalSma50 -> "BEARISH SMA ALIGNMENT (DOWNTREND)"
                        else -> "CONVERGING AVERAGES"
                    }
                    val crossoverColor = if (stock.technicalSma20 > stock.technicalSma50) TerminalEmerald else TerminalCoral
                    TechnicalIndicatorRow(
                        indicator = "SMA Alignment",
                        value = "SMA20: $${stock.technicalSma20} | SMA50: $${stock.technicalSma50}",
                        statusText = crossoverStatus,
                        statusColor = crossoverColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- NEWS & SOCIAL STREAM ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Newspaper,
                    contentDescription = "News",
                    tint = TerminalCyan,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "AI-INGESTED SOCIAL & NEWS REACTION FEED",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = TerminalLightGrey
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            val newsList = remember(stock.ticker, stock.sentimentScore) {
                generateSimulatedNews(stock.ticker, stock.sentimentScore)
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (news in newsList) {
                    NewsFeedCard(headline = news.first, source = news.second, isPositive = news.third)
                }
            }

        } ?: Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Select a Ticker to view sentiment matrices", color = TerminalLightGrey)
        }
    }
}

@Composable
fun SentimentNeedleGauge(score: Float) {
    val scoreOffset = (score + 1f) / 2f
    val targetAngle = 180f - (scoreOffset * 180f)

    val animatedAngle by animateFloatAsState(
        targetValue = targetAngle,
        label = "needleAngle"
    )

    Box(
        modifier = Modifier
            .size(width = 240.dp, height = 130.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val center = Offset(width / 2f, height)
            val radius = width / 2.3f

            val strokeWidth = 14.dp.toPx()

            drawArc(
                brush = Brush.horizontalGradient(listOf(TerminalCoral, TerminalGold)),
                startAngle = 180f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            drawArc(
                brush = Brush.horizontalGradient(listOf(TerminalGold, TerminalEmerald)),
                startAngle = 270f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            val radians = Math.toRadians(animatedAngle.toDouble())
            val needleLength = radius - 15.dp.toPx()
            val endX = center.x + (needleLength * cos(radians)).toFloat()
            val endY = center.y - (needleLength * sin(radians)).toFloat()

            drawLine(
                color = Color.White,
                start = center,
                end = Offset(endX, endY),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )

            drawCircle(
                color = TerminalCyan,
                radius = 8.dp.toPx(),
                center = center
            )
            drawCircle(
                color = TerminalBlack,
                radius = 3.dp.toPx(),
                center = center
            )
        }
    }
}

@Composable
fun TechnicalIndicatorRow(
    indicator: String,
    value: String,
    statusText: String,
    statusColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = indicator,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White
            )
            Text(
                text = value,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = TerminalCyan
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = statusText,
            color = statusColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun NewsFeedCard(headline: String, source: String, isPositive: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp, 40.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (isPositive) TerminalEmerald else TerminalCoral)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = headline,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = source,
                    fontSize = 10.sp,
                    color = TerminalLightGrey,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun generateSimulatedNews(ticker: String, sentiment: Double): List<Triple<String, String, Boolean>> {
    val source = "FINANCE TERMINAL | " + SimpleDateFormat("HH:mm", Locale.US).format(Date(System.currentTimeMillis() - 1200000))
    val score = sentiment
    return if (score > 0.2) {
        listOf(
            Triple("Heavy buy prints crossing on $ticker ATS Dark Pools; massive block accumulator identified.", "DARKPOOL INTELLIGENCE • 3m ago", true),
            Triple("Analyst upgrades $ticker: Outlining exceptional off-exchange institutional demand ratios.", "BLOOMBERG • 12m ago", true),
            Triple("Retail sentiment surges on social media as $ticker defends key support with massive volume support.", "X CHANNELS • 24m ago", true)
        )
    } else if (score < -0.2) {
        listOf(
            Triple("Institutional block sizes shrinking on public books; distribution noted off-exchange on $ticker.", "DARKPOOL WATCH • 5m ago", false),
            Triple("Market reacts to microeconomic headwinds; $ticker experiences elevated volume crossing at bid.", "FINANCIAL TIMES • 18m ago", false),
            Triple("Options volume spikes as traders hedges downside risk on $ticker following key support breakdown.", "OPTIONSMATRIX • 31m ago", false)
        )
    } else {
        listOf(
            Triple("$ticker displays balanced OTC crossing activity; major volume consolidates near SMA20.", "REUTERS • 7m ago", true),
            Triple("Technical indicators converge: $ticker RSI resting at 50 with standard volume ratios.", "TRADINGVIEW • 15m ago", false),
            Triple("Corporate flows remain range-bound with minor block transactions registered via Barclays LX.", "OTC TRACKER • 28m ago", true)
        )
    }
}
