package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.ClosedCaptionDisabled
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbDownAlt
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.ThumbUpAlt
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Comment
import com.example.data.model.Video
import com.example.data.model.VideoResolution
import com.example.ui.components.ChannelAvatar
import com.example.ui.components.FormatUtils
import com.example.ui.components.VideoCard
import com.example.ui.theme.YtProCyan
import com.example.ui.theme.YtProEmerald
import com.example.ui.theme.YtProIndigo
import com.example.ui.theme.YtProRose
import com.example.ui.viewmodel.YtProViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WatchScreen(
    video: Video,
    viewModel: YtProViewModel,
    onNavigateToChannel: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPosition by viewModel.playbackPosition.collectAsState()
    val speed by viewModel.playbackSpeed.collectAsState()
    val resolution by viewModel.selectedResolution.collectAsState()
    val isCaptionsEnabled by viewModel.isCaptionsEnabled.collectAsState()
    val isFullscreen by viewModel.isFullscreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val comments by viewModel.getComments(video.id).collectAsState(initial = emptyList())
    val isLikedRecord by viewModel.getIsLiked(video.id).collectAsState(initial = null)
    val subscriptionRecord by viewModel.getIsSubscribed(video.creatorId).collectAsState(initial = null)
    val feedVideos by viewModel.feedVideos.collectAsState()

    var showControls by remember { mutableStateOf(true) }
    var isDescriptionExpanded by remember { mutableStateOf(false) }
    var showSpeedMenu by remember { mutableStateOf(false) }
    var showQualityMenu by remember { mutableStateOf(false) }
    var newCommentText by remember { mutableStateOf("") }
    var replyingToCommentId by remember { mutableStateOf<String?>(null) }
    var replyCommentText by remember { mutableStateOf("") }

    // Simulated media playback ticker
    LaunchedEffect(isPlaying, video.id) {
        while (isPlaying) {
            delay(1000)
            if (currentPosition < video.durationSeconds) {
                viewModel.seekTo(currentPosition + 1)
            } else {
                viewModel.seekTo(0)
            }
        }
    }

    // Auto hide controls after 4 seconds
    LaunchedEffect(showControls) {
        if (showControls) {
            delay(4000)
            showControls = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("watch_screen")
    ) {
        // VIDEO PLAYER STAGE
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (isFullscreen) Modifier.fillMaxSize()
                    else Modifier.aspectRatio(16f / 9f)
                )
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { showControls = !showControls },
                        onDoubleTap = { offset ->
                            if (offset.x < size.width / 2) {
                                // Double tap left: Rewind 10s
                                viewModel.seekTo((currentPosition - 10).coerceAtLeast(0))
                            } else {
                                // Double tap right: Fast forward 10s
                                viewModel.seekTo((currentPosition + 10).coerceAtMost(video.durationSeconds))
                            }
                        }
                    )
                }
        ) {
            // Ambient Backlight Glow
            AsyncImage(
                model = video.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(24.dp),
                contentScale = ContentScale.Crop,
                alpha = 0.35f
            )

            // Main Video Surface
            AsyncImage(
                model = video.thumbnailUrl,
                contentDescription = video.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )

            // Closed Captions Simulation
            if (isCaptionsEnabled && isPlaying) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = if (showControls) 56.dp else 16.dp, start = 20.dp, end = 20.dp),
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Black.copy(alpha = 0.8f)
                ) {
                    Text(
                        text = "Transcribing in real-time [YT Pro AI Engine] • Master stream running at ${speed}x in ${resolution.badge}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Overlay Controls
            androidx.compose.animation.AnimatedVisibility(
                visible = showControls,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                ) {
                    // Top Player Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .align(Alignment.TopCenter),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.minimizeToMiniPlayer() }) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Minimize",
                                tint = Color.White
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Captions toggle
                            IconButton(onClick = { viewModel.toggleCaptions() }) {
                                Icon(
                                    imageVector = if (isCaptionsEnabled) Icons.Default.ClosedCaption else Icons.Default.ClosedCaptionDisabled,
                                    contentDescription = "Captions",
                                    tint = if (isCaptionsEnabled) YtProCyan else Color.White
                                )
                            }

                            // Speed Selector
                            Box {
                                IconButton(onClick = { showSpeedMenu = true }) {
                                    Icon(Icons.Default.Speed, contentDescription = "Speed", tint = Color.White)
                                }
                                DropdownMenu(
                                    expanded = showSpeedMenu,
                                    onDismissRequest = { showSpeedMenu = false }
                                ) {
                                    listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { s ->
                                        DropdownMenuItem(
                                            text = { Text("${s}x ${if (s == 1.0f) "(Normal)" else ""}") },
                                            onClick = {
                                                viewModel.setSpeed(s)
                                                showSpeedMenu = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Quality Selector
                            Box {
                                IconButton(onClick = { showQualityMenu = true }) {
                                    Icon(Icons.Default.HighQuality, contentDescription = "Quality", tint = Color.White)
                                }
                                DropdownMenu(
                                    expanded = showQualityMenu,
                                    onDismissRequest = { showQualityMenu = false }
                                ) {
                                    VideoResolution.values().forEach { res ->
                                        DropdownMenuItem(
                                            text = { Text("${res.label} [${res.badge}]") },
                                            onClick = {
                                                viewModel.setResolution(res)
                                                showQualityMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Center Transport Controls
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalArrangement = Arrangement.spacedBy(32.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.seekTo((currentPosition - 10).coerceAtLeast(0)) },
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FastRewind,
                                contentDescription = "Rewind 10s",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Surface(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .clickable { viewModel.togglePlayPause() },
                            color = YtProIndigo.copy(alpha = 0.9f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlaying) "Pause" else "Play",
                                    tint = Color.White,
                                    modifier = Modifier.size(38.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.seekTo((currentPosition + 10).coerceAtMost(video.durationSeconds)) },
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FastForward,
                                contentDescription = "Fast Forward 10s",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    // Bottom Bar (Seekbar, Timers, Resolution & Fullscreen)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                            .align(Alignment.BottomCenter)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${FormatUtils.formatDuration(currentPosition)} / ${FormatUtils.formatDuration(video.durationSeconds)}",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = YtProIndigo.copy(alpha = 0.8f)
                                ) {
                                    Text(
                                        text = resolution.badge,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                IconButton(
                                    onClick = { viewModel.isFullscreen.value = !isFullscreen },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                        contentDescription = "Toggle Fullscreen",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        Slider(
                            value = currentPosition.toFloat(),
                            onValueChange = { viewModel.seekTo(it.toInt()) },
                            valueRange = 0f..video.durationSeconds.toFloat(),
                            colors = SliderDefaults.colors(
                                thumbColor = YtProRose,
                                activeTrackColor = YtProRose,
                                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }
            }
        }

        // SCROLLABLE CONTENT BELOW VIDEO
        if (!isFullscreen) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Title and Metadata
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = video.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "${FormatUtils.formatViews(video.views)} views",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = FormatUtils.formatTimestamp(video.uploadTimestamp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = video.category,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Creator Channel Bar
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onNavigateToChannel(video.creatorId) }
                        ) {
                            ChannelAvatar(
                                avatarUrl = video.creatorAvatar,
                                isVerified = video.isVerifiedCreator,
                                size = 42.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = video.creatorName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Creator Channel",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        val isSubscribed = subscriptionRecord != null
                        Button(
                            onClick = { viewModel.toggleSubscription(video.creatorId) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSubscribed) MaterialTheme.colorScheme.surfaceContainerHigh else YtProIndigo
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("subscribe_button")
                        ) {
                            Text(
                                text = if (isSubscribed) "Subscribed" else "Subscribe",
                                color = if (isSubscribed) MaterialTheme.colorScheme.onSurface else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Action Bar (Like, Dislike, Share, Save, Report)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Like / Dislike pill
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val isLiked = isLikedRecord?.isLike == true
                                val isDisliked = isLikedRecord?.isLike == false

                                Row(
                                    modifier = Modifier
                                        .clickable { viewModel.toggleLike(video.id, isShort = false, isLike = true) }
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isLiked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                                        contentDescription = "Like",
                                        tint = if (isLiked) YtProIndigo else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = FormatUtils.formatViews(video.likes),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(18.dp)
                                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                )

                                IconButton(
                                    onClick = { viewModel.toggleLike(video.id, isShort = false, isLike = false) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isDisliked) Icons.Filled.ThumbDown else Icons.Outlined.ThumbDown,
                                        contentDescription = "Dislike",
                                        tint = if (isDisliked) YtProRose else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        // Share
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { /* Trigger share */ }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Share", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        // Save to Playlist
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { viewModel.showPlaylistSelector.value = video.id }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.PlaylistAdd, contentDescription = "Save", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Save", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        // Report
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { viewModel.openReportDialog("VIDEO", video.id, video.title) }
                        ) {
                            Box(modifier = Modifier.padding(8.dp)) {
                                Icon(
                                    Icons.Default.Report,
                                    contentDescription = "Report",
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Expandable Description & Tags
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isDescriptionExpanded = !isDescriptionExpanded },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = video.description,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            if (isDescriptionExpanded) {
                                Spacer(modifier = Modifier.height(8.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    video.tags.split(",").forEach { tag ->
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer
                                        ) {
                                            Text(
                                                text = "#${tag.trim()}",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Audio: ${video.audioTrackName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = if (isDescriptionExpanded) "Show less" else "...more",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Comments Section Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Comments (${comments.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Add Comment Input
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ChannelAvatar(avatarUrl = currentUser.avatarUrl, size = 36.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = newCommentText,
                            onValueChange = { newCommentText = it },
                            placeholder = { Text("Add a comment...", fontSize = 13.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("comment_input_field"),
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                focusedBorderColor = YtProIndigo
                            ),
                            trailingIcon = {
                                if (newCommentText.isNotBlank()) {
                                    IconButton(onClick = {
                                        viewModel.addComment(video.id, newCommentText, isShort = false)
                                        newCommentText = ""
                                    }) {
                                        Icon(Icons.Default.Send, contentDescription = "Send", tint = YtProIndigo)
                                    }
                                }
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // List of Comments
                items(comments) { comment ->
                    CommentItemView(
                        comment = comment,
                        viewModel = viewModel,
                        isReplying = replyingToCommentId == comment.id,
                        replyText = replyCommentText,
                        onReplyTextChange = { replyCommentText = it },
                        onStartReply = {
                            replyingToCommentId = if (replyingToCommentId == comment.id) null else comment.id
                            replyCommentText = ""
                        },
                        onSubmitReply = {
                            viewModel.addComment(video.id, replyCommentText, isShort = false, parentCommentId = comment.id)
                            replyingToCommentId = null
                            replyCommentText = ""
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Up Next Recommendations Feed
                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    Text(
                        text = "Up Next",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                val upNextVideos = feedVideos.filter { it.id != video.id }
                items(upNextVideos) { upNext ->
                    VideoCard(
                        video = upNext,
                        onClick = { viewModel.playVideo(upNext) },
                        onChannelClick = { onNavigateToChannel(upNext.creatorId) },
                        onSaveToPlaylist = { viewModel.showPlaylistSelector.value = upNext.id },
                        onShare = {},
                        onReport = { viewModel.openReportDialog("VIDEO", upNext.id, upNext.title) },
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun CommentItemView(
    comment: Comment,
    viewModel: YtProViewModel,
    isReplying: Boolean,
    replyText: String,
    onReplyTextChange: (String) -> Unit,
    onStartReply: () -> Unit,
    onSubmitReply: () -> Unit
) {
    val replies by viewModel.getReplies(comment.id).collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            ChannelAvatar(avatarUrl = comment.userAvatar, size = 32.dp)
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = comment.username,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = FormatUtils.formatTimestamp(comment.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = comment.content,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { /* Like comment */ }
                    ) {
                        Icon(Icons.Outlined.ThumbUp, contentDescription = "Like", modifier = Modifier.size(14.dp))
                        if (comment.likes > 0) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = comment.likes.toString(), fontSize = 11.sp)
                        }
                    }

                    Text(
                        text = if (isReplying) "Cancel" else "Reply",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { onStartReply() }
                    )
                }
            }
        }

        // Reply input form
        if (isReplying) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 40.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = replyText,
                    onValueChange = onReplyTextChange,
                    placeholder = { Text("Write a reply...", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    trailingIcon = {
                        if (replyText.isNotBlank()) {
                            IconButton(onClick = onSubmitReply) {
                                Icon(Icons.Default.Send, contentDescription = "Send Reply", tint = YtProIndigo)
                            }
                        }
                    }
                )
            }
        }

        // Nested Replies list
        if (replies.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 40.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                replies.forEach { reply ->
                    Row(verticalAlignment = Alignment.Top) {
                        ChannelAvatar(avatarUrl = reply.userAvatar, size = 26.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(text = reply.username, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = FormatUtils.formatTimestamp(reply.timestamp),
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(text = reply.content, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
