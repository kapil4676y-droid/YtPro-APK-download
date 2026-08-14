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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.data.model.ReportStatus
import com.example.ui.components.ChannelAvatar
import com.example.ui.components.FormatUtils
import com.example.ui.components.MetricCard
import com.example.ui.theme.YtProCyan
import com.example.ui.theme.YtProEmerald
import com.example.ui.theme.YtProIndigo
import com.example.ui.theme.YtProRose
import com.example.ui.viewmodel.YtProViewModel

@Composable
fun AdminScreen(
    viewModel: YtProViewModel,
    modifier: Modifier = Modifier
) {
    val users by viewModel.adminUsers.collectAsState()
    val reports by viewModel.adminReports.collectAsState()
    val payouts by viewModel.adminPayoutRequests.collectAsState()
    val feedVideos by viewModel.feedVideos.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Moderation", "Payouts", "Users", "Infrastructure")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("admin_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(YtProRose.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, null, tint = YtProRose)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Admin Moderation Control",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Platform Governance & Payout Approvals",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
            0 -> { // Moderation Queue
                item {
                    Text(
                        text = "Abuse & Copyright Reports Queue (${reports.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(reports) { rep ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (rep.status == ReportStatus.PENDING) YtProRose.copy(alpha = 0.2f) else YtProEmerald.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "${rep.targetType}: ${rep.status.name}",
                                        color = if (rep.status == ReportStatus.PENDING) YtProRose else YtProEmerald,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = FormatUtils.formatTimestamp(rep.createdAt),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Target: ${rep.targetTitle}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Reason: ${rep.reason}",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            if (rep.details.isNotBlank()) {
                                Text(text = "Details: ${rep.details}", fontSize = 12.sp)
                            }

                            if (rep.status == ReportStatus.PENDING) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedButton(
                                        onClick = { viewModel.adminDismissReport(rep.id) },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Dismiss", fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            viewModel.adminResolveReport(rep.id)
                                            if (rep.targetType == "VIDEO") {
                                                viewModel.adminToggleFlagVideo(rep.targetId, true)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = YtProRose),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Take Action & Resolve", fontSize = 12.sp, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> { // Payouts Approval
                item {
                    Text(
                        text = "Creator Payout Requests",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(payouts) { payout ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(payout.creatorName, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "$${String.format("%.2f", payout.amount)}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = YtProEmerald
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Method: ${payout.payoutMethod} • Destination: ${payout.destinationDetails}", fontSize = 12.sp)
                            Text("Status: ${payout.status.name}", fontSize = 11.sp, color = YtProIndigo)

                            if (payout.status == PayoutStatus.PENDING) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    OutlinedButton(
                                        onClick = { viewModel.adminRejectPayout(payout.id) },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Reject", color = MaterialTheme.colorScheme.error)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = { viewModel.adminApprovePayout(payout.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = YtProEmerald),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Approve Payout", color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> { // User Management
                item {
                    Text(
                        text = "Registered Platform Users (${users.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(users) { u ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ChannelAvatar(avatarUrl = u.avatarUrl, isVerified = u.isVerified, size = 40.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(u.displayName, fontWeight = FontWeight.Bold)
                                Text("@${u.username} • Role: ${u.role.name} • ${FormatUtils.formatViews(u.subscriberCount.toLong())} subs", fontSize = 11.sp)
                                if (u.isBanned) {
                                    Text("STATUS: BANNED / SUSPENDED", color = MaterialTheme.colorScheme.error, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Button(
                                onClick = { viewModel.adminToggleBanUser(u.id, !u.isBanned) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (u.isBanned) YtProEmerald else MaterialTheme.colorScheme.error
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(if (u.isBanned) "Unban" else "Ban", fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                }
            }

            3 -> { // Infrastructure Telemetry
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.HealthAndSafety, null, tint = YtProEmerald)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("YT Pro Global Cloud Cluster Health", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                            Text("• CDN Edge Points of Presence: 240+ Locations Active", fontSize = 13.sp)
                            Text("• Video Transcoding Cluster: 100% Operational (FFmpeg 7.0 AV1/H.264/H.265)", fontSize = 13.sp)
                            Text("• Global CDN Cache Hit Ratio: 98.6%", fontSize = 13.sp)
                            Text("• Adaptive Bitrate HLS/DASH Latency: 1.4s (Low Latency Mode)", fontSize = 13.sp)
                            Text("• Database Replication: Multi-Region Active-Active Room & SQLite Sync", fontSize = 13.sp)
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
