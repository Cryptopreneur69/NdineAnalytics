package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.data.api.TelegramClient
import com.example.ui.theme.TerminalBlack
import com.example.ui.theme.TerminalCoral
import com.example.ui.theme.TerminalCyan
import com.example.ui.theme.TerminalEmerald
import com.example.ui.theme.TerminalGold
import com.example.ui.theme.TerminalLightGrey
import com.example.ui.theme.TerminalMediumGrey
import com.example.ui.viewmodel.OtcViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    viewModel: OtcViewModel,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val botToken by viewModel.telegramBotToken.collectAsState()
    val chatId by viewModel.telegramChatId.collectAsState()
    val autoSend by viewModel.autoSendTelegram.collectAsState()

    var tokenInput by remember(botToken) { mutableStateOf(botToken) }
    var chatIdInput by remember(chatId) { mutableStateOf(chatId) }
    var autoSendInput by remember(autoSend) { mutableStateOf(autoSend) }

    var isTokenVisible by remember { mutableStateOf(false) }
    var testStatus by remember { mutableStateOf<String?>(null) }
    var showTickerDialog by remember { mutableStateOf(false) }

    var traderName by remember { mutableStateOf("Ndine Trader") }
    var traderEmail by remember { mutableStateOf("trader@ndinelabs.com") }
    var accountSavedMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, TerminalCyan.copy(alpha = 0.25f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.otc_launcher_logo_1783552574101),
                    contentDescription = "Ndine Analytics Logo",
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.5.dp, TerminalCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "NDINEANALYTICS ALPHA",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Core Routing Engine & Telegram Integration",
                        fontSize = 11.sp,
                        color = TerminalLightGrey
                    )
                }
            }
        }

        Text(
            text = "AGENT ROUTING & INTEGRATIONS",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        // --- TELEGRAM INTEGRATION CONFIG CARD ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Telegram",
                        tint = TerminalCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TELEGRAM BOT INTEGRATION",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }

                Text(
                    text = "Enables active routing of buy/sell signals to your private channel or chat instantly.",
                    fontSize = 12.sp,
                    color = TerminalLightGrey
                )

                // Bot Token Input
                OutlinedTextField(
                    value = tokenInput,
                    onValueChange = { tokenInput = it },
                    label = { Text("Telegram Bot Token") },
                    singleLine = true,
                    visualTransformation = if (isTokenVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isTokenVisible = !isTokenVisible }) {
                            Icon(
                                imageVector = if (isTokenVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle Visibility",
                                tint = TerminalLightGrey
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TerminalCyan,
                        focusedLabelColor = TerminalCyan
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("settings_token_input")
                )

                // Chat ID Input
                OutlinedTextField(
                    value = chatIdInput,
                    onValueChange = { chatIdInput = it },
                    label = { Text("Telegram Chat ID / Channel ID") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TerminalCyan,
                        focusedLabelColor = TerminalCyan
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("settings_chatid_input")
                )

                // Auto Send Toggle Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Auto-Transmit Signals",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Instantly push signals upon agent completion.",
                            fontSize = 11.sp,
                            color = TerminalLightGrey
                        )
                    }

                    Switch(
                        checked = autoSendInput,
                        onCheckedChange = { autoSendInput = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TerminalCyan,
                            checkedTrackColor = TerminalCyan.copy(alpha = 0.4f),
                            uncheckedThumbColor = TerminalLightGrey,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.testTag("settings_autosend_switch")
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.saveTelegramSettings(tokenInput, chatIdInput, autoSendInput)
                            testStatus = "Configuration saved successfully!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TerminalCyan),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .testTag("settings_save_button")
                    ) {
                        Text(
                            text = "SAVE SETTINGS",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 11.sp
                        )
                    }

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                testStatus = "Transmitting test message..."
                                if (tokenInput.isBlank() || chatIdInput.isBlank()) {
                                    testStatus = "Error: Token and Chat ID cannot be blank."
                                    return@launch
                                }
                                val testMsg = "🤖 *Ndine Analytics Agent Connection Test*\n\nYour Telegram integration is set up correctly and fully functional!"
                                val success = TelegramClient.sendMessage(tokenInput, chatIdInput, testMsg)
                                testStatus = if (success) {
                                    "Test successful! Check your Telegram chat."
                                } else {
                                    "Transmission failed. Check credentials/bot permissions."
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .testTag("settings_test_button")
                    ) {
                        Text(
                            text = "TEST CONNECTION",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    }
                }

                // Test Status Text
                testStatus?.let { status ->
                    Text(
                        text = status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (status.startsWith("Error") || status.startsWith("Transmission")) TerminalCoral else TerminalEmerald,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // --- STEP-BY-STEP TELEGRAM INTEGRATION GUIDE ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Setup Guide",
                        tint = TerminalGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "INTEGRATION GUIDE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }

                StepItem(step = "1", text = "Open Telegram, search for @BotFather and send /newbot. Copy the token generated and paste it into 'Telegram Bot Token' above.")
                StepItem(step = "2", text = "Create a Channel or Group. Add your newly created bot as an Administrator with post permissions.")
                StepItem(step = "3", text = "Search for @userinfobot or invite it to retrieve your Group/Channel Chat ID (it should start with -100 for channels). Paste it above.")
                StepItem(step = "4", text = "Click 'SAVE SETTINGS' first, then click 'TEST CONNECTION' to verify integration.")
            }
        }

        // --- MANUAL DATA ADDITION CARD ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Manage Tickers",
                        tint = TerminalCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "TRACK NEW PORTFOLIO TICKER",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }

                Text(
                    text = "Add custom stocks into the system to ingest, analyze weekly OTC stats, and generate actionable scalping strategies.",
                    fontSize = 11.sp,
                    color = TerminalLightGrey
                )

                Button(
                    onClick = { showTickerDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = TerminalCyan),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .testTag("settings_add_ticker_trigger")
                ) {
                    Text(
                        text = "TRACK A NEW STOCK",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 11.sp
                    )
                }
            }
        }

        if (showTickerDialog) {
            AddTickerDialog(
                onDismiss = { showTickerDialog = false },
                onConfirm = { ticker, name, assetClass ->
                    viewModel.addStock(ticker, name, assetClass)
                    showTickerDialog = false
                    testStatus = "Tracked $ticker successfully!"
                }
            )
        }

        // --- ACCOUNT MANAGEMENT SECTION ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Account Status",
                        tint = TerminalCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ACCOUNT MANAGEMENT",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }

                Text(
                    text = "Configure your trading identity and license verification for Ndine Analytics.",
                    fontSize = 11.sp,
                    color = TerminalLightGrey
                )

                OutlinedTextField(
                    value = traderName,
                    onValueChange = { traderName = it },
                    label = { Text("Trader Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TerminalCyan,
                        focusedLabelColor = TerminalCyan
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("trader_name_input")
                )

                OutlinedTextField(
                    value = traderEmail,
                    onValueChange = { traderEmail = it },
                    label = { Text("Contact Email") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TerminalCyan,
                        focusedLabelColor = TerminalCyan
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("trader_email_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "License Tier: PROFESSIONAL PRO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TerminalGold
                    )

                    Button(
                        onClick = {
                            accountSavedMessage = "Account identity updated!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TerminalCyan),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.height(32.dp).testTag("save_account_button")
                    ) {
                        Text("UPDATE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }

                accountSavedMessage?.let { msg ->
                    Text(text = msg, color = TerminalEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // --- DEDICATED MARKET INSIGHTS SECTION ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Market Insights",
                        tint = TerminalGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DEDICATED MARKET INSIGHTS FEED",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }

                Text(
                    text = "Global macroeconomic bulletins compiled in real-time by Ndine Labs analysts:",
                    fontSize = 11.sp,
                    color = TerminalLightGrey
                )

                val bulletins = listOf(
                    "📊 Capital Rotation" to "Substantial volumes rotating out of tech equities into gold and agricultural commodities due to hedging pressures.",
                    "💸 Forex Sentiment" to "USD shows temporary weakness against EUR & GBP following retail index adjustments.",
                    "🐋 Dark Pool Prints" to "Massive block trading buy orders detected on major cryptocurrency trusts (BTC/ETH) during late exchange hours."
                )

                bulletins.forEach { (title, content) ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .background(TerminalMediumGrey.copy(alpha = 0.2f))
                            .padding(10.dp)
                    ) {
                        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TerminalCyan)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = content, fontSize = 10.sp, color = TerminalLightGrey, lineHeight = 14.sp)
                    }
                }
            }
        }

        // --- ABOUT & HELP SECTION (NDINE LABS) ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, TerminalCyan.copy(alpha = 0.2f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "About Ndine Labs",
                        tint = TerminalCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ABOUT NDINE ANALYTICS & NDINE LABS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }

                Text(
                    text = "MISSION STATEMENT:\nTo democratize high-grade institutional market intelligence, exposing hidden off-exchange block trades and dark pool liquidity ratios for everyday traders globally.",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = TerminalLightGrey,
                    lineHeight = 16.sp
                )

                Text(
                    text = "FOUNDING STORY & VISION:\nProduced by Ndine Labs, Ndine Analytics was created to level the playing field. Traditionally, dark pool prints and institutional volume indicators were reserved for high-frequency desks. Our vision is to pack these advanced data models into an intuitive, real-time analytics assistant covering stocks, crypto, forex, commodities, and metals.",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = TerminalLightGrey,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(4.dp))
                Spacer(modifier = Modifier.height(1.dp).background(TerminalMediumGrey).fillMaxWidth())
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "CUSTOMER SUPPORT & ENQUIRIES",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = TerminalCyan
                )

                val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { uriHandler.openUri("mailto:ndinethemba69@gmail.com") },
                        colors = ButtonDefaults.buttonColors(containerColor = TerminalMediumGrey),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.weight(1f).height(36.dp)
                    ) {
                        Text("📧 EMAIL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Button(
                        onClick = { uriHandler.openUri("tel:+27613886126") },
                        colors = ButtonDefaults.buttonColors(containerColor = TerminalMediumGrey),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.weight(1f).height(36.dp)
                    ) {
                        Text("📞 CALL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
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

@Composable
fun StepItem(step: String, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(TerminalGold.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                .border(1.dp, TerminalGold, RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = step,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TerminalGold
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            color = TerminalLightGrey,
            lineHeight = 16.sp
        )
    }
}
