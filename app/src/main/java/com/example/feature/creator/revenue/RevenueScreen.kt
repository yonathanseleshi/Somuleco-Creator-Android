package com.example.feature.creator.revenue

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.ArrowOutward
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.navigation.Screen
import com.example.data.model.TransactionItem
import com.example.data.repository.CreatorRepository
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RevenueScreen(
    onNavigate: (Screen) -> Unit
) {
    val transactions by CreatorRepository.transactions.collectAsState()
    var selectedTransaction by remember { mutableStateOf<TransactionItem?>(null) }
    var withdrawalMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp)
            .testTag("screen_revenue"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text("Revenue & Payouts", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Complete breakdown of subscriptions, store sales, and fees", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }

        withdrawalMessage?.let { msg ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SomulecoGreenSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = SomulecoGreenText)
                    Text(msg, style = MaterialTheme.typography.bodySmall, color = SomulecoGreenText, modifier = Modifier.weight(1f))
                    TextButton(onClick = { withdrawalMessage = null }) {
                        Text("Dismiss", color = SomulecoGreenText)
                    }
                }
            }
        }

        // Balance Overview Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSubtle, BorderSubtle)))
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Available Payout Balance", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                        Text("$7,820.00", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = SomulecoGreenText)
                    }

                    Button(
                        onClick = { withdrawalMessage = "Payout of $7,820.00 initiated to your connected bank account." },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SomulecoGreenDark)
                    ) {
                        Icon(Icons.Outlined.ArrowOutward, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Withdraw", fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider(color = BorderSubtle, thickness = 0.8.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Pending Clearance", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text("$1,430.00", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    }
                    Column {
                        Text("Subscriptions (MRR)", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text("$8,190.00", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = SomulecoPurple)
                    }
                    Column {
                        Text("Product Sales", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text("$10,260.00", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = SomulecoBlue)
                    }
                }
            }
        }

        Text("All Transactions (${transactions.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(transactions) { tx ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedTransaction = tx },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSubtle, BorderSubtle)))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(tx.itemTitle, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text("+$${"%.2f".format(tx.netAmount)}", fontWeight = FontWeight.ExtraBold, color = SomulecoGreenText)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("From ${tx.customerName} • ${tx.type}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            Text("Gross: $${"%.2f".format(tx.grossAmount)} • Fee: -$${"%.2f".format(tx.platformFee)}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                    }
                }
            }
        }
    }

    // Transaction Detail Bottom Sheet
    selectedTransaction?.let { tx ->
        ModalBottomSheet(
            onDismissRequest = { selectedTransaction = null },
            containerColor = SurfaceWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Transaction Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("ID: ${tx.id} • ${tx.date}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    }
                    Surface(shape = RoundedCornerShape(6.dp), color = SomulecoGreenSurface) {
                        Text(
                            tx.status,
                            color = SomulecoGreenText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BackgroundLight)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Item Title", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                            Text(tx.itemTitle, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Customer", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                            Text(tx.customerName, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodySmall)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Revenue Type", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                            Text(tx.type, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodySmall)
                        }
                        HorizontalDivider(color = BorderSubtle)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Gross Amount", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                            Text("$${"%.2f".format(tx.grossAmount)}", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Somuleco Platform Fee (5%)", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                            Text("-$${"%.2f".format(tx.platformFee)}", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Net Creator Payout", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text("+$${"%.2f".format(tx.netAmount)}", fontWeight = FontWeight.ExtraBold, color = SomulecoGreenText, style = MaterialTheme.typography.titleSmall)
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Outlined.Shield, contentDescription = null, tint = SomulecoBlue, modifier = Modifier.size(18.dp))
                    Text("Verified by Digital Product Rights Registry", style = MaterialTheme.typography.labelSmall, color = SomulecoBlue, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = { selectedTransaction = null },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SomulecoBlue)
                ) {
                    Text("Done")
                }
            }
        }
    }
}
