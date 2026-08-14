package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    VIEWER, CREATOR, ADMIN
}

enum class VideoVisibility {
    PUBLIC, UNLISTED, PRIVATE
}

enum class VideoResolution(val label: String, val badge: String) {
    RES_360P("360p", "SD"),
    RES_720P("720p", "HD"),
    RES_1080P("1080p", "FHD"),
    RES_4K("4K Ultra HD", "4K")
}

enum class ReportStatus {
    PENDING, RESOLVED, DISMISSED
}

enum class PayoutStatus {
    PENDING, APPROVED, REJECTED, PAID
}

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val username: String,
    val displayName: String,
    val email: String,
    val avatarUrl: String,
    val bannerUrl: String = "",
    val bio: String = "",
    val isVerified: Boolean = false,
    val isCreator: Boolean = false,
    val subscriberCount: Int = 0,
    val videoCount: Int = 0,
    val joinDate: String = "Jan 2024",
    val isMonetized: Boolean = false,
    val role: UserRole = UserRole.VIEWER,
    val isBanned: Boolean = false
)

@Entity(tableName = "videos")
data class Video(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val durationSeconds: Int,
    val creatorId: String,
    val creatorName: String,
    val creatorAvatar: String,
    val isVerifiedCreator: Boolean = true,
    val views: Long = 0,
    val likes: Long = 0,
    val dislikes: Long = 0,
    val category: String = "Technology",
    val tags: String = "tech, coding, ai",
    val visibility: VideoVisibility = VideoVisibility.PUBLIC,
    val availableResolutions: String = "360p, 720p, 1080p, 4K",
    val uploadTimestamp: Long = System.currentTimeMillis(),
    val isFeatured: Boolean = false,
    val isTrending: Boolean = false,
    val subtitles: String = "English (Auto-generated), Spanish, French",
    val audioTrackName: String = "Original Audio - Studio Master",
    val isFlagged: Boolean = false
)

@Entity(tableName = "shorts")
data class ShortVideo(
    @PrimaryKey val id: String,
    val title: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val creatorId: String,
    val creatorName: String,
    val creatorAvatar: String,
    val isVerifiedCreator: Boolean = true,
    val soundTitle: String = "Original Sound - YT Pro Beats",
    val soundAuthor: String = "YT Pro Audio Lab",
    val views: Long = 0,
    val likes: Long = 0,
    val commentsCount: Int = 0,
    val uploadTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey val id: String,
    val targetId: String, // Video ID or Short ID
    val isShort: Boolean = false,
    val userId: String,
    val username: String,
    val userAvatar: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val likes: Int = 0,
    val parentCommentId: String? = null,
    val repliesCount: Int = 0
)

@Entity(tableName = "likes")
data class LikeRecord(
    @PrimaryKey val id: String, // userId_targetId
    val userId: String,
    val targetId: String,
    val isShort: Boolean = false,
    val isLike: Boolean = true // true = like, false = dislike
)

@Entity(tableName = "subscriptions")
data class Subscription(
    @PrimaryKey val id: String, // userId_creatorId
    val subscriberUserId: String,
    val channelCreatorId: String,
    val subscribedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val description: String = "",
    val isPrivate: Boolean = false,
    val videoIds: String = "", // Comma-separated video IDs
    val thumbnailUrl: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "watch_history")
data class WatchHistoryItem(
    @PrimaryKey val id: String, // userId_videoId
    val userId: String,
    val videoId: String,
    val lastPositionSeconds: Int,
    val totalDurationSeconds: Int,
    val watchedAt: Long = System.currentTimeMillis(),
    val completed: Boolean = false
)

@Entity(tableName = "notifications")
data class NotificationItem(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: String, // UPLOAD, LIKE, COMMENT, PAYOUT, SYSTEM
    val targetId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "moderation_reports")
data class ModerationReport(
    @PrimaryKey val id: String,
    val reporterUserId: String,
    val targetType: String, // VIDEO, COMMENT, USER
    val targetId: String,
    val targetTitle: String,
    val reason: String,
    val details: String = "",
    val status: ReportStatus = ReportStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "creator_analytics")
data class CreatorAnalytics(
    @PrimaryKey val creatorId: String,
    val totalViews: Long,
    val watchTimeHours: Double,
    val monthlyRevenue: Double,
    val impressions: Long,
    val clickThroughRate: Double,
    val avgViewDurationSecs: Int,
    val topCountry: String
)

@Entity(tableName = "earnings")
data class EarningsRecord(
    @PrimaryKey val id: String,
    val creatorId: String,
    val monthYear: String,
    val viewsRevenue: Double,
    val subscriptionsRevenue: Double,
    val sponsorshipsRevenue: Double,
    val totalAmount: Double,
    val isPaidOut: Boolean = false
)

@Entity(tableName = "payout_requests")
data class PayoutRequest(
    @PrimaryKey val id: String,
    val creatorId: String,
    val creatorName: String,
    val amount: Double,
    val payoutMethod: String, // PayPal, Stripe, Bank Wire
    val destinationDetails: String,
    val status: PayoutStatus = PayoutStatus.PENDING,
    val requestedAt: Long = System.currentTimeMillis(),
    val processedAt: Long? = null
)

@Entity(tableName = "categories")
data class VideoCategory(
    @PrimaryKey val id: String,
    val name: String,
    val iconName: String,
    val videoCount: Int = 0
)
