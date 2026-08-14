package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Comment
import com.example.data.model.CreatorAnalytics
import com.example.data.model.EarningsRecord
import com.example.data.model.LikeRecord
import com.example.data.model.ModerationReport
import com.example.data.model.NotificationItem
import com.example.data.model.PayoutRequest
import com.example.data.model.Playlist
import com.example.data.model.ShortVideo
import com.example.data.model.Subscription
import com.example.data.model.User
import com.example.data.model.Video
import com.example.data.model.VideoCategory
import com.example.data.model.WatchHistoryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {
    @Query("SELECT * FROM videos WHERE isFlagged = 0 ORDER BY uploadTimestamp DESC")
    fun getAllVideos(): Flow<List<Video>>

    @Query("SELECT * FROM videos WHERE isTrending = 1 AND isFlagged = 0 ORDER BY views DESC")
    fun getTrendingVideos(): Flow<List<Video>>

    @Query("SELECT * FROM videos WHERE isFeatured = 1 AND isFlagged = 0 LIMIT 5")
    fun getFeaturedVideos(): Flow<List<Video>>

    @Query("SELECT * FROM videos WHERE category = :category AND isFlagged = 0 ORDER BY uploadTimestamp DESC")
    fun getVideosByCategory(category: String): Flow<List<Video>>

    @Query("SELECT * FROM videos WHERE creatorId = :creatorId ORDER BY uploadTimestamp DESC")
    fun getVideosByCreator(creatorId: String): Flow<List<Video>>

    @Query("SELECT * FROM videos WHERE id = :id LIMIT 1")
    fun getVideoById(id: String): Flow<Video?>

    @Query("SELECT * FROM videos WHERE id = :id LIMIT 1")
    suspend fun getVideoDirect(id: String): Video?

    @Query("SELECT * FROM videos WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%'")
    fun searchVideos(query: String): Flow<List<Video>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: Video)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllVideos(videos: List<Video>)

    @Update
    suspend fun updateVideo(video: Video)

    @Query("UPDATE videos SET views = views + 1 WHERE id = :id")
    suspend fun incrementViews(id: String)

    @Query("UPDATE videos SET likes = likes + :delta WHERE id = :id")
    suspend fun updateLikes(id: String, delta: Long)

    @Query("DELETE FROM videos WHERE id = :id")
    suspend fun deleteVideo(id: String)

    @Query("UPDATE videos SET isFlagged = :flagged WHERE id = :id")
    suspend fun setFlagged(id: String, flagged: Boolean)
}

@Dao
interface ShortDao {
    @Query("SELECT * FROM shorts ORDER BY uploadTimestamp DESC")
    fun getAllShorts(): Flow<List<ShortVideo>>

    @Query("SELECT * FROM shorts WHERE creatorId = :creatorId ORDER BY uploadTimestamp DESC")
    fun getShortsByCreator(creatorId: String): Flow<List<ShortVideo>>

    @Query("SELECT * FROM shorts WHERE id = :id LIMIT 1")
    fun getShortById(id: String): Flow<ShortVideo?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShort(short: ShortVideo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllShorts(shorts: List<ShortVideo>)

    @Query("UPDATE shorts SET views = views + 1 WHERE id = :id")
    suspend fun incrementViews(id: String)

    @Query("UPDATE shorts SET likes = likes + :delta WHERE id = :id")
    suspend fun updateLikes(id: String, delta: Long)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY subscriberCount DESC")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: String): Flow<User?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserDirect(id: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllUsers(users: List<User>)

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE users SET subscriberCount = subscriberCount + :delta WHERE id = :id")
    suspend fun updateSubscriberCount(id: String, delta: Int)

    @Query("UPDATE users SET isBanned = :banned WHERE id = :id")
    suspend fun setBanned(id: String, banned: Boolean)
}

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE targetId = :targetId AND parentCommentId IS NULL ORDER BY timestamp DESC")
    fun getRootComments(targetId: String): Flow<List<Comment>>

    @Query("SELECT * FROM comments WHERE parentCommentId = :parentId ORDER BY timestamp ASC")
    fun getReplies(parentId: String): Flow<List<Comment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: Comment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllComments(comments: List<Comment>)

    @Query("UPDATE comments SET likes = likes + 1 WHERE id = :id")
    suspend fun likeComment(id: String)

    @Query("DELETE FROM comments WHERE id = :id")
    suspend fun deleteComment(id: String)
}

@Dao
interface LikeDao {
    @Query("SELECT * FROM likes WHERE userId = :userId AND targetId = :targetId LIMIT 1")
    fun getLike(userId: String, targetId: String): Flow<LikeRecord?>

