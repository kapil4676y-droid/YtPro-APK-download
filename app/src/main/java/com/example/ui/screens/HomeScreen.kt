package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.data.model.ShortVideo
import com.example.data.model.Video
import com.example.data.model.WatchHistoryItem
import com.example.ui.components.CategoryChips
import com.example.ui.components.FormatUtils
import com.example.ui.components.VideoCard
import com.example.ui.theme.YtProCyan
import com.example.ui.theme.YtProIndigo
import com.example.ui.theme.YtProRose
import com.example.ui.viewmodel.YtProViewModel

@Composable
fun HomeScreen(
    viewModel: YtProViewModel,
    onNavigateToShorts: () -> Unit,
    onNavigateToChannel: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val feedVideos by viewModel.feedVideos.collectAsState()
    val featuredVideos by viewModel.featuredVideos.collectAsState()
    val trendingVideos by viewModel.trendingVideos.collectAsState()
    val shorts by viewModel.allShorts.collectAsState()
    val watchHistory by viewModel.watchHistory.collectAsState()

    val featured = featuredVideos.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_feed_list"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Category Chips Filter
        item {
            CategoryChips(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { viewModel.selectedCategory.value = it },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Hero Featured Video (if "All" is selected)
        if (selectedCategory == "All" && featured != null) {
            item {
                FeaturedHeroBanner(
                    video = featured,
                    onPlay = { viewModel.playVideo(featured) },
                    onChannelClick = { onNavigateToChannel(featured.creatorId) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        // Continue Watching Tray (if user has watch history)
        if (watchHistory.isNotEmpty()) {
            item {
                ContinueWatchingTray(
                    historyItems = watchHistory.take(4),
                    allVideos = feedVideos,
                    onPlay = { vid -> viewModel.playVideo(vid) }
                )
            }
        }

        // Shorts Preview Carousel (YT Pro Shorts)
        if (shorts.isNotEmpty()) {
            item {
                ShortsShelf(
                    shorts = shorts,
                    onShortClick = { onNavigateToShorts() }
                )
            }
        }

        // Feed Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = YtProCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (selectedCategory == "All") "Personalized For You" else "$selectedCategory Videos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Video Feed Cards
        items(feedVideos) { video ->
            VideoCard(
                video = video,
                onClick = { viewModel.playVideo(video) },
                onChannelClick = { onNavigateToChannel(video.creatorId) },
                onSaveToPlaylist = { viewModel.showPlaylistSelector.value = video.id },
                onShare = { /* Trigger system share or toast */ },
                onReport = { viewModel.openReportDialog("VIDEO", video.id, video.title) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun FeaturedHeroBanner(
    video: Video,
    onPlay: () -> Unit,
    onChannelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onPlay() }
            .testTag("featured_hero_banner"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF272727)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x26FFFFFF))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
        ) {
            AsyncImage(
                model = video.thumbnailUrl,
                contentDescription = video.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Frosted Glass Scrim
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0x990F0F0F),
                                Color(0xF20F0F0F)
                            ),
                            startY = 50f
                        )
                    )
            )

            // Play Icon Glass Badge Overlay
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .background(Color(0x33A084E8))
                    .border(1.dp, Color(0x4DFFFFFF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }

            // Content Overlay
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFA084E8)
                    ) {
                        Text(
                            text = "FEATURED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            letterSpacing = 0.5.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0x99000000),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0x33FFFFFF))
                    ) {
                        Text(
                            text = "4K ULTRA HD",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC4B5FD),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF1F1F1),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${video.creatorName} • ${FormatUtils.formatViews(video.views)} views",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFAAAAAA)
                    )

                    Button(
                        onClick = onPlay,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA084E8)),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Watch", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ContinueWatchingTray(
    historyItems: List<WatchHistoryItem>,
    allVideos: List<Video>,
    onPlay: (Video) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = "Continue Watching",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF1F1F1),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(historyItems) { history ->
                val video = allVideos.find { it.id == history.videoId }
                if (video != null) {
                    val progress = (history.lastPositionSeconds.toFloat() / history.totalDurationSeconds.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)

                    Card(
                        modifier = Modifier
                            .width(180.dp)
                            .clickable { onPlay(video) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF272727)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x1FFFFFFF))
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                            ) {
                                AsyncImage(
                                    model = video.thumbnailUrl,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .align(Alignment.BottomCenter),
                                    color = Color(0xFFA084E8),
                                    trackColor = Color.Black.copy(alpha = 0.5f)
                                )
                            }
                            Text(
                                text = video.title,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFF1F1F1),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShortsShelf(
    shorts: List<ShortVideo>,
    onShortClick: (ShortVideo) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.TrendingUp,
                contentDescription = null,
                tint = Color(0xFFA084E8),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "YT Pro Shorts",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF1F1F1)
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            items(shorts) { short ->
                Card(
                    modifier = Modifier
                        .width(136.dp)
                        .aspectRatio(9f / 16f)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onShortClick(short) },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF272727)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x26FFFFFF))
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
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)),
                                        startY = 100f
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Text(
                                text = short.title,
                                color = Color(0xFFF1F1F1),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${FormatUtils.formatViews(short.views)} views",
                                color = Color(0xFFAAAAAA),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
