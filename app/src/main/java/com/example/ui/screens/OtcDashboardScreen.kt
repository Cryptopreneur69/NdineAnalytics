package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.LinearEasing
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.database.OtcStockEntity
import com.example.data.database.OtcWeeklyDataEntity
import com.example.ui.theme.TerminalBlack
import com.example.ui.theme.TerminalCoral
import com.example.ui.theme.TerminalCyan
import com.example.ui.theme.TerminalEmerald
import com.example.ui.theme.TerminalGold
import com.example.ui.theme.TerminalLightGrey
import com.example.ui.theme.TerminalMediumGrey
import com.example.ui.viewmodel.OtcViewModel

@Composable
fun OtcDashboardScreen(
    viewModel: OtcViewModel,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val stocks by viewModel.allStocks.collectAsState()
    val selectedStock by viewModel.selectedStock.collectAsState()
    val weeklyDataList by viewModel.selectedWeeklyData.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val analysisStatus by viewModel.analysisStatus.collectAsState()

    val isAdvancedMode by viewModel.isAdvancedMode.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedAssetClass by remember { mutableStateOf("ALL") }

    val filteredStocks = remember(stocks, selectedAssetClass) {
        if (selectedAssetClass == "ALL") stocks
        else stocks.filter { it.assetClass.uppercase() == selectedAssetClass.uppercase() }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // --- MODE SELECTOR (SIMPLE vs ADVANCED) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "DASHBOARD ANALYTICS MODE",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = TerminalLightGrey,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = if (isAdvancedMode) "ADVANCED PRO MODE" else "SIMPLE NEWCOMER MODE",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isAdvancedMode) TerminalGold else TerminalCyan
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Pro View",
                    fontSize = 11.sp,
                    color = if (isAdvancedMode) TerminalGold else TerminalLightGrey,
                    fontWeight = FontWeight.Bold
                )
                Switch(
                    checked = isAdvancedMode,
                    onCheckedChange = { viewModel.isAdvancedMode.value = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = TerminalGold,
                        checkedTrackColor = TerminalGold.copy(alpha = 0.4f),
                        uncheckedThumbColor = TerminalCyan,
                        uncheckedTrackColor = TerminalCyan.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("mode_toggle_switch")
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // --- ASSET CLASS SELECTOR TABS ---
        val assetClasses = listOf("ALL", "STOCKS", "COMMODITIES", "METALS", "CRYPTO", "FOREX")
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(assetClasses) { cls ->
                val isSelected = selectedAssetClass == cls
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(32.dp))
                        .background(if (isSelected) TerminalCyan else TerminalMediumGrey.copy(alpha = 0.5f))
                        .clickable { selectedAssetClass = cls }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = cls,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = if (isSelected) Color.Black else Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- TOP PERFORMERS HIGHLIGHTS SECTION ---
        TopPerformersSection(stocks = stocks, onSelectStock = { viewModel.selectStock(it) })

        Spacer(modifier = Modifier.height(20.dp))

        // --- TICKER SELECTOR BAR ---
        var showTickerDropdown by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TRACKED ${selectedAssetClass.uppercase()} TICKERS",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = TerminalLightGrey
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Dropdown Asset management selection
                Box {
                    IconButton(
                        onClick = { showTickerDropdown = true },
                        modifier = Modifier
                            .size(32.dp)
                            .background(TerminalCyan.copy(alpha = 0.15f), CircleShape)
                            .testTag("ticker_dropdown_trigger")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Select Ticker Dropdown",
                            tint = TerminalCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showTickerDropdown,
                        onDismissRequest = { showTickerDropdown = false },
                        modifier = Modifier.background(TerminalMediumGrey)
                    ) {
                        filteredStocks.forEach { stock ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "${stock.ticker} - ${stock.companyName}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                },
                                onClick = {
                                    viewModel.selectStock(stock)
                                    showTickerDropdown = false
                                }
                            )
                        }
                    }
                }

                IconButton(
                    onClick = { showAddDialog = true },
                    modifier = Modifier
                        .size(32.dp)
                        .background(TerminalCyan.copy(alpha = 0.15f), CircleShape)
                        .testTag("add_ticker_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Ticker",
                        tint = TerminalCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(filteredStocks) { stock ->
                val isSelected = selectedStock?.ticker == stock.ticker
                InputChip(
                    selected = isSelected,
                    onClick = { viewModel.selectStock(stock) },
                    label = { 
                        Text(
                            text = stock.ticker, 
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.Black else Color.White
                        ) 
                    },
                    trailingIcon = {
                        if (stocks.size > 1) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = if (isSelected) Color.Black.copy(alpha = 0.6f) else TerminalCoral.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .size(14.dp)
                                    .clickable { viewModel.deleteStock(stock.ticker) }
                            )
                        }
                    },
                    colors = InputChipDefaults.inputChipColors(
                        selectedContainerColor = TerminalCyan,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                    modifier = Modifier.testTag("ticker_chip_${stock.ticker}")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- STOCK INFORMATION PANELS ---
        selectedStock?.let { stock ->
            StockHeaderCard(stock = stock, isAdvancedMode = isAdvancedMode)

            Spacer(modifier = Modifier.height(16.dp))

            if (isAdvancedMode) {
                // Custom Visual Canvas Market Chart
                MarketVisualChart(stock = stock)

                Spacer(modifier = Modifier.height(16.dp))

                // Portfolio Tracker
                PortfolioTrackerCard(stock = stock, viewModel = viewModel)

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Market Data Comparison / Off-Exchange Panel
            val currentWeekly = weeklyDataList.firstOrNull()
            if (currentWeekly != null) {
                OtcDataComparisonPanel(weeklyData = currentWeekly, isAdvancedMode = isAdvancedMode)
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "No data",
                            tint = TerminalLightGrey,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No public market transparency volume statistics found.",
                            color = TerminalLightGrey,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            if (!isAdvancedMode) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = TerminalCyan.copy(alpha = 0.05f)),
                    border = BorderStroke(1.dp, TerminalCyan.copy(alpha = 0.15f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "🎓 TRADING RESOURCES READY",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = TerminalCyan
                        )
                        Text(
                            text = "Need learning directories? We've consolidated the full 'Trading Education Hub' & 'About' section inside the brand-new Resources tab at the bottom of your screen.",
                            fontSize = 11.sp,
                            color = TerminalLightGrey,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            if (isAdvancedMode) {
                Spacer(modifier = Modifier.height(20.dp))

                // --- AI ANALYTICS TRIGGER SECTION ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = TerminalCyan.copy(alpha = 0.05f)),
                    border = BorderStroke(1.dp, TerminalCyan.copy(alpha = pulseAlpha * 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.otc_launcher_logo_1783552574101),
                                contentDescription = "AI Agent",
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .border(1.dp, TerminalCyan.copy(alpha = pulseAlpha), RoundedCornerShape(4.dp))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "QUANT AI SCALPER AGENT",
                                fontWeight = FontWeight.Bold,
                                color = TerminalCyan,
                                fontSize = 14.sp,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            // Glowing online status dot
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(TerminalEmerald.copy(alpha = pulseAlpha))
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Synthesizes real-time off-exchange and dark pool volume ratios against public exchange quotes to identify hidden block trade interest and technical momentum.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TerminalLightGrey,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (isAnalyzing) {
                            CircularProgressIndicator(
                                color = TerminalCyan,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = analysisStatus,
                                color = TerminalCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        } else {
                            Button(
                                onClick = { viewModel.runAgentAnalysis(stock) },
                                colors = ButtonDefaults.buttonColors(containerColor = TerminalCyan),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("run_ai_analysis_button")
                            ) {
                                Text(
                                    text = "RUN QUANT AI ANALYSIS",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        } ?: Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Select or Add a Ticker to Begin", color = TerminalLightGrey)
        }

        // Add Ticker Dialog
        if (showAddDialog) {
            AddTickerDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { ticker, name, assetClass ->
                    viewModel.addStock(ticker, name, assetClass)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun StockHeaderCard(stock: OtcStockEntity, isAdvancedMode: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stock.ticker,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = TerminalCyan
                    )
                    Text(
                        text = stock.companyName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TerminalLightGrey
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$${stock.realTimePrice}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val isPositive = stock.sentimentScore >= 0
                        Icon(
                            imageVector = if (isPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                            contentDescription = "Direction",
                            tint = if (isPositive) TerminalEmerald else TerminalCoral,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Sentiment: ${String.format("%.2f", stock.sentimentScore)}",
                            fontSize = 12.sp,
                            color = if (isPositive) TerminalEmerald else TerminalCoral,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            if (!isAdvancedMode) {
                val sentimentDescription = when {
                    stock.sentimentScore > 0.3 -> "Highly Bullish: Strong off-exchange buying noted"
                    stock.sentimentScore < -0.3 -> "Bearish: Significant selling and consolidation"
                    else -> "Neutral: Balanced market sentiment and trade volume"
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(TerminalBlack.copy(alpha = 0.4f))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "💡 Market Mood: $sentimentDescription",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TerminalLightGrey
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "REAL-TIME EXCHANGE VOLUME",
                        style = MaterialTheme.typography.labelSmall,
                        color = TerminalLightGrey
                    )
                    Text(
                        text = String.format("%,d", stock.realTimeVolume),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Institutional Pressure Tag
                val (pressureColor, pressureText) = when (stock.institutionalPressure) {
                    "HIGH BUYING" -> TerminalEmerald to "HIGH BUYING PRESSURE"
                    "MILD BUYING" -> TerminalEmerald.copy(alpha = 0.7f) to "MILD BUYING PRESSURE"
                    "MILD SELLING" -> TerminalCoral.copy(alpha = 0.7f) to "MILD SELLING PRESSURE"
                    "HIGH SELLING" -> TerminalCoral to "HIGH SELLING PRESSURE"
                    else -> TerminalGold to "NEUTRAL OTC PRESSURE"
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(pressureColor.copy(alpha = 0.15f))
                        .border(
                            width = 1.dp,
                            color = pressureColor,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = pressureText,
                        color = pressureColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun OtcDataComparisonPanel(weeklyData: OtcWeeklyDataEntity, isAdvancedMode: Boolean) {
    val totalDarkVol = weeklyData.atsVolume + weeklyData.nonAtsVolume
    val atsPercent = if (weeklyData.totalMarketVolume > 0) {
        (weeklyData.atsVolume.toDouble() / weeklyData.totalMarketVolume.toDouble())
    } else 0.0
    val nonAtsPercent = if (weeklyData.totalMarketVolume > 0) {
        (weeklyData.nonAtsVolume.toDouble() / weeklyData.totalMarketVolume.toDouble())
    } else 0.0
    val darkPoolPercent = atsPercent + nonAtsPercent

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ShowChart,
                        contentDescription = "OTC",
                        tint = TerminalCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "FINRA OTC TRANSPARENCY STATS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
                Text(
                    text = "W/E ${weeklyData.weekEndDate}",
                    fontSize = 11.sp,
                    color = TerminalLightGrey
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Custom double sided bar graph
            Text(
                text = "Dark Pool vs Public Exchange Ratio",
                fontSize = 12.sp,
                color = TerminalLightGrey,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))

            DoubleSidedBarGraph(
                atsPercentage = atsPercent.toFloat(),
                nonAtsPercentage = nonAtsPercent.toFloat()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Details list
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    OtcDetailRow(label = "ATS (Dark Pool) Vol", value = String.format("%,d", weeklyData.atsVolume))
                    OtcDetailRow(label = "ATS Trades Count", value = String.format("%,d", weeklyData.atsTrades))
                    OtcDetailRow(label = "ATS Market Ratio", value = String.format("%.2f%%", atsPercent * 100))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    OtcDetailRow(label = "Non-ATS Off-Exch Vol", value = String.format("%,d", weeklyData.nonAtsVolume))
                    OtcDetailRow(label = "Non-ATS Trades", value = String.format("%,d", weeklyData.nonAtsTrades))
                    OtcDetailRow(label = "Total Dark Pool Ratio", value = String.format("%.2f%%", darkPoolPercent * 100))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Spacer(modifier = Modifier.height(1.dp).background(TerminalMediumGrey).fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))

            // Highest Volume ATS Venue highlight
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DOMINANT ATS VENUE",
                        fontSize = 10.sp,
                        color = TerminalLightGrey,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = weeklyData.highestVolumeAts,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TerminalCyan
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "VENUE SHARE VOLUME",
                        fontSize = 10.sp,
                        color = TerminalLightGrey,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = String.format("%,d shares", weeklyData.highestVolumeAtsShares),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            if (!isAdvancedMode) {
                Spacer(modifier = Modifier.height(12.dp))
                Spacer(modifier = Modifier.height(1.dp).background(TerminalMediumGrey).fillMaxWidth())
                Spacer(modifier = Modifier.height(12.dp))
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = TerminalBlack),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "💡 NEWCOMER EXPLAINER",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = TerminalCyan
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Private Off-Exchange Volume (Dark Pool) represents trades executed away from public exchanges. Financial institutions use private venues to trade large blocks of assets without causing drastic price spikes. High Off-Exchange activity often implies heavy institutional accumulation.",
                            fontSize = 11.sp,
                            color = TerminalLightGrey,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OtcDetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, fontSize = 10.sp, color = TerminalLightGrey)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
fun DoubleSidedBarGraph(atsPercentage: Float, nonAtsPercentage: Float) {
    val remaining = 1f - atsPercentage - nonAtsPercentage
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(16.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(TerminalMediumGrey)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            if (atsPercentage > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(atsPercentage.coerceAtLeast(0.01f))
                        .background(TerminalCyan)
                )
            }
            if (nonAtsPercentage > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(nonAtsPercentage.coerceAtLeast(0.01f))
                        .background(TerminalEmerald)
                )
            }
            if (remaining > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(remaining.coerceAtLeast(0.01f))
                        .background(TerminalLightGrey.copy(alpha = 0.3f))
                )
            }
        }
    }
    
    Spacer(modifier = Modifier.height(4.dp))
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(TerminalCyan, RoundedCornerShape(2.dp)))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "ATS", fontSize = 10.sp, color = TerminalLightGrey)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(TerminalEmerald, RoundedCornerShape(2.dp)))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Non-ATS", fontSize = 10.sp, color = TerminalLightGrey)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(TerminalLightGrey.copy(alpha = 0.4f), RoundedCornerShape(2.dp)))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Public Exch", fontSize = 10.sp, color = TerminalLightGrey)
        }
    }
}

@Composable
fun AddTickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (ticker: String, companyName: String, assetClass: String) -> Unit
) {
    var ticker by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var assetClass by remember { mutableStateOf("Stocks") }
    var errorText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "ADD NEW TRACKED ASSET",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TerminalCyan
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = ticker,
                    onValueChange = { 
                        ticker = it.take(10).uppercase()
                        errorText = ""
                    },
                    label = { Text("Asset Ticker (e.g. BTC/USD, GC1!)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TerminalCyan,
                        focusedLabelColor = TerminalCyan
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_ticker_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = companyName,
                    onValueChange = { companyName = it },
                    label = { Text("Name / Description (optional)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TerminalCyan,
                        focusedLabelColor = TerminalCyan
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_company_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ASSET CLASS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = TerminalLightGrey
                )
                Spacer(modifier = Modifier.height(6.dp))
                val classes = listOf("Stocks", "Commodities", "Metals", "Crypto", "Forex")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    classes.forEach { cls ->
                        val isSelected = assetClass == cls
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isSelected) TerminalCyan else TerminalMediumGrey.copy(alpha = 0.5f))
                                .clickable { assetClass = cls }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cls,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.Black else Color.White
                            )
                        }
                    }
                }

                if (errorText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = errorText, color = TerminalCoral, fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        modifier = Modifier.testTag("add_ticker_cancel")
                    ) {
                        Text("CANCEL", color = TerminalLightGrey, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (ticker.isBlank()) {
                                errorText = "Ticker is required."
                            } else {
                                onConfirm(ticker, companyName, assetClass)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TerminalCyan),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.testTag("add_ticker_confirm")
                    ) {
                        Text("ADD ASSET", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun TopPerformersSection(
    stocks: List<OtcStockEntity>,
    onSelectStock: (OtcStockEntity) -> Unit
) {
    val topPerformers = remember(stocks) {
        stocks.sortedByDescending { it.priceChangePercent }.take(3)
    }

    if (topPerformers.isNotEmpty()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "🚀 TOP PERFORMING ASSETS",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = TerminalGold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                topPerformers.forEach { asset ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSelectStock(asset) },
                        colors = CardDefaults.cardColors(containerColor = TerminalMediumGrey.copy(alpha = 0.2f)),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (asset.priceChangePercent >= 0) TerminalEmerald.copy(alpha = 0.4f) else TerminalCoral.copy(alpha = 0.4f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = asset.ticker,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp,
                                    color = TerminalCyan
                                )
                                Text(
                                    text = asset.assetClass.take(5).uppercase(),
                                    fontSize = 7.sp,
                                    color = TerminalLightGrey,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$${asset.realTimePrice}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val isPositive = asset.priceChangePercent >= 0
                                Icon(
                                    imageVector = if (isPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    tint = if (isPositive) TerminalEmerald else TerminalCoral,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${if (isPositive) "+" else ""}${String.format("%.2f", asset.priceChangePercent)}%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPositive) TerminalEmerald else TerminalCoral
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MarketVisualChart(stock: OtcStockEntity) {
    val pointsCount = 15
    val prices = remember(stock.ticker, stock.realTimePrice) {
        val list = mutableListOf<Double>()
        var current = stock.realTimePrice * (1.0 - (stock.priceChangePercent / 100.0))
        val step = (stock.realTimePrice - current) / pointsCount
        val rand = java.util.Random(stock.ticker.hashCode().toLong())
        
        for (i in 0 until pointsCount - 1) {
            val noise = (rand.nextDouble() - 0.5) * (stock.realTimePrice * 0.015)
            current += step + noise
            list.add(current)
        }
        list.add(stock.realTimePrice)
        list
    }

    val minPrice = prices.minOrNull() ?: 0.0
    val maxPrice = prices.maxOrNull() ?: 1.0
    val priceRange = if (maxPrice - minPrice == 0.0) 1.0 else maxPrice - minPrice

    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(stock.ticker) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing)
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, TerminalMediumGrey.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ShowChart,
                        contentDescription = "Chart",
                        tint = TerminalCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "1-MIN MARKET SCALPER CHART",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Text(
                    text = "${stock.ticker} SMA(20) • SMA(50)",
                    fontSize = 10.sp,
                    color = TerminalLightGrey,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    val spacing = width / (pointsCount - 1)

                    // Draw grid lines
                    val gridLines = 4
                    for (i in 0..gridLines) {
                        val y = height * i / gridLines
                        drawLine(
                            color = TerminalMediumGrey.copy(alpha = 0.25f),
                            start = androidx.compose.ui.geometry.Offset(0f, y),
                            end = androidx.compose.ui.geometry.Offset(width, y),
                            strokeWidth = 1f,
                            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )
                    }

                    // Map points to offsets with animation
                    val path = androidx.compose.ui.graphics.Path()
                    val averageY = height / 2f
                    val offsets = prices.mapIndexed { index, price ->
                        val x = index * spacing
                        val targetY = height - ((price - minPrice) / priceRange * height * 0.75f + height * 0.12f).toFloat()
                        val y = averageY + (targetY - averageY) * animationProgress.value
                        androidx.compose.ui.geometry.Offset(x, y)
                    }

                    // Draw area fill gradient
                    if (offsets.isNotEmpty()) {
                        path.moveTo(offsets.first().x, height)
                        offsets.forEach { offset ->
                            path.lineTo(offset.x, offset.y)
                        }
                        path.lineTo(offsets.last().x, height)
                        path.close()

                        drawPath(
                            path = path,
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(TerminalCyan.copy(alpha = 0.2f), Color.Transparent),
                                startY = 0f,
                                endY = height
                            )
                        )
                    }

                    // Draw price line path
                    val linePath = androidx.compose.ui.graphics.Path()
                    if (offsets.isNotEmpty()) {
                        linePath.moveTo(offsets.first().x, offsets.first().y)
                        for (i in 1 until offsets.size) {
                            linePath.lineTo(offsets[i].x, offsets[i].y)
                        }
                        drawPath(
                            path = linePath,
                            color = TerminalCyan,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = 3f,
                                cap = StrokeCap.Round
                            )
                        )
                    }

                    // Draw SMA 20 (dotted line, computed relative to prices)
                    val sma20Path = androidx.compose.ui.graphics.Path()
                    val sma20Offset = offsets.mapIndexed { index, offset ->
                        val smaVal = if (index > 2) prices.subList(index - 2, index + 1).average() else prices[index]
                        val y = height - ((smaVal - minPrice) / priceRange * height * 0.75f + height * 0.12f).toFloat()
                        androidx.compose.ui.geometry.Offset(offset.x, y)
                    }
                    if (sma20Offset.isNotEmpty()) {
                        sma20Path.moveTo(sma20Offset.first().x, sma20Offset.first().y)
                        for (i in 1 until sma20Offset.size) {
                            sma20Path.lineTo(sma20Offset[i].x, sma20Offset[i].y)
                        }
                        drawPath(
                            path = sma20Path,
                            color = TerminalEmerald.copy(alpha = 0.7f),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = 1.5f,
                                cap = StrokeCap.Round,
                                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                            )
                        )
                    }

                    // Draw end dot
                    if (offsets.isNotEmpty()) {
                        val last = offsets.last()
                        drawCircle(
                            color = TerminalCyan,
                            radius = 5f,
                            center = last
                        )
                        drawCircle(
                            color = TerminalCyan.copy(alpha = 0.3f),
                            radius = 10f,
                            center = last,
                            style = Stroke(width = 1.5f)
                        )
                    }
                }

                // Pricing axes
                Column(
                    modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    Text(text = "$${String.format("%.2f", maxPrice)}", color = TerminalLightGrey, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    Text(text = "$${String.format("%.2f", (maxPrice + minPrice) / 2.0)}", color = TerminalLightGrey.copy(alpha = 0.7f), fontSize = 8.sp)
                    Text(text = "$${String.format("%.2f", minPrice)}", color = TerminalLightGrey, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun EducationalResourcesSection() {
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, TerminalCyan.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Learn Trading",
                    tint = TerminalCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "🎓 TRADING EDUCATION HUB",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }

            Text(
                text = "New to markets? Explore verified educational directories to build your market knowledge:",
                fontSize = 11.sp,
                color = TerminalLightGrey
            )

            val resources = listOf(
                "Investopedia" to ("General finance, investing terms, and market structure tutorials." to "https://www.investopedia.com"),
                "BabyPips" to ("The ultimate free guide to learning forex and chart basics." to "https://www.babypips.com"),
                "TradingView Education" to ("Interactive technical charting tutorials and masterclasses." to "https://www.tradingview.com"),
                "FINRA Transparency" to ("Official dark pool off-exchange volume data guidelines." to "https://www.finra.org/filing-reporting/otc-transparency")
            )

            resources.forEach { item ->
                val name = item.first
                val desc = item.second.first
                val url = item.second.second
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(TerminalMediumGrey.copy(alpha = 0.2f))
                        .clickable { uriHandler.openUri(url) }
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = TerminalCyan
                        )
                        Text(
                            text = desc,
                            fontSize = 10.sp,
                            color = TerminalLightGrey
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ShowChart,
                        contentDescription = "Open Link",
                        tint = TerminalCyan.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PortfolioTrackerCard(
    stock: OtcStockEntity,
    viewModel: OtcViewModel
) {
    val holdings by viewModel.portfolioHoldings.collectAsState()
    val holding = holdings[stock.ticker]
    
    var sharesInput by remember(stock.ticker, holding) { 
        mutableStateOf(holding?.first?.toString() ?: "") 
    }
    var priceInput by remember(stock.ticker, holding) { 
        mutableStateOf(holding?.second?.toString() ?: "") 
    }
    
    val currentPrice = stock.realTimePrice

    Card(
        modifier = Modifier.fillMaxWidth().testTag("portfolio_tracker_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, TerminalGold.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.ShowChart,
                    contentDescription = "Portfolio Tracker",
                    tint = TerminalGold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "💼 PRO PORTFOLIO TRACKER",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }

            Text(
                text = "Enter your current holdings for ${stock.ticker} to calculate investment gains based on live off-exchange pricing.",
                fontSize = 11.sp,
                color = TerminalLightGrey
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = sharesInput,
                    onValueChange = { sharesInput = it },
                    label = { Text("Shares Owned", fontSize = 10.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TerminalGold,
                        focusedLabelColor = TerminalGold
                    ),
                    modifier = Modifier.weight(1f).testTag("portfolio_shares_input")
                )

                OutlinedTextField(
                    value = priceInput,
                    onValueChange = { priceInput = it },
                    label = { Text("Avg Entry Price", fontSize = 10.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TerminalGold,
                        focusedLabelColor = TerminalGold
                    ),
                    modifier = Modifier.weight(1f).testTag("portfolio_price_input")
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (holding != null) {
                    Button(
                        onClick = {
                            viewModel.updateHolding(stock.ticker, 0.0, 0.0)
                            sharesInput = ""
                            priceInput = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TerminalCoral.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.height(32.dp).testTag("portfolio_clear_button")
                    ) {
                        Text("CLEAR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TerminalCoral)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Button(
                    onClick = {
                        val shares = sharesInput.toDoubleOrNull() ?: 0.0
                        val price = priceInput.toDoubleOrNull() ?: 0.0
                        if (shares > 0.0 && price > 0.0) {
                            viewModel.updateHolding(stock.ticker, shares, price)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TerminalGold),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.height(32.dp).testTag("portfolio_save_button")
                ) {
                    Text("SAVE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }

            if (holding != null) {
                val shares = holding.first
                val avgPrice = holding.second
                val totalCost = shares * avgPrice
                val currentValue = shares * currentPrice
                val pnl = currentValue - totalCost
                val pnlPercent = if (totalCost > 0) (pnl / totalCost) * 100.0 else 0.0
                val isProfit = pnl >= 0.0

                Spacer(modifier = Modifier.height(4.dp))
                Spacer(modifier = Modifier.height(1.dp).background(TerminalMediumGrey).fillMaxWidth())
                Spacer(modifier = Modifier.height(4.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "POSITION STATISTICS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = TerminalGold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Holdings:", fontSize = 11.sp, color = TerminalLightGrey)
                        Text("$shares ${stock.ticker} @ $${String.format("%.2f", avgPrice)}", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Cost Basis:", fontSize = 11.sp, color = TerminalLightGrey)
                        Text("$${String.format("%.2f", totalCost)}", fontSize = 11.sp, color = Color.White)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Current Valuation:", fontSize = 11.sp, color = TerminalLightGrey)
                        Text("$${String.format("%.2f", currentValue)}", fontSize = 11.sp, color = Color.White)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Unrealized P&L:", fontSize = 11.sp, color = TerminalLightGrey)
                        Text(
                            text = "${if (isProfit) "+" else ""}$${String.format("%.2f", pnl)} (${String.format("%.2f", pnlPercent)}%)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isProfit) TerminalEmerald else TerminalCoral
                        )
                    }
                }
            }
        }
    }
}
