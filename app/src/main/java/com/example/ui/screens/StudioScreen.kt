package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PayoutStatus
import com.example.ui.components.FormatUtils
import com.example.ui.components.MetricCard
import com.example.ui.theme.YtProAmber
import com.example.ui.theme.YtProCyan
import com.example.ui.theme.YtProEmerald
import com.example.ui.theme.YtProIndigo
import com.example.ui.theme.YtProRose
import com.example.ui.viewmodel.YtProViewModel

@Composable
fun StudioScreen(
    viewModel: YtProViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val analytics by viewModel.creatorAnalytics.collectAsState()
    val earnings by viewModel.creatorEarnings.collectAsState()
    val myVideos by viewModel.getCreatorVideos(currentUser.id).collectAsState(initial = emptyList())
    val myPayouts by viewModel.adminPayoutRequests.collectAsState() // filter by creator if needed

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Dashboard", "Analytics", "Monetization", "Content")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("creator_studio_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Creator Studio Pro",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Channel Performance & Revenue Hub",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = YtProEmerald.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, null, tint = YtProEmerald, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Partner Active",
                            color = YtProEmerald,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF161616),
                contentColor = Color(0xFFA084E8),
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { idx, title ->
                    Tab(
                        selected = selectedTab == idx,
                        onClick = { selectedTab = idx },
                        text = { Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                }
            }
        }

        when (selectedTab) {
            0 -> { // Dashboard
                item {
                    Text(
                        text = "Overview (Last 28 Days)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricCard(
                            title = "Total Views",
                            value = FormatUtils.formatViews(analytics?.totalViews ?: 184200),
                            subtitle = "+14.2% vs last month",
                            icon = Icons.Default.Visibility,
                            iconColor = YtProIndigo,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Watch Time",
                            value = "${String.format("%.0f", analytics?.watchTimeHours ?: 5620.0)}h",
                            subtitle = "+22.8% vs last month",
                            icon = Icons.Default.Schedule,
                            iconColor = YtProCyan,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricCard(
                            title = "Subscribers",
                            value = FormatUtils.formatViews(currentUser.subscriberCount.toLong()),
                            subtitle = "+1.4K this month",
                            icon = Icons.Default.Group,
                            iconColor = YtProRose,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Est. Revenue",
                            value = "$${String.format("%.2f", analytics?.monthlyRevenue ?: 3842.50)}",
                            subtitle = "RPM: $4.85",
                            icon = Icons.Default.AttachMoney,
                            iconColor = YtProEmerald,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Audience Retention & Demographics",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Top Region: ${analytics?.topCountry ?: "United States (42%)"}")
                            Text("Impressions: ${FormatUtils.formatViews(analytics?.impressions ?: 940000)}")
                            Text("Click-Through Rate (CTR): ${analytics?.clickThroughRate ?: 8.4}%")
                            Text("Avg. View Duration: ${FormatUtils.formatDuration(analytics?.avgViewDurationSecs ?: 410)}")
                        }
                    }
                }
            }

            1 -> { // Analytics
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Real-Time Telemetry & Traffic Sources", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("• YT Pro Home Recommendations: 58%")
                            Text("• Shorts Vertical Feed: 24%")
                            Text("• Direct Search & Creator Channels: 14%")
                            Text("• External Embeds & Playlists: 4%")
                        }
                    }
                }
            }

            2 -> { // Monetization
                item {
                    // Eligibility Progress Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Partner Program Status", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = YtProEmerald
                                ) {
                                    Text(
                                        text = "QUALIFIED & MONETIZED",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Subscriber Milestone (1,000 required)
                            val subProgress = (currentUser.subscriberCount / 1000f).coerceIn(0f, 1f)
                            Text("1. Subscribers Milestone (${currentUser.subscriberCount} / 1,000 required)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { subProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = YtProEmerald
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Watch Hours Milestone (4,000 required)
                            val watchHours = (analytics?.watchTimeHours ?: 5620.0).toFloat()
                            val hourProgress = (watchHours / 4000f).coerceIn(0f, 1f)
                            Text("2. Public Watch Hours (${String.format("%.0f", watchHours)} / 4,000 hrs required)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { hourProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = YtProEmerald
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { viewModel.showPayoutRequestDialog.value = true },
                                colors = ButtonDefaults.buttonColors(containerColor = YtProEmerald),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("request_payout_button")
                            ) {
                                Icon(Icons.Default.AccountBalanceWallet, null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Request Revenue Payout / Withdrawal", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                item {
                    Text("Monthly Revenue Reports", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                items(earnings) { record ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(record.monthYear, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(
                                    text = "$${String.format("%.2f", record.totalAmount)}",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = YtProEmerald,
                                    fontSize = 15.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("• Ad Streaming: $${String.format("%.2f", record.viewsRevenue)}", fontSize = 11.sp)
                                Text("• Subscriptions: $${String.format("%.2f", record.subscriptionsRevenue)}", fontSize = 11.sp)
                                Text("• Sponsorships: $${String.format("%.2f", record.sponsorshipsRevenue)}", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            3 -> { // Content Management
                item {
                    Text("Published Channel Content (${myVideos.size} Videos)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                items(myVideos) { v ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(v.title, fontWeight = FontWeight.SemiBold, maxLines = 1)
                                Text(
                                    text = "${FormatUtils.formatViews(v.views)} views • ${FormatUtils.formatViews(v.likes)} likes • Visibility: ${v.visibility.name}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
