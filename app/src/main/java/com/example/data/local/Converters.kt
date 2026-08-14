package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.PayoutStatus
import com.example.data.model.ReportStatus
import com.example.data.model.UserRole
import com.example.data.model.VideoVisibility

class Converters {
    @TypeConverter
    fun fromUserRole(role: UserRole): String = role.name

    @TypeConverter
    fun toUserRole(value: String): UserRole = try {
        UserRole.valueOf(value)
    } catch (e: Exception) {
        UserRole.VIEWER
    }

    @TypeConverter
    fun fromVideoVisibility(visibility: VideoVisibility): String = visibility.name

    @TypeConverter
    fun toVideoVisibility(value: String): VideoVisibility = try {
        VideoVisibility.valueOf(value)
    } catch (e: Exception) {
        VideoVisibility.PUBLIC
    }

    @TypeConverter
    fun fromReportStatus(status: ReportStatus): String = status.name

    @TypeConverter
    fun toReportStatus(value: String): ReportStatus = try {
        ReportStatus.valueOf(value)
    } catch (e: Exception) {
        ReportStatus.PENDING
    }

    @TypeConverter
    fun fromPayoutStatus(status: PayoutStatus): String = status.name

    @TypeConverter
    fun toPayoutStatus(value: String): PayoutStatus = try {
        PayoutStatus.valueOf(value)
    } catch (e: Exception) {
        PayoutStatus.PENDING
    }
}
