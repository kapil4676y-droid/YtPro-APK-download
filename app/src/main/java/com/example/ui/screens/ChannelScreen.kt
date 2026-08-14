package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.User
import com.example.ui.components.ChannelAvatar
import com.example.ui.components.FormatUtils
import com.example.ui.components.VideoCard
import com.example.ui.theme.YtProAmber
import com.example.ui.theme.YtProCyan
import com.example.ui.theme.YtProIndigo
import com.example.ui.viewmodel.YtProViewModel

@Composable
fun ChannelScreen(
    channelId: String,
    viewModel: YtProViewModel,
    onNavigateToShorts: () -> Unit,
    modifier: Modifier = Modifier
) {
    val creatorUser by viewModel.getCreatorUser(channelId).collectAsState(initial = null)
    val videos by viewModel.getCreatorVideos(channelId).collectAsState(initial = emptyList())
    val shorts by viewModel.getCreatorShorts(channelId).collectAsState(initial = emptyList())
    val isSubscribedRecord by viewModel.getIsSubscribed(channelId).collectAsState(initial = null)

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Videos", "Shorts", "About")

    val user = creatorUser ?: User(
        id = channelId,
        username = "creator",
        displayName = "YT Pro Creator",
        email = "creator@ytpro.io",
        avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200&auto=format&fit=crop&q=80",
        bannerUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=1200&auto=format&fit=crop&q=80",
        bio = "Official Verified Creator on YT Pro.",
        isVerified = true,
        subscriberCount = 284000,
        videoCount = 64
    )

    val isSubscribed = isSubscribedRecord != null

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("channel_screen"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Channel Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (user.bannerUrl.isNotBlank()) {
                    AsyncImage(
                        model = user.bannerUrl,
                        contentDescription = "Channel Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(listOf(YtProIndigo, YtProCyan))
                            )
                    )
                }
            }
        }

        // Channel Header Info
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ChannelAvatar(
                        avatarUrl = user.avatarUrl,
                        isVerified = user.isVerified,
                        size = 72.dp
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = user.displayName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            if (user.isVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified",
                                    tint = YtProCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Text(
                            text = "@${user.username}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "${FormatUtils.formatViews(user.subscriberCount.toLong())} subscribers • ${user.videoCount} videos",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (user.bio.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = user.bio,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 3
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Subscribe / Follow Button
                Button(
                    onClick = { viewModel.toggleSubscription(user.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSubscribed) MaterialTheme.colorScheme.surfaceVariant else YtProIndigo
                    )
                ) {
                    Text(
                        text = if (isSubscribed) "Subscribed (Notifications On)" else "Subscribe",
                        color = if (isSubscribed) MaterialTheme.colorScheme.onSurface else Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Primary Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color(0xFF161616),
                contentColor = Color(0xFFA084E8),
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title, fontWeight = FontWeight.Bold) }
                    )
                }
            }
        }

        // Tab Content
        when (selectedTabIndex) {
            0 -> { // Videos Tab
                if (videos.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No published videos yet.")
                        }
                    }
                } else {
                    items(videos) { video ->
                        VideoCard(
                            video = video,
                            onClick = { viewModel.playVideo(video) },
                            onChannelClick = {},
                            onSaveToPlaylist = { viewModel.showPlaylistSelector.value = video.id },
                            onShare = {},
                            onReport = { viewModel.openReportDialog("VIDEO", video.id, video.title) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
            1 -> { // Shorts Tab
                if (shorts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No published shorts yet.")
                        }
                    }
                } else {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            shorts.forEach { short ->
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(9f / 16f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { onNavigateToShorts() },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        AsyncImage(
                                            model = short.thumbnailUrl,
                                            contentDescription = short.title,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    Brush.verticalGradient(
                                                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                                                        startY = 80f
                                                    )
                                                )
                                        )
                                        Text(
                                            text = short.title,
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .align(Alignment.BottomStart)
                                                .padding(6.dp),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            2 -> { // About Tab
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Channel Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Joined: ${user.joinDate}")
                                Text("Verified Creator Badge: ${if (user.isVerified) "Active 💎" else "Standard"}")
                                Text("Monetization Partner: ${if (user.isMonetized) "Enabled (Revenue Sharing)" else "In Review"}")
                                Text("Contact: ${user.email}")
                            }
                        }
                    }
                }
            }
        }
    }
}
