package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.TradingSignalEntity
import com.example.ui.theme.DarkOnSurface
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

@Composable
fun SignalsScreen(
    viewModel: OtcViewModel,
    modifier: Modifier = Modifier
) {
    val signals by viewModel.allSignals.collectAsState()
    val telegramStatus by viewModel.telegramStatus.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // --- HEADER ACTION ROW ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ACTIONABLE TRADING SIGNALS",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            if (signals.isNotEmpty()) {
                IconButton(
                    onClick = { viewModel.clearAllSignals() },
                    modifier = Modifier.testTag("clear_signals_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear History",
                        tint = TerminalCoral.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Telegram alerts notice banner
        telegramStatus?.let { status ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = TerminalCyan.copy(alpha = 0.12f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "Alert",
                        tint = TerminalCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = status,
                        color = TerminalCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { viewModel.clearTelegramStatus() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TerminalCyan,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (signals.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "No Signals",
                        tint = TerminalLightGrey.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No Trading Signals Generated Yet",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Trigger 'Run Quant AI Analysis' in Dashboard.",
                        fontSize = 12.sp,
                        color = TerminalLightGrey
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(signals, key = { it.id }) { signal ->
                    SignalItemCard(
                        signal = signal,
                        onSendTelegram = { viewModel.triggerTelegramSend(signal) }
                    )
                }
            }
        }
    }
}

@Composable
fun SignalItemCard(
    signal: TradingSignalEntity,
    onSendTelegram: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    val signalColor = when (signal.signalType) {
        "BUY" -> TerminalEmerald
        "SELL" -> TerminalCoral
        else -> TerminalGold
    }

    val formattedDate = remember(signal.timestamp) {
        val sdf = SimpleDateFormat("MM-dd HH:mm:ss", Locale.US)
        sdf.format(Date(signal.timestamp))
    }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(450)) +
                slideInVertically(
                    initialOffsetY = { 32 },
                    animationSpec = tween(450, easing = FastOutSlowInEasing)
                )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .clickable { expanded = !expanded }
                .testTag("signal_card_${signal.ticker}_${signal.id}"),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // --- SUMMARY HEADER ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(signalColor.copy(alpha = 0.15f))
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = if (signal.signalType == "BUY") Icons.Default.TrendingUp else Icons.Default.TrendingFlat,
                                contentDescription = signal.signalType,
                                tint = signalColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = signal.ticker,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = Color.White
                               )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(signalColor.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = signal.signalType,
                                        color = signalColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                            Text(
                                text = formattedDate,
                                fontSize = 11.sp,
                                color = TerminalLightGrey
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "STRENGTH",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = TerminalLightGrey
                            )
                            Text(
                                text = "${signal.strength}%",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = signalColor
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Expand",
                            tint = TerminalLightGrey,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // --- EXPANDED ANALYSIS & RATIONALE ---
                AnimatedVisibility(visible = expanded) {
                    Column {
                        Spacer(modifier = Modifier.height(14.dp))
                        Spacer(modifier = Modifier.height(1.dp).background(TerminalMediumGrey).fillMaxWidth())
                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "QUANT AGENT RATIONALE & FEED",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = TerminalCyan
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Stylised Markdown text rendering
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(TerminalBlack)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = signal.scalphRationale.trim(),
                                style = MaterialTheme.typography.bodySmall,
                                color = DarkOnSurface,
                                fontFamily = FontFamily.SansSerif,
                                lineHeight = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Send to Telegram action inside signal card
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Telegram status",
                                    tint = if (signal.telegramSent) TerminalEmerald else TerminalLightGrey,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (signal.telegramSent) "Transmitted to Telegram" else "Pending transmission",
                                    fontSize = 11.sp,
                                    color = if (signal.telegramSent) TerminalEmerald else TerminalLightGrey,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Button(
                                onClick = onSendTelegram,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (signal.telegramSent) TerminalMediumGrey else TerminalCyan
                                ),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier
                                    .height(32.dp)
                                    .testTag("send_telegram_button_${signal.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Telegram Icon",
                                    tint = if (signal.telegramSent) Color.White else Color.Black,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (signal.telegramSent) "RE-TRANSMIT" else "SEND ALERT",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (signal.telegramSent) Color.White else Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
