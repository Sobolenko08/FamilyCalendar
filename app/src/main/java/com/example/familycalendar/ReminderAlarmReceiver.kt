package com.example.familycalendar

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class ReminderAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(KEY_TITLE) ?: return
        val time = intent.getStringExtra(KEY_TIME) ?: ""
        val author = intent.getStringExtra(KEY_AUTHOR) ?: ""

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }

        createChannel(context)

        val bodyText = buildString {
            if (time.isNotBlank()) append("В $time")
            if (author.isNotBlank()) {
                if (isNotEmpty()) append(" • ")
                append(author)
            }
        }.ifBlank { "Семейное событие" }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(title)
            .setContentText(bodyText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notifId = (intent.getStringExtra(KEY_WORK_TAG) ?: (title + time + author)).hashCode()
        NotificationManagerCompat.from(context).notify(notifId, notification)
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Напоминания о событиях",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Уведомления о запланированных семейных событиях"
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "family_calendar_reminders"
        const val KEY_TITLE = "key_title"
        const val KEY_TIME = "key_time"
        const val KEY_AUTHOR = "key_author"
        const val KEY_WORK_TAG = "key_work_tag"

        fun scheduleReminder(
            context: Context,
            title: String,
            dateStr: String,
            timeStr: String,
            author: String,
            minutesBefore: Long = 60L
        ) {
            val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("ru"))
            val targetDate = try {
                formatter.parse("$dateStr $timeStr")
            } catch (_: Exception) {
                null
            } ?: return

            val triggerAtMillis = targetDate.time - TimeUnit.MINUTES.toMillis(minutesBefore)
            if (triggerAtMillis <= System.currentTimeMillis()) return

            val tag = "reminder_${title}_${dateStr}_$timeStr"
            val requestCode = tag.hashCode()

            val intent = Intent(context, ReminderAlarmReceiver::class.java).apply {
                putExtra(KEY_TITLE, title)
                putExtra(KEY_TIME, timeStr)
                putExtra(KEY_AUTHOR, author)
                putExtra(KEY_WORK_TAG, tag)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                // Фолбэк, если точные будильники запрещены системой
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
                return
            }

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }
}