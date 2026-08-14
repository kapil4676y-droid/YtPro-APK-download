package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NotificationItem
import com.example.ui.components.ChannelAvatar
import com.example.ui.components.FormatUtils
import com.example.ui.components.VideoCard
import com.example.ui.theme.YtProCyan
import com.example.ui.theme.YtProIndigo
import com.example.ui.theme.YtProRose
import com.example.ui.viewmodel.YtProViewModel

@Composable
fun SubscriptionsScreen(
    viewModel: YtProViewModel,
    onNavigateToChannel: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val subscriptions by viewModel.userSubscriptions.collectAsState()
    val allVideos by viewModel.feedVideos.collectAsState()
    val notifications by viewModel.notifications.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val unreadCount = notifications.count { !it.isRead }

    val subscribedCreatorIds = subscriptions.map { it.channelCreatorId }.toSet()
    val subscriptionVideos = allVideos.filter { it.creatorId in subscribedCreatorIds }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("subscriptions_screen"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Tab Selector (Subscribed Feed vs Notifications)
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF161616),
                contentColor = Color(0xFFA084E8),
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Subscriptions, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Latest Feed", fontWeight = FontWeight.Bold)
                        }
                    }
                )

                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            BadgedBox(
                                badge = {
                                    if (unreadCount > 0) {
                                        Badge(containerColor = YtProRose) { Text("$unreadCount") }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Notifications, null, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Notifications", fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }
        }

        if (selectedTab == 0) {
            // Subscribed Creators Horizontal Bar
            item {
                if (subscriptions.isNotEmpty()) {
                    Column(modifier = Modifier.padding(vertical = 12.dp)) {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(subscriptions) { sub ->
                                val sampleVideo = allVideos.find { it.creatorId == sub.channelCreatorId }
                                val creatorName = sampleVideo?.creatorName ?: "Creator"
                                val creatorAvatar = sampleVideo?.creatorAvatar ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200&auto=format&fit=crop&q=80"

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .width(64.dp)
                                        .clickable { onNavigateToChannel(sub.channelCreatorId) }
                                ) {
                                    ChannelAvatar(
                                        avatarUrl = creatorAvatar,
                                        isVerified = true,
                                        size = 50.dp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = creatorName,
                                        fontSize = 10.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Subscription Feed
            if (subscriptionVideos.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Subscribe to creators on YT Pro to see their newest releases directly here!",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(subscriptionVideos) { video ->
                    VideoCard(
                        video = video,
                        onClick = { viewModel.playVideo(video) },
                        onChannelClick = { onNavigateToChannel(video.creatorId) },
                        onSaveToPlaylist = { viewModel.showPlaylistSelector.value = video.id },
                        onShare = {},
                        onReport = { viewModel.openReportDialog("VIDEO", video.id, video.title) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        } else {
            // Notifications Tab
            items(notifications) { notif ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.markNotificationAsRead(notif.id) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = if (!notif.isRead) YtProIndigo else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = notif.title,
                            fontWeight = if (!notif.isRead) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = notif.message,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = FormatUtils.formatTimestamp(notif.timestamp),
                            fontSize = 10.sp,
                            color = YtProIndigo
                        )
                    }
                }
            }
        }
    }
}
