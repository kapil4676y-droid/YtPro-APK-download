package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ShortVideo
import com.example.data.model.Video
import com.example.data.model.VideoCategory
import com.example.ui.theme.YtProCyan
import com.example.ui.theme.YtProIndigo
import com.example.ui.theme.YtProRose

object FormatUtils {
    fun formatViews(views: Long): String {
        return when {
            views >= 1_000_000 -> String.format("%.1fM", views / 1_000_000.0)
            views >= 1_000 -> String.format("%.1fK", views / 1_000.0)
            else -> views.toString()
        }
    }

    fun formatDuration(seconds: Int): String {
        val m = seconds / 60
        val s = seconds % 60
        val h = m / 60
        return if (h > 0) {
            String.format("%d:%02d:%02d", h, m % 60, s)
        } else {
            String.format("%d:%02d", m, s)
        }
    }

    fun formatTimestamp(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = (now - timestamp).coerceAtLeast(0L)
        val mins = diff / (1000 * 60)
        val hours = diff / (1000 * 60 * 60)
        val days = diff / (1000 * 60 * 60 * 24)

        return when {
            days > 30 -> "${days / 30}mo ago"
            days > 0 -> "${days}d ago"
            hours > 0 -> "${hours}h ago"
            mins > 0 -> "${mins}m ago"
            else -> "Just now"
        }
    }
}

@Composable
fun YtProLogo(
    modifier: Modifier = Modifier,
    iconSize: Dp = 36.dp,
    showText: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Frosted Gradient Emblem (Lavender to Magenta)
        Box(
            modifier = Modifier
                .size(iconSize)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFA084E8), Color(0xFF6F1E51))
                    )
                )
                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "YP",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = (iconSize.value * 0.45f).sp,
                letterSpacing = (-1).sp
            )
        }

        if (showText) {
            Spacer(modifier = Modifier.width(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "YT",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    letterSpacing = (-0.2).sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "PRO",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    letterSpacing = 0.5.sp,
                    color = Color(0xFFA084E8)
                )
            }
        }
    }
}

@Composable
fun ChannelAvatar(
    avatarUrl: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    isVerified: Boolean = false
) {
    Box(modifier = modifier, contentAlignment = Alignment.BottomEnd) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = "Channel Avatar",
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .border(1.5.dp, Color(0x33FFFFFF), CircleShape),
            contentScale = ContentScale.Crop
        )
        if (isVerified) {
            Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = "Verified Creator",
                tint = Color(0xFFA084E8),
                modifier = Modifier
                    .size(size * 0.42f)
                    .background(Color(0xFF0F0F0F), CircleShape)
            )
        }
    }
}

@Composable
fun VideoCard(
    video: Video,
    onClick: () -> Unit,
    onChannelClick: (String) -> Unit,
    onSaveToPlaylist: () -> Unit,
    onShare: () -> Unit,
    onReport: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("video_card_${video.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            // Thumbnail container with frosted border and corners
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF272727))
                    .border(1.dp, Color(0x1FFFFFFF), RoundedCornerShape(20.dp))
            ) {
                AsyncImage(
                    model = video.thumbnailUrl,
                    contentDescription = video.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Frosted Gradient scrim at bottom
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                                startY = 120f
                            )
                        )
                )

                // Top badges (4K / Featured)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (video.isFeatured) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFA084E8).copy(alpha = 0.9f)
                        ) {
                            Text(
                                text = "FEATURED",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0x99000000),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0x33FFFFFF))
                    ) {
                        Text(
                            text = "4K HDR",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC4B5FD),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Frosted Duration badge bottom right
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp),
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xCC000000),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0x33FFFFFF))
                ) {
                    Text(
                        text = FormatUtils.formatDuration(video.durationSeconds),
                        color = Color(0xFFF1F1F1),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Metadata Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.Top
            ) {
                ChannelAvatar(
                    avatarUrl = video.creatorAvatar,
                    isVerified = video.isVerifiedCreator,
                    size = 40.dp,
                    modifier = Modifier.clickable { onChannelClick(video.creatorId) }
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = video.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = video.creatorName,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFAAAAAA),
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.clickable { onChannelClick(video.creatorId) }
                        )

                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFAAAAAA)
                        )

                        Text(
                            text = "${FormatUtils.formatViews(video.views)} views",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFAAAAAA)
                        )

                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFAAAAAA)
                        )

                        Text(
                            text = FormatUtils.formatTimestamp(video.uploadTimestamp),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFAAAAAA)
                        )
                    }
                }

                // 3-dot overflow
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = Color(0xFFAAAAAA),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Save to Playlist") },
                            leadingIcon = { Icon(Icons.Default.PlaylistAdd, null) },
                            onClick = {
                                showMenu = false
                                onSaveToPlaylist()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Share") },
                            leadingIcon = { Icon(Icons.Default.Share, null) },
                            onClick = {
                                showMenu = false
                                onShare()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Report Video") },
                            leadingIcon = { Icon(Icons.Default.Report, null, tint = MaterialTheme.colorScheme.error) },
                            onClick = {
                                showMenu = false
                                onReport()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun CategoryChips(
    categories: List<VideoCategory>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { cat ->
            val isSelected = cat.name == selectedCategory
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) Color(0xFFF1F1F1) else Color(0xFF272727),
                border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0x1FFFFFFF)) else null,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onCategorySelected(cat.name) }
                    .testTag("category_chip_${cat.name}")
            ) {
                Text(
                    text = cat.name,
                    color = if (isSelected) Color(0xFF0F0F0F) else Color(0xFFF1F1F1),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp)
                )
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color = Color(0xFFA084E8),
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0x4D272727)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x26FFFFFF))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFAAAAAA),
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconColor.copy(alpha = 0.15f))
                        .border(1.dp, iconColor.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF1F1F1)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFA084E8),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun MiniPlayerBar(
    video: Video,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onClose: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .clickable { onClick() }
            .testTag("mini_player_bar"),
        color = Color(0xF2161616),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x26FFFFFF)),
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mini Thumbnail with frosted outline
            Box(
                modifier = Modifier
                    .width(84.dp)
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black)
                    .border(1.dp, Color(0x26FFFFFF), RoundedCornerShape(8.dp))
            ) {
                AsyncImage(
                    model = video.thumbnailUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF1F1F1),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = video.creatorName,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFAAAAAA),
                    maxLines = 1
                )
            }

            IconButton(onClick = onPlayPause) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color(0xFFA084E8)
                )
            }

            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Miniplayer",
                    tint = Color(0xFFAAAAAA)
                )
            }
        }
    }
}
