package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.ui.theme.TerminalBlack
import com.example.ui.theme.TerminalCyan
import com.example.ui.theme.TerminalGold
import com.example.ui.theme.TerminalLightGrey
import com.example.ui.theme.TerminalMediumGrey

@Composable
fun ResourcesScreen(modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- HEADER ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, TerminalCyan.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.otc_launcher_logo_1783552574101),
                contentDescription = "Ndine Analytics Logo",
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.5.dp, TerminalCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "NDINE ANALYTICS ALPHA",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TerminalCyan,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "RESOURCES & EDUCATION",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Institutional market intelligence & off-exchange wisdom.",
                    fontSize = 11.sp,
                    color = TerminalLightGrey,
                    lineHeight = 14.sp
                )
            }
        }

        // --- 1. TRADING EDUCATION HUB ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, TerminalCyan.copy(alpha = 0.2f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = "Trading Education Hub",
                        tint = TerminalCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TRADING EDUCATION HUB",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }

                Text(
                    text = "Beginner-friendly curated courses and structured directories to build raw market expertise:",
                    fontSize = 11.sp,
                    color = TerminalLightGrey
                )

                val eduLinks = listOf(
                    "Investopedia Academy" to ("The premier glossary and guide on stock markets & mechanics." to "https://www.investopedia.com"),
                    "BabyPips Forex Guide" to ("The standard world-renowned roadmap for learning currencies and charts." to "https://www.babypips.com"),
                    "TradingView Mastery" to ("Master support/resistance, oscillators, and candlestick patterns." to "https://www.tradingview.com/ideas/education/")
                )

                eduLinks.forEach { (name, info) ->
                    val (desc, url) = info
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
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = "Open Link",
                            tint = TerminalCyan,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // --- 2. MARKET ANALYSIS RESOURCES ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, TerminalGold.copy(alpha = 0.2f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ShowChart,
                        contentDescription = "Market Analysis",
                        tint = TerminalGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "MARKET ANALYSIS RESOURCES",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }

                Text(
                    text = "Professional analytical directories, data streams, and economic calendars:",
                    fontSize = 11.sp,
                    color = TerminalLightGrey
                )

                val analysisLinks = listOf(
                    "FINRA OTC Transparency" to ("Real-time off-exchange dark pool equity transactions." to "https://www.finra.org/filing-reporting/otc-transparency"),
                    "SEC EDGAR Filing Database" to ("Official corporate disclosures, 10-K, and insider trade files." to "https://www.sec.gov/edgar"),
                    "CME Group Market Data" to ("Global reference pricing models for commodities and metals futures." to "https://www.cmegroup.com"),
                    "CoinMarketCap Research" to ("Exhaustive liquidity maps and whitepapers on crypto tokens." to "https://coinmarketcap.com")
                )

                analysisLinks.forEach { (name, info) ->
                    val (desc, url) = info
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
                                color = TerminalGold
                            )
                            Text(
                                text = desc,
                                fontSize = 10.sp,
                                color = TerminalLightGrey
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = "Open Link",
                            tint = TerminalGold,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // --- 3. ABOUT & SUPPORT (NDINE LABS) ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, TerminalCyan.copy(alpha = 0.15f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "About",
                        tint = TerminalCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ABOUT NDINE LABS & NDINE ANALYTICS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }

                Text(
                    text = "MISSION STATEMENT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TerminalCyan
                )
                Text(
                    text = "To democratize high-grade institutional market intelligence, exposing hidden off-exchange block trades and dark pool liquidity ratios for everyday traders globally.",
                    fontSize = 11.sp,
                    color = TerminalLightGrey,
                    lineHeight = 16.sp
                )

                Text(
                    text = "FOUNDING STORY & VISION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TerminalCyan
                )
                Text(
                    text = "Produced by Ndine Labs, Ndine Analytics was created to level the playing field. Traditionally, dark pool prints and institutional volume indicators were reserved for high-frequency desks. Our vision is to pack these advanced data models into an intuitive, real-time analytics assistant covering stocks, crypto, forex, commodities, and metals.",
                    fontSize = 11.sp,
                    color = TerminalLightGrey,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(4.dp))
                Spacer(modifier = Modifier.height(1.dp).background(TerminalMediumGrey).fillMaxWidth())
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "24/7 CUSTOMER SUPPORT & ENQUIRIES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TerminalGold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { uriHandler.openUri("mailto:ndinethemba69@gmail.com") },
                        colors = ButtonDefaults.buttonColors(containerColor = TerminalMediumGrey),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.weight(1f).height(36.dp).testTag("resource_email_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Email, contentDescription = "Email Support", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("EMAIL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Button(
                        onClick = { uriHandler.openUri("tel:+27613886126") },
                        colors = ButtonDefaults.buttonColors(containerColor = TerminalMediumGrey),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.weight(1f).height(36.dp).testTag("resource_call_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = "Call Support", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("CALL US", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Email: ndinethemba69@gmail.com", fontSize = 10.sp, color = TerminalLightGrey)
                    Text(text = "Phone: (+27) 61 388 6126", fontSize = 10.sp, color = TerminalLightGrey)
                }
            }
        }
    }
}
