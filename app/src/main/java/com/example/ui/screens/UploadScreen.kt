package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Video
import com.example.data.model.VideoVisibility
import com.example.ui.theme.YtProCyan
import com.example.ui.theme.YtProEmerald
import com.example.ui.theme.YtProIndigo
import com.example.ui.theme.YtProRose
import com.example.ui.viewmodel.YtProViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun UploadScreen(
    viewModel: YtProViewModel,
    onUploadCompleted: (Video) -> Unit,
    modifier: Modifier = Modifier
) {
    var uploadTypeTab by remember { mutableIntStateOf(0) } // 0 = Video, 1 = Short
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Technology") }
    var tagsText by remember { mutableStateOf("4k, tutorial, creator, ytpro") }
    var visibility by remember { mutableStateOf(VideoVisibility.PUBLIC) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var soundTitle by remember { mutableStateOf("Original Sound - Studio Master") }

    // Gallery video picker state
    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }
    var selectedVideoName by remember { mutableStateOf<String?>(null) }

    // Gallery thumbnail picker state
    var customThumbnailUri by remember { mutableStateOf<Uri?>(null) }

    val isUploading by viewModel.isUploading.collectAsState()
    val uploadProgress by viewModel.uploadProgress.collectAsState()
    val uploadStage by viewModel.uploadStage.collectAsState()

    // Activity Result Launcher for selecting Video from Device Gallery
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedVideoUri = uri
            val fileName = uri.lastPathSegment?.substringAfterLast("/") ?: "gallery_video_${System.currentTimeMillis()}.mp4"
            selectedVideoName = fileName
            if (title.isBlank()) {
                title = fileName.substringBeforeLast(".").replace("_", " ").replace("-", " ")
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }
        }
    }

    // Activity Result Launcher for selecting custom Thumbnail from Device Gallery
    val thumbnailPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            customThumbnailUri = uri
        }
    }

    // Preset thumbnails for instant selection
    val presetThumbnails = listOf(
        "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1518770660439-4636190af475?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1507238691740-187a5b1d37b8?w=800&auto=format&fit=crop&q=80"
    )
    var selectedPresetThumbnail by remember { mutableStateOf(presetThumbnails[0]) }

    val finalThumbnailUrl = customThumbnailUri?.toString() ?: selectedPresetThumbnail

    val categories = listOf(
        "Technology", "Coding & AI", "Gaming", "Cinematography",
        "Music & Beats", "Science & Space", "UI/UX Design", "Fitness & Health"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("upload_screen")
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(Color(0xFFA084E8), Color(0xFF6F1E51)))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
            Column {
                Text(
                    text = "Upload to YouTube Pro",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Upload from gallery, auto-transcode & publish",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Upload Format Tab (Video vs Short)
        TabRow(
            selectedTabIndex = uploadTypeTab,
            containerColor = Color(0xFF1A1A1A),
            contentColor = Color(0xFFA084E8),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = uploadTypeTab == 0,
                onClick = { uploadTypeTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Movie, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Video (16:9)", fontWeight = FontWeight.Bold)
                    }
                }
            )
            Tab(
                selected = uploadTypeTab == 1,
                onClick = { uploadTypeTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SlowMotionVideo, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Short (9:16)", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 1. GALLERY VIDEO SELECTION CARD (Primary Focus)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = if (selectedVideoUri != null) 2.dp else 1.dp,
                    color = if (selectedVideoUri != null) YtProEmerald else Color(0x33FFFFFF),
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable { videoPickerLauncher.launch("video/*") },
            colors = CardDefaults.cardColors(
                containerColor = if (selectedVideoUri != null) Color(0xFF14241B) else Color(0xFF1C1C1C)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (selectedVideoUri == null) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0x26A084E8)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileUpload,
                            contentDescription = null,
                            tint = Color(0xFFA084E8),
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Choose Video from Gallery",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Tap to browse phone videos (.mp4, .mov, .mkv)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { videoPickerLauncher.launch("video/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA084E8)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.VideoLibrary, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Select from Gallery", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(YtProEmerald.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = YtProEmerald,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = YtProEmerald.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "GALLERY VIDEO LOADED",
                                        color = YtProEmerald,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = selectedVideoName ?: "Device Video File",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                maxLines = 1,
                                color = Color.White
                            )
                            Text(
                                text = "Ready for transcoding & 4K delivery",
                                fontSize = 11.sp,
                                color = Color.LightGray
                            )
                        }
                        OutlinedButton(
                            onClick = { videoPickerLauncher.launch("video/*") },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Change", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Live Upload Pipeline Progress Card (if uploading)
        AnimatedVisibility(visible = isUploading) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF231B38)),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CloudUpload, null, tint = Color(0xFFA084E8), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Transcoding & Publishing Pipeline",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Text(
                            text = "${(uploadProgress * 100).toInt()}%",
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFA084E8),
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { uploadProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFFA084E8),
                        trackColor = Color(0xFF333333)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = uploadStage,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // 2. VIDEO THUMBNAIL SELECTOR (Gallery + Presets)
        if (uploadTypeTab == 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Video Thumbnail",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                OutlinedButton(
                    onClick = { thumbnailPickerLauncher.launch("image/*") },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.AddPhotoAlternate, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Pick Image from Gallery", fontSize = 11.sp)
                }
            }
            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E1E1E))
                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(12.dp))
            ) {
                AsyncImage(
                    model = finalThumbnailUrl,
                    contentDescription = "Selected Thumbnail",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                    color = Color.Black.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (customThumbnailUri != null) "CUSTOM GALLERY THUMBNAIL" else "HD/4K READY",
                        fontSize = 10.sp,
                        color = if (customThumbnailUri != null) YtProEmerald else YtProCyan,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Or choose frame preset:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 6.dp)
            ) {
                items(presetThumbnails) { thumb ->
                    val isSelected = thumb == selectedPresetThumbnail && customThumbnailUri == null
                    Box(
                        modifier = Modifier
                            .width(76.dp)
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) Color(0xFFA084E8) else Color(0x33FFFFFF),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                customThumbnailUri = null
                                selectedPresetThumbnail = thumb
                            }
                    ) {
                        AsyncImage(
                            model = thumb,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        } else {
            // Shorts 9:16 Preview
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .aspectRatio(9f / 16f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E1E1E))
                        .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = finalThumbnailUrl,
                        contentDescription = "Short Cover",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Icon(
                        Icons.Default.SlowMotionVideo,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text("YouTube Shorts Format", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("9:16 full-screen loop stream with original audio.", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedButton(
                        onClick = { thumbnailPickerLauncher.launch("image/*") },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Custom Short Cover", fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. TITLE & DESCRIPTION
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(if (uploadTypeTab == 0) "Video Title (Required)" else "Short Title / Caption (Required)") },
            placeholder = { Text(if (uploadTypeTab == 0) "e.g. My Gallery Video • Vlog & Tech Insights" else "e.g. Crazy transition effect! #shorts #viral") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("upload_title_input"),
            shape = RoundedCornerShape(12.dp),
            maxLines = 2
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (uploadTypeTab == 0) {
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                placeholder = { Text("Tell viewers about your video, timestamps, and credits...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .testTag("upload_description_input"),
                shape = RoundedCornerShape(12.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Dropdown
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                selectedCategory = cat
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }
        } else {
            // Sound Title for Short
            OutlinedTextField(
                value = soundTitle,
                onValueChange = { soundTitle = it },
                label = { Text("Sound / Audio Track Name") },
                leadingIcon = { Icon(Icons.Default.MusicNote, null, tint = YtProRose) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tags Input
        OutlinedTextField(
            value = tagsText,
            onValueChange = { tagsText = it },
            label = { Text("Tags (comma separated)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Visibility Selector
        Text(
            text = "Visibility",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                VideoVisibility.PUBLIC to ("Public" to Icons.Default.Public),
                VideoVisibility.UNLISTED to ("Unlisted" to Icons.Default.Visibility),
                VideoVisibility.PRIVATE to ("Private" to Icons.Default.Lock)
            ).forEach { (vis, pair) ->
                val (label, icon) = pair
                val isSelected = visibility == vis
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) Color(0x26A084E8) else MaterialTheme.colorScheme.surfaceVariant,
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { visibility = vis }
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) Color(0xFFA084E8) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4. PUBLISH BUTTON
        Button(
            onClick = {
                val videoUriString = selectedVideoUri?.toString() ?: ""
                if (uploadTypeTab == 0) {
                    if (title.isNotBlank()) {
                        viewModel.startUploadPipeline(
                            title = title,
                            description = description.ifEmpty { "Uploaded from device gallery via YouTube Pro." },
                            category = selectedCategory,
                            tags = tagsText,
                            visibility = visibility,
                            thumbnailUrl = finalThumbnailUrl,
                            videoUrl = videoUriString,
                            onComplete = { video ->
                                onUploadCompleted(video)
                            }
                        )
                    }
                } else {
                    val shortTitle = title.ifBlank { "Short Video ${System.currentTimeMillis()}" }
                    viewModel.startShortUploadPipeline(
                        title = shortTitle,
                        videoUrl = videoUriString,
                        thumbnailUrl = finalThumbnailUrl,
                        soundTitle = soundTitle,
                        onComplete = {
                            // On short complete, return to home/shorts
                            val dummyVid = Video(
                                id = it.id,
                                title = it.title,
                                description = "Short uploaded from gallery",
                                videoUrl = it.videoUrl,
                                thumbnailUrl = it.thumbnailUrl,
                                durationSeconds = 45,
                                creatorId = it.creatorId,
                                creatorName = it.creatorName,
                                creatorAvatar = it.creatorAvatar
                            )
                            onUploadCompleted(dummyVid)
                        }
                    )
                }
            },
            enabled = title.isNotBlank() && !isUploading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("publish_video_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA084E8))
        ) {
            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isUploading) "Transcoding in Progress..." else if (uploadTypeTab == 0) "Transcode & Publish Video" else "Publish Short to YouTube Feed",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
