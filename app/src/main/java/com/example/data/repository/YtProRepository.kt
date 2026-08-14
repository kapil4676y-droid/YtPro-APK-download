package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.Comment
import com.example.data.model.CreatorAnalytics
import com.example.data.model.EarningsRecord
import com.example.data.model.LikeRecord
import com.example.data.model.ModerationReport
import com.example.data.model.NotificationItem
import com.example.data.model.PayoutRequest
import com.example.data.model.PayoutStatus
import com.example.data.model.Playlist
import com.example.data.model.ReportStatus
import com.example.data.model.ShortVideo
import com.example.data.model.Subscription
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.model.Video
import com.example.data.model.VideoCategory
import com.example.data.model.VideoVisibility
import com.example.data.model.WatchHistoryItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class YtProRepository(private val db: AppDatabase) {

    private val _currentUser = MutableStateFlow(
        User(
            id = "user_me",
            username = "alex_creator",
            displayName = "Alex Rivera",
            email = "alex@ytpro.io",
            avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&auto=format&fit=crop&q=80",
            bannerUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=1200&auto=format&fit=crop&q=80",
            bio = "Cinematographer & Tech Visionary. Crafting next-gen digital experiences.",
            isVerified = true,
            isCreator = true,
            subscriberCount = 14200,
            videoCount = 28,
            joinDate = "Feb 2023",
            isMonetized = true,
            role = UserRole.CREATOR
        )
    )
    val currentUser = _currentUser.asStateFlow()

    fun switchUserRole(role: UserRole) {
        val updated = _currentUser.value.copy(
            role = role,
            isCreator = role != UserRole.VIEWER,
            isMonetized = role != UserRole.VIEWER
        )
        _currentUser.value = updated
        CoroutineScope(Dispatchers.IO).launch {
            db.userDao().insertUser(updated)
        }
    }

    fun loginWithGoogle(
        email: String,
        displayName: String,
        avatarUrl: String = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&auto=format&fit=crop&q=80",
        handle: String = ""
    ) {
        val username = handle.ifBlank { email.substringBefore("@").replace(".", "_") }
        val id = "user_google_" + email.replace("@", "_").replace(".", "_")
        val newUser = User(
            id = id,
            username = username,
            displayName = displayName.ifBlank { username.replaceFirstChar { it.uppercase() } },
            email = email,
            avatarUrl = avatarUrl.ifBlank { "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&auto=format&fit=crop&q=80" },
            bannerUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=1200&auto=format&fit=crop&q=80",
            bio = "YouTube Pro Creator & Explorer • Logged in via Google ($email)",
            isVerified = true,
            isCreator = true,
            subscriberCount = 1250,
            videoCount = 4,
            joinDate = "Aug 2026",
            isMonetized = true,
            role = UserRole.CREATOR
        )
        _currentUser.value = newUser
        CoroutineScope(Dispatchers.IO).launch {
            db.userDao().insertUser(newUser)
            db.notificationDao().insertNotification(
                NotificationItem(
                    id = UUID.randomUUID().toString(),
                    userId = newUser.id,
                    title = "Google Sign-In Successful",
                    message = "Welcome to YouTube Pro, ${newUser.displayName}! You are signed in with $email.",
                    type = "AUTH",
                    targetId = newUser.id
                )
            )
        }
    }

    fun switchAccount(user: User) {
        _currentUser.value = user
    }

    // Video streams
    val allVideos: Flow<List<Video>> = db.videoDao().getAllVideos()
    val trendingVideos: Flow<List<Video>> = db.videoDao().getTrendingVideos()
    val featuredVideos: Flow<List<Video>> = db.videoDao().getFeaturedVideos()
    val allShorts: Flow<List<ShortVideo>> = db.shortDao().getAllShorts()
    val allCategories: Flow<List<VideoCategory>> = db.categoryDao().getAllCategories()

    fun getVideosByCategory(category: String): Flow<List<Video>> =
        if (category == "All") db.videoDao().getAllVideos()
        else db.videoDao().getVideosByCategory(category)

    fun getVideoById(id: String): Flow<Video?> = db.videoDao().getVideoById(id)
    fun getVideosByCreator(creatorId: String): Flow<List<Video>> = db.videoDao().getVideosByCreator(creatorId)
    fun getShortsByCreator(creatorId: String): Flow<List<ShortVideo>> = db.shortDao().getShortsByCreator(creatorId)
    fun getUserById(id: String): Flow<User?> = db.userDao().getUserById(id)
    fun searchVideos(query: String): Flow<List<Video>> = db.videoDao().searchVideos(query)

    fun getComments(targetId: String): Flow<List<Comment>> = db.commentDao().getRootComments(targetId)
    fun getReplies(parentId: String): Flow<List<Comment>> = db.commentDao().getReplies(parentId)

    fun isLiked(targetId: String): Flow<LikeRecord?> = db.likeDao().getLike(_currentUser.value.id, targetId)
    fun isSubscribed(creatorId: String): Flow<Subscription?> = db.subscriptionDao().isSubscribed(_currentUser.value.id, creatorId)
    val userSubscriptions: Flow<List<Subscription>> = db.subscriptionDao().getSubscriptions(_currentUser.value.id)

    val userPlaylists: Flow<List<Playlist>> = db.playlistDao().getPlaylistsByUser(_currentUser.value.id)
    val userHistory: Flow<List<WatchHistoryItem>> = db.historyDao().getHistory(_currentUser.value.id)
    val notifications: Flow<List<NotificationItem>> = db.notificationDao().getNotifications(_currentUser.value.id)

    // Studio & Admin
    fun getCreatorAnalytics(creatorId: String): Flow<CreatorAnalytics?> = db.analyticsDao().getAnalytics(creatorId)
    fun getEarnings(creatorId: String): Flow<List<EarningsRecord>> = db.analyticsDao().getEarnings(creatorId)
    val allPayoutRequests: Flow<List<PayoutRequest>> = db.payoutDao().getAllPayoutRequests()
    fun getCreatorPayouts(creatorId: String): Flow<List<PayoutRequest>> = db.payoutDao().getPayoutsByCreator(creatorId)
    val allReports: Flow<List<ModerationReport>> = db.reportDao().getAllReports()
    val allUsers: Flow<List<User>> = db.userDao().getAllUsers()

    // Actions
    suspend fun recordWatchProgress(videoId: String, positionSecs: Int, totalSecs: Int) = withContext(Dispatchers.IO) {
        db.videoDao().incrementViews(videoId)
        val historyItem = WatchHistoryItem(
            id = "${_currentUser.value.id}_$videoId",
            userId = _currentUser.value.id,
            videoId = videoId,
            lastPositionSeconds = positionSecs,
            totalDurationSeconds = totalSecs,
            watchedAt = System.currentTimeMillis(),
            completed = positionSecs >= (totalSecs * 0.9)
        )
        db.historyDao().insertHistory(historyItem)
    }

    suspend fun toggleLike(targetId: String, isShort: Boolean, isLike: Boolean) = withContext(Dispatchers.IO) {
        val userId = _currentUser.value.id
        val existing = db.likeDao().getLikeDirect(userId, targetId)
        if (existing != null && existing.isLike == isLike) {
            db.likeDao().removeLike(userId, targetId)
            if (isShort) {
                db.shortDao().updateLikes(targetId, if (isLike) -1 else 0)
            } else {
                db.videoDao().updateLikes(targetId, if (isLike) -1 else 0)
            }
        } else {
            val delta = if (existing != null) 2L else 1L
            db.likeDao().insertLike(
                LikeRecord(
                    id = "${userId}_$targetId",
                    userId = userId,
                    targetId = targetId,
                    isShort = isShort,
                    isLike = isLike
                )
            )
            if (isShort) {
                db.shortDao().updateLikes(targetId, if (isLike) delta else -1)
            } else {
                db.videoDao().updateLikes(targetId, if (isLike) delta else -1)
            }
        }
    }

    suspend fun toggleSubscription(creatorId: String) = withContext(Dispatchers.IO) {
        val userId = _currentUser.value.id
        val existing = db.subscriptionDao().isSubscribedDirect(userId, creatorId)
        if (existing != null) {
            db.subscriptionDao().removeSubscription(userId, creatorId)
            db.userDao().updateSubscriberCount(creatorId, -1)
        } else {
            db.subscriptionDao().insertSubscription(
                Subscription(
                    id = "${userId}_$creatorId",
                    subscriberUserId = userId,
                    channelCreatorId = creatorId
                )
            )
            db.userDao().updateSubscriberCount(creatorId, 1)

            // Send notification to creator
            db.notificationDao().insertNotification(
                NotificationItem(
                    id = UUID.randomUUID().toString(),
                    userId = creatorId,
                    title = "New Subscriber!",
                    message = "${_currentUser.value.displayName} subscribed to your channel.",
                    type = "SUBSCRIBE",
                    targetId = userId
                )
            )
        }
    }

    suspend fun addComment(targetId: String, content: String, isShort: Boolean, parentCommentId: String? = null) = withContext(Dispatchers.IO) {
        val comment = Comment(
            id = UUID.randomUUID().toString(),
            targetId = targetId,
            isShort = isShort,
            userId = _currentUser.value.id,
            username = _currentUser.value.displayName,
            userAvatar = _currentUser.value.avatarUrl,
            content = content,
            parentCommentId = parentCommentId
        )
        db.commentDao().insertComment(comment)
    }

    suspend fun createPlaylist(title: String, description: String, isPrivate: Boolean, initialVideoId: String? = null) = withContext(Dispatchers.IO) {
        val playlist = Playlist(
            id = UUID.randomUUID().toString(),
            userId = _currentUser.value.id,
            title = title,
            description = description,
            isPrivate = isPrivate,
            videoIds = initialVideoId ?: "",
            thumbnailUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=600&auto=format&fit=crop&q=80"
        )
        db.playlistDao().insertPlaylist(playlist)
    }

    suspend fun addVideoToPlaylist(playlistId: String, videoId: String) = withContext(Dispatchers.IO) {
        val pl = db.playlistDao().getPlaylistById(playlistId).firstOrNull() ?: return@withContext
        val currentIds = if (pl.videoIds.isEmpty()) emptyList() else pl.videoIds.split(",")
        if (!currentIds.contains(videoId)) {
            val newIds = (currentIds + videoId).joinToString(",")
            db.playlistDao().updatePlaylist(pl.copy(videoIds = newIds, updatedAt = System.currentTimeMillis()))
        }
    }

    suspend fun uploadVideo(
        title: String,
        description: String,
        category: String,
        tags: String,
        visibility: VideoVisibility,
        thumbnailUrl: String,
        videoUrl: String = "",
        durationSecs: Int = 420
    ): Video = withContext(Dispatchers.IO) {
        val finalVideoUrl = videoUrl.ifBlank { "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4" }
        val video = Video(
            id = "vid_" + UUID.randomUUID().toString().take(8),
            title = title,
            description = description,
            videoUrl = finalVideoUrl,
            thumbnailUrl = thumbnailUrl.ifEmpty { "https://images.unsplash.com/photo-1579546929518-9e396f3cc809?w=600&auto=format&fit=crop&q=80" },
            durationSeconds = durationSecs,
            creatorId = _currentUser.value.id,
            creatorName = _currentUser.value.displayName,
            creatorAvatar = _currentUser.value.avatarUrl,
            isVerifiedCreator = _currentUser.value.isVerified,
            category = category,
            tags = tags,
            visibility = visibility,
            uploadTimestamp = System.currentTimeMillis()
        )
        db.videoDao().insertVideo(video)
        db.notificationDao().insertNotification(
            NotificationItem(
                id = UUID.randomUUID().toString(),
                userId = _currentUser.value.id,
                title = "Video Published!",
                message = "Your video '$title' has finished 4K transcoding and is now live.",
                type = "UPLOAD",
                targetId = video.id
            )
        )
        video
    }

    suspend fun uploadShort(
        title: String,
        videoUrl: String = "",
        thumbnailUrl: String = "",
        soundTitle: String = "Original Sound"
    ): ShortVideo = withContext(Dispatchers.IO) {
        val finalVideoUrl = videoUrl.ifBlank { "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4" }
        val short = ShortVideo(
            id = "short_" + UUID.randomUUID().toString().take(8),
            title = title,
            videoUrl = finalVideoUrl,
            thumbnailUrl = thumbnailUrl.ifEmpty { "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=600&auto=format&fit=crop&q=80" },
            creatorId = _currentUser.value.id,
            creatorName = _currentUser.value.displayName,
            creatorAvatar = _currentUser.value.avatarUrl,
            isVerifiedCreator = _currentUser.value.isVerified,
            soundTitle = soundTitle,
            soundAuthor = _currentUser.value.displayName,
            views = 0,
            likes = 0,
            commentsCount = 0,
            uploadTimestamp = System.currentTimeMillis()
        )
        db.shortDao().insertShort(short)
        db.notificationDao().insertNotification(
            NotificationItem(
                id = UUID.randomUUID().toString(),
                userId = _currentUser.value.id,
                title = "Short Published!",
                message = "Your Short '$title' is now live on the Shorts feed.",
                type = "UPLOAD",
                targetId = short.id
            )
        )
        short
    }

    suspend fun requestPayout(amount: Double, method: String, destination: String) = withContext(Dispatchers.IO) {
        val req = PayoutRequest(
            id = "payout_" + UUID.randomUUID().toString().take(6),
            creatorId = _currentUser.value.id,
            creatorName = _currentUser.value.displayName,
            amount = amount,
            payoutMethod = method,
            destinationDetails = destination,
            status = PayoutStatus.PENDING
        )
        db.payoutDao().insertPayoutRequest(req)
    }

    suspend fun submitReport(targetType: String, targetId: String, targetTitle: String, reason: String, details: String) = withContext(Dispatchers.IO) {
        val rep = ModerationReport(
            id = "rep_" + UUID.randomUUID().toString().take(6),
            reporterUserId = _currentUser.value.id,
            targetType = targetType,
            targetId = targetId,
            targetTitle = targetTitle,
            reason = reason,
            details = details,
            status = ReportStatus.PENDING
        )
        db.reportDao().insertReport(rep)
    }

    suspend fun adminUpdateReport(reportId: String, status: ReportStatus) = withContext(Dispatchers.IO) {
        db.reportDao().updateReportStatus(reportId, status)
    }

    suspend fun adminToggleBan(userId: String, banned: Boolean) = withContext(Dispatchers.IO) {
        db.userDao().setBanned(userId, banned)
    }

    suspend fun adminToggleFlagVideo(videoId: String, flagged: Boolean) = withContext(Dispatchers.IO) {
        db.videoDao().setFlagged(videoId, flagged)
    }

    suspend fun adminUpdatePayoutStatus(payoutId: String, status: PayoutStatus) = withContext(Dispatchers.IO) {
        db.payoutDao().updatePayoutStatus(payoutId, status)
    }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        db.historyDao().clearHistory(_currentUser.value.id)
    }

    suspend fun removeHistoryItem(videoId: String) = withContext(Dispatchers.IO) {
        db.historyDao().removeFromHistory(_currentUser.value.id, videoId)
    }

    suspend fun markNotificationsRead() = withContext(Dispatchers.IO) {
        db.notificationDao().markAllAsRead(_currentUser.value.id)
    }

    suspend fun seedInitialDataIfEmpty() = withContext(Dispatchers.IO) {
        val existingVideos = db.videoDao().getAllVideos().firstOrNull()
        if (existingVideos.isNullOrEmpty()) {
            // Seed Categories
            val categories = listOf(
                VideoCategory("cat_all", "All", "Explore"),
                VideoCategory("cat_tech", "Technology", "Computer"),
                VideoCategory("cat_code", "Coding & AI", "Code"),
                VideoCategory("cat_gaming", "Gaming", "SportsEsports"),
                VideoCategory("cat_cinema", "Cinematography", "MovieFilter"),
                VideoCategory("cat_music", "Music & Beats", "MusicNote"),
                VideoCategory("cat_science", "Science & Space", "RocketLaunch"),
                VideoCategory("cat_design", "UI/UX Design", "Brush"),
                VideoCategory("cat_fitness", "Fitness & Health", "FitnessCenter")
            )
            db.categoryDao().insertAll(categories)

            // Seed Users / Creators
            val creators = listOf(
                _currentUser.value,
                User(
                    id = "user_lumina",
                    username = "lumina_studios",
                    displayName = "Lumina Studios",
                    email = "contact@luminastudios.com",
                    avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200&auto=format&fit=crop&q=80",
                    bannerUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=1200&auto=format&fit=crop&q=80",
                    bio = "Next-gen Visual Effects, 3D Unreal Engine & Cyberpunk Cinematics.",
                    isVerified = true,
                    isCreator = true,
                    subscriberCount = 284000,
                    videoCount = 64,
                    joinDate = "Oct 2021",
                    isMonetized = true,
                    role = UserRole.CREATOR
                ),
                User(
                    id = "user_quantum",
                    username = "quantum_code",
                    displayName = "Quantum Coder",
                    email = "quantum@devworld.org",
                    avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200&auto=format&fit=crop&q=80",
                    bannerUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=1200&auto=format&fit=crop&q=80",
                    bio = "Deep dive into High Performance Kotlin, Neural Networks & System Architecture.",
                    isVerified = true,
                    isCreator = true,
                    subscriberCount = 492000,
                    videoCount = 112,
                    joinDate = "May 2020",
                    isMonetized = true,
                    role = UserRole.CREATOR
                ),
                User(
                    id = "user_soundwave",
                    username = "neon_beats",
                    displayName = "Neon Audio Lab",
                    email = "beats@neonaudio.io",
                    avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200&auto=format&fit=crop&q=80",
                    bannerUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=1200&auto=format&fit=crop&q=80",
                    bio = "Synthwave, Cyber Ambient & Studio Masters for Digital Creators.",
                    isVerified = true,
                    isCreator = true,
                    subscriberCount = 178000,
                    videoCount = 45,
                    joinDate = "Aug 2022",
                    isMonetized = true,
                    role = UserRole.CREATOR
                ),
                User(
                    id = "user_hypergear",
                    username = "hyper_gear",
                    displayName = "HyperGear Gaming",
                    email = "stream@hypergear.gg",
                    avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=200&auto=format&fit=crop&q=80",
                    bannerUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=1200&auto=format&fit=crop&q=80",
                    bio = "Pro Esports Tournament Highlights & Ray-Tracing Benchmark Showcases.",
                    isVerified = true,
                    isCreator = true,
                    subscriberCount = 620000,
                    videoCount = 230,
                    joinDate = "Jan 2019",
                    isMonetized = true,
                    role = UserRole.CREATOR
                )
            )
            db.userDao().insertAllUsers(creators)

            // Seed Videos
            val videos = listOf(
                Video(
                    id = "v_1",
                    title = "Building a Full-Scale Next-Gen AI Video Platform from Scratch in Kotlin & Jetpack Compose",
                    description = "In this comprehensive masterclass, we architect a production-ready video sharing platform named YT Pro. We cover multi-resolution adaptive streaming (4K, 1080p, 720p), Room local persistence, seamless shorts pager, creator studio analytics, ad-revenue monetization, and admin moderation suites.",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=800&auto=format&fit=crop&q=80",
                    durationSeconds = 1420,
                    creatorId = "user_quantum",
                    creatorName = "Quantum Coder",
                    creatorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200&auto=format&fit=crop&q=80",
                    isVerifiedCreator = true,
                    views = 124500,
                    likes = 8920,
                    dislikes = 45,
                    category = "Coding & AI",
                    tags = "kotlin, jetpack compose, android, architecture, ai",
                    isFeatured = true,
                    isTrending = true
                ),
                Video(
                    id = "v_2",
                    title = "Unreal Engine 5.5 Cinematic Showcase: Neon Cyberpunk Metropolis in 4K 60FPS",
                    description = "Experience hyper-realistic real-time ray-traced lumen lighting, nanite geometry, and volumetrics rendered inside Unreal Engine 5.5. Full breakdown of the rendering pipeline and custom shaders included.",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800&auto=format&fit=crop&q=80",
                    durationSeconds = 645,
                    creatorId = "user_lumina",
                    creatorName = "Lumina Studios",
                    creatorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200&auto=format&fit=crop&q=80",
                    isVerifiedCreator = true,
                    views = 348000,
                    likes = 24100,
                    dislikes = 110,
                    category = "Cinematography",
                    tags = "unrealengine, cyberpunk, vfx, 4k, cinematography",
                    isFeatured = true,
                    isTrending = true
                ),
                Video(
                    id = "v_3",
                    title = "Deep Space Odyssey: James Webb Telescope captures new Pillars of Creation",
                    description = "A deep astronomical analysis of the latest mid-infrared and near-infrared spectral imaging captured by JWST. We explore star formation, protostellar outflows, and dark matter gravitational lensing.",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=800&auto=format&fit=crop&q=80",
                    durationSeconds = 890,
                    creatorId = "user_me",
                    creatorName = "Alex Rivera",
                    creatorAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&auto=format&fit=crop&q=80",
                    isVerifiedCreator = true,
                    views = 89200,
                    likes = 6700,
                    dislikes = 23,
                    category = "Science & Space",
                    tags = "space, astronomy, jwst, astrophysics, universe",
                    isFeatured = true,
                    isTrending = false
                ),
                Video(
                    id = "v_4",
                    title = "Midnight Synthwave & Cyberpunk Ambient Beats [Live 24/7 Studio Stream]",
                    description = "Relax, code, or chill with ultra-high-fidelity retro synthwave and atmospheric cyberpunk beats produced exclusively at Neon Audio Lab.",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=800&auto=format&fit=crop&q=80",
                    durationSeconds = 3600,
                    creatorId = "user_soundwave",
                    creatorName = "Neon Audio Lab",
                    creatorAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200&auto=format&fit=crop&q=80",
                    isVerifiedCreator = true,
                    views = 512000,
                    likes = 41200,
                    dislikes = 130,
                    category = "Music & Beats",
                    tags = "synthwave, lofi, beats, ambient, coding music",
                    isFeatured = false,
                    isTrending = true
                ),
                Video(
                    id = "v_5",
                    title = "RTX 5090 Extreme Benchmark & 8K Ultra Ray-Tracing Performance Test",
                    description = "Pushing the silicon to the absolute limit. We test Cyberpunk 2077 Overdrive Path Tracing, Alan Wake 2, and Black Myth Wukong with full hardware sensor telemetry.",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=800&auto=format&fit=crop&q=80",
                    durationSeconds = 1120,
                    creatorId = "user_hypergear",
                    creatorName = "HyperGear Gaming",
                    creatorAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=200&auto=format&fit=crop&q=80",
                    isVerifiedCreator = true,
                    views = 420000,
                    likes = 31500,
                    dislikes = 180,
                    category = "Gaming",
                    tags = "gaming, rtx, benchmarks, 8k, hardware",
                    isFeatured = false,
                    isTrending = true
                ),
                Video(
                    id = "v_6",
                    title = "Modern Material You 3 Design Systems & Dynamic Color Alchemy",
                    description = "Explore advanced Jetpack Compose UI architecture, custom shader effects, glassmorphic surfaces, and fluid micro-interactions for modern Android.",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1507238691740-187a5b1d37b8?w=800&auto=format&fit=crop&q=80",
                    durationSeconds = 760,
                    creatorId = "user_quantum",
                    creatorName = "Quantum Coder",
                    creatorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200&auto=format&fit=crop&q=80",
                    isVerifiedCreator = true,
                    views = 78400,
                    likes = 5400,
                    dislikes = 19,
                    category = "UI/UX Design",
                    tags = "design, compose, material3, android, ui",
                    isFeatured = false,
                    isTrending = false
                )
            )
            db.videoDao().insertAllVideos(videos)

            // Seed Shorts
            val shorts = listOf(
                ShortVideo(
                    id = "s_1",
                    title = "3 Kotlin Clean Architecture tricks every senior dev uses 🔥",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=600&auto=format&fit=crop&q=80",
                    creatorId = "user_quantum",
                    creatorName = "Quantum Coder",
                    creatorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200&auto=format&fit=crop&q=80",
                    soundTitle = "Coding Vibes Chill Beat",
                    soundAuthor = "Neon Audio Lab",
                    views = 98000,
                    likes = 12400,
                    commentsCount = 248
                ),
                ShortVideo(
                    id = "s_2",
                    title = "Anamorphic Lens vs Spherical Lens in 30 Seconds 🎥",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1533750349088-cd871a92f312?w=600&auto=format&fit=crop&q=80",
                    creatorId = "user_lumina",
                    creatorName = "Lumina Studios",
                    creatorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200&auto=format&fit=crop&q=80",
                    soundTitle = "Cinema Euphoria Drop",
                    soundAuthor = "Lumina Audio",
                    views = 245000,
                    likes = 31200,
                    commentsCount = 412
                ),
                ShortVideo(
                    id = "s_3",
                    title = "The Quantum Computing Breakthrough you missed today ⚡",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1635070041078-e363dbe005cb?w=600&auto=format&fit=crop&q=80",
                    creatorId = "user_me",
                    creatorName = "Alex Rivera",
                    creatorAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&auto=format&fit=crop&q=80",
                    soundTitle = "Cosmic Synthesizer Wave",
                    soundAuthor = "CosmoSounds",
                    views = 156000,
                    likes = 18900,
                    commentsCount = 310
                ),
                ShortVideo(
                    id = "s_4",
                    title = "Smooth 120Hz Animation in Jetpack Compose in 5 lines 🚀",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=600&auto=format&fit=crop&q=80",
                    creatorId = "user_quantum",
                    creatorName = "Quantum Coder",
                    creatorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200&auto=format&fit=crop&q=80",
                    soundTitle = "Hyper Tech Pulse",
                    soundAuthor = "Neon Audio Lab",
                    views = 310000,
                    likes = 45000,
                    commentsCount = 580
                )
            )
            db.shortDao().insertAllShorts(shorts)

            // Seed Comments
            val comments = listOf(
                Comment(
                    id = "c_1",
                    targetId = "v_1",
                    userId = "user_lumina",
                    username = "Lumina Studios",
                    userAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200&auto=format&fit=crop&q=80",
                    content = "This is hands down the cleanest Jetpack Compose architecture tutorial on the web! The state management flow is pristine.",
                    likes = 142
                ),
                Comment(
                    id = "c_2",
                    targetId = "v_1",
                    userId = "user_soundwave",
                    username = "Neon Audio Lab",
                    userAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200&auto=format&fit=crop&q=80",
                    content = "Loving the custom audio sync and adaptive bitrate streaming logic. Inspiring stuff!",
                    likes = 89
                ),
                Comment(
                    id = "c_3",
                    targetId = "v_2",
                    userId = "user_me",
                    username = "Alex Rivera",
                    userAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&auto=format&fit=crop&q=80",
                    content = "The depth of field and volumetric fog in the alley shot is breathtaking. What camera focal length did you simulate?",
                    likes = 67
                )
            )
            db.commentDao().insertAllComments(comments)

            // Seed Playlists
            val playlists = listOf(
                Playlist(
                    id = "pl_1",
                    userId = _currentUser.value.id,
                    title = "Modern Android Masterclass",
                    description = "Essential engineering and Jetpack Compose best practices.",
                    isPrivate = false,
                    videoIds = "v_1,v_6",
                    thumbnailUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=600&auto=format&fit=crop&q=80"
                ),
                Playlist(
                    id = "pl_2",
                    userId = _currentUser.value.id,
                    title = "Cinematic Visuals & VFX",
                    description = "4K Unreal Engine showcases and space explorations.",
                    isPrivate = false,
                    videoIds = "v_2,v_3",
                    thumbnailUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&auto=format&fit=crop&q=80"
                )
            )
            db.playlistDao().insertAllPlaylists(playlists)

            // Seed Notifications
            val notifications = listOf(
                NotificationItem(
                    id = "notif_1",
                    userId = _currentUser.value.id,
                    title = "YT Pro Monetization Approved! 💎",
                    message = "Congratulations! Your channel has exceeded 1,000 subscribers and 4,000 watch hours. Ad revenue sharing is now active.",
                    type = "PAYOUT"
                ),
                NotificationItem(
                    id = "notif_2",
                    userId = _currentUser.value.id,
                    title = "Lumina Studios published a new video",
                    message = "Check out: 'Unreal Engine 5.5 Cinematic Showcase in 4K 60FPS'",
                    type = "UPLOAD",
                    targetId = "v_2"
                ),
                NotificationItem(
                    id = "notif_3",
                    userId = _currentUser.value.id,
                    title = "New Comment on your Video",
                    message = "Quantum Coder replied: 'Phenomenal astrophotography analysis Alex!'",
                    type = "COMMENT",
                    targetId = "v_3"
                )
            )
            db.notificationDao().insertAllNotifications(notifications)

            // Seed Analytics
            val analytics = listOf(
                CreatorAnalytics(
                    creatorId = _currentUser.value.id,
                    totalViews = 184200,
                    watchTimeHours = 5620.4,
                    monthlyRevenue = 3842.50,
                    impressions = 940000,
                    clickThroughRate = 8.4,
                    avgViewDurationSecs = 410,
                    topCountry = "United States (42%)"
                ),
                CreatorAnalytics(
                    creatorId = "user_quantum",
                    totalViews = 890000,
                    watchTimeHours = 24100.0,
                    monthlyRevenue = 12450.00,
                    impressions = 3200000,
                    clickThroughRate = 9.8,
                    avgViewDurationSecs = 580,
                    topCountry = "Global"
                )
            )
            db.analyticsDao().insertAllAnalytics(analytics)

            // Seed Earnings Records
            val earnings = listOf(
                EarningsRecord(
                    id = "earn_1",
                    creatorId = _currentUser.value.id,
                    monthYear = "July 2026",
                    viewsRevenue = 2840.00,
                    subscriptionsRevenue = 650.00,
                    sponsorshipsRevenue = 352.50,
                    totalAmount = 3842.50,
                    isPaidOut = false
                ),
                EarningsRecord(
                    id = "earn_2",
                    creatorId = _currentUser.value.id,
                    monthYear = "June 2026",
                    viewsRevenue = 2410.00,
                    subscriptionsRevenue = 520.00,
                    sponsorshipsRevenue = 300.00,
                    totalAmount = 3230.00,
                    isPaidOut = true
                )
            )
            db.analyticsDao().insertEarnings(earnings)

            // Seed Moderation Reports
            val reports = listOf(
                ModerationReport(
                    id = "rep_101",
                    reporterUserId = "user_hypergear",
                    targetType = "VIDEO",
                    targetId = "v_5",
                    targetTitle = "RTX 5090 Extreme Benchmark",
                    reason = "Misleading benchmark tag representation",
                    details = "User reported FPS numbers seem synthetic without driver version disclosed.",
                    status = ReportStatus.PENDING
                ),
                ModerationReport(
                    id = "rep_102",
                    reporterUserId = "user_quantum",
                    targetType = "COMMENT",
                    targetId = "c_3",
                    targetTitle = "Comment on Unreal Engine Showcase",
                    reason = "Spam bot promo link",
                    details = "Automated promotional spam comment attempt.",
                    status = ReportStatus.RESOLVED
                )
            )
            db.reportDao().insertAllReports(reports)

            // Seed Payout Requests
            val payouts = listOf(
                PayoutRequest(
                    id = "pay_01",
                    creatorId = _currentUser.value.id,
                    creatorName = "Alex Rivera",
                    amount = 3230.00,
                    payoutMethod = "Stripe Connect",
                    destinationDetails = "acct_1NZ420... (US Bank)",
                    status = PayoutStatus.PAID,
                    requestedAt = System.currentTimeMillis() - 2592000000L,
                    processedAt = System.currentTimeMillis() - 2400000000L
                )
            )
            db.payoutDao().insertAllPayoutRequests(payouts)
        }
    }
}
