/*
 * Copyright 2020 Korti
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.korti.muffle.notification

import android.app.*
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import io.korti.muffle.MainActivity
import io.korti.muffle.R
import io.korti.muffle.database.entity.MufflePoint
import javax.inject.Inject

class NotificationManager @Inject constructor(val context: Context) {

    companion object {
        const val ACTIVE_MUFFLE_POINT_ID = 0
        const val ACTIVE_MUFFLE_POINT_CHANNEL_ID = "io.korti.muffle.active.muffle.point"
    }

    val pendingIntent: PendingIntent by lazy {
        val intent = Intent(context, MainActivity::class.java)
        val stackBuild = TaskStackBuilder.create(context).apply {
            addNextIntentWithParentStack(intent)
        }
        stackBuild.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(
                R.string.notification_channel_name,
                R.string.notification_channel_description,
                ACTIVE_MUFFLE_POINT_CHANNEL_ID,
                NotificationManager.IMPORTANCE_MIN
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(
        nameId: Int,
        descriptionId: Int,
        channelId: String,
        importance: Int
    ) {
        val name = context.getString(nameId)
        val description = context.getString(descriptionId)
        val channel = NotificationChannel(channelId, name, importance).apply {
            this.description = description
        }
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun showActivMufflePointNotification(mufflePoint: MufflePoint) {
        val notification = createNotification(mufflePoint)
        showNotification(ACTIVE_MUFFLE_POINT_ID, notification)
    }

    fun hideActiveMufflePointNotification() {
        hideNotification(ACTIVE_MUFFLE_POINT_ID)
    }

    private fun showNotification(id: Int, notification: Notification) {
        with(NotificationManagerCompat.from(context)) {
            notify(id, notification)
        }
    }

    private fun hideNotification(id: Int) {
        with(NotificationManagerCompat.from(context)) {
            cancel(id)
        }
    }

    private fun createNotification(mufflePoint: MufflePoint): Notification {
        return createNotification(ACTIVE_MUFFLE_POINT_CHANNEL_ID)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.notification_text, mufflePoint.name))
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MIN).build()
    }

    private fun createNotification(channelId: String): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_notification)
            .setContentTitle(context.getString(R.string.app_name))
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setContentIntent(pendingIntent)
    }

}