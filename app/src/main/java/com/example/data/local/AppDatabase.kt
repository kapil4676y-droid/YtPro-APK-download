package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
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

@Database(
    entities = [
        User::class,
        Video::class,
        ShortVideo::class,
        Comment::class,
        LikeRecord::class,
        Subscription::class,
        Playlist::class,
        WatchHistoryItem::class,
        NotificationItem::class,
        ModerationReport::class,
        CreatorAnalytics::class,
        EarningsRecord::class,
        PayoutRequest::class,
        VideoCategory::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun videoDao(): VideoDao
    abstract fun shortDao(): ShortDao
    abstract fun userDao(): UserDao
    abstract fun commentDao(): CommentDao
    abstract fun likeDao(): LikeDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun historyDao(): HistoryDao
    abstract fun notificationDao(): NotificationDao
    abstract fun reportDao(): ReportDao
    abstract fun analyticsDao(): AnalyticsDao
    abstract fun payoutDao(): PayoutDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ytpro_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