    @Query("SELECT * FROM likes WHERE userId = :userId AND targetId = :targetId LIMIT 1")
    suspend fun getLikeDirect(userId: String, targetId: String): LikeRecord?

    @Query("SELECT * FROM likes WHERE userId = :userId AND isLike = 1")
    fun getLikedTargets(userId: String): Flow<List<LikeRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLike(like: LikeRecord)

    @Query("DELETE FROM likes WHERE userId = :userId AND targetId = :targetId")
    suspend fun removeLike(userId: String, targetId: String)
}

@Dao
interface SubscriptionDao {
    @Query("SELECT * FROM subscriptions WHERE subscriberUserId = :userId")
    fun getSubscriptions(userId: String): Flow<List<Subscription>>

    @Query("SELECT * FROM subscriptions WHERE subscriberUserId = :userId AND channelCreatorId = :creatorId LIMIT 1")
    fun isSubscribed(userId: String, creatorId: String): Flow<Subscription?>

    @Query("SELECT * FROM subscriptions WHERE subscriberUserId = :userId AND channelCreatorId = :creatorId LIMIT 1")
    suspend fun isSubscribedDirect(userId: String, creatorId: String): Subscription?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: Subscription)

    @Query("DELETE FROM subscriptions WHERE subscriberUserId = :userId AND channelCreatorId = :creatorId")
    suspend fun removeSubscription(userId: String, creatorId: String)
}

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists WHERE userId = :userId ORDER BY updatedAt DESC")
    fun getPlaylistsByUser(userId: String): Flow<List<Playlist>>

    @Query("SELECT * FROM playlists WHERE id = :id LIMIT 1")
    fun getPlaylistById(id: String): Flow<Playlist?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: Playlist)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPlaylists(playlists: List<Playlist>)

    @Update
    suspend fun updatePlaylist(playlist: Playlist)

    @Query("DELETE FROM playlists WHERE id = :id")
    suspend fun deletePlaylist(id: String)
}

@Dao
interface HistoryDao {
    @Query("SELECT * FROM watch_history WHERE userId = :userId ORDER BY watchedAt DESC")
    fun getHistory(userId: String): Flow<List<WatchHistoryItem>>

    @Query("SELECT * FROM watch_history WHERE userId = :userId AND videoId = :videoId LIMIT 1")
    suspend fun getHistoryItem(userId: String, videoId: String): WatchHistoryItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(item: WatchHistoryItem)

    @Query("DELETE FROM watch_history WHERE userId = :userId AND videoId = :videoId")
    suspend fun removeFromHistory(userId: String, videoId: String)

    @Query("DELETE FROM watch_history WHERE userId = :userId")
    suspend fun clearHistory(userId: String)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    fun getNotifications(userId: String): Flow<List<NotificationItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllNotifications(notifications: List<NotificationItem>)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllAsRead(userId: String)
}

@Dao
interface ReportDao {
    @Query("SELECT * FROM moderation_reports ORDER BY createdAt DESC")
    fun getAllReports(): Flow<List<ModerationReport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ModerationReport)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllReports(reports: List<ModerationReport>)

    @Query("UPDATE moderation_reports SET status = :status WHERE id = :id")
    suspend fun updateReportStatus(id: String, status: com.example.data.model.ReportStatus)
}

@Dao
interface AnalyticsDao {
    @Query("SELECT * FROM creator_analytics WHERE creatorId = :creatorId LIMIT 1")
    fun getAnalytics(creatorId: String): Flow<CreatorAnalytics?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalytics(analytics: CreatorAnalytics)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAnalytics(analytics: List<CreatorAnalytics>)

    @Query("SELECT * FROM earnings WHERE creatorId = :creatorId ORDER BY monthYear DESC")
    fun getEarnings(creatorId: String): Flow<List<EarningsRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEarnings(earnings: List<EarningsRecord>)
}

@Dao
interface PayoutDao {
    @Query("SELECT * FROM payout_requests ORDER BY requestedAt DESC")
    fun getAllPayoutRequests(): Flow<List<PayoutRequest>>

    @Query("SELECT * FROM payout_requests WHERE creatorId = :creatorId ORDER BY requestedAt DESC")
    fun getPayoutsByCreator(creatorId: String): Flow<List<PayoutRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayoutRequest(request: PayoutRequest)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPayoutRequests(requests: List<PayoutRequest>)

    @Query("UPDATE payout_requests SET status = :status, processedAt = :timestamp WHERE id = :id")
    suspend fun updatePayoutStatus(id: String, status: com.example.data.model.PayoutStatus, timestamp: Long = System.currentTimeMillis())
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<VideoCategory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<VideoCategory>)
}
