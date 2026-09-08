package com.example.feature.creator.revenue

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.ArrowOutward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.navigation.Screen
import com.example.data.repository.CreatorRepository
import com.example.ui.theme.*

@Composable
fun RevenueScreen(
    onNavigate: (Screen) -> Unit
) {
    val transactions by CreatorRepository.transactions.collectAsState()

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
                        onClick = { /* Simulated payout */ },
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

        Text("All Transactions", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(transactions) { tx ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
}
