/*
 * Copyright © 2020 WireGuard LLC. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.wireguard.android.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import androidx.annotation.RequiresApi
import com.wireguard.android.R
import com.wireguard.android.activity.MainActivity
import com.wireguard.android.activity.TunnelToggleActivity

class NotificationFactory(private val context: Context) {
    private val notificationManager = ContextWrapper(context).getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val foregroundNotificationChannelId = "WireGuard - Foreground"

    @RequiresApi(Build.VERSION_CODES.O)
    fun createForegroundNotification() : Notification {
        if (!notificationManager.notificationChannels.map { c -> c.id }.contains(foregroundNotificationChannelId)) {
            notificationManager.createNotificationChannel(NotificationChannel(
                    foregroundNotificationChannelId, foregroundNotificationChannelId, NotificationManager.IMPORTANCE_LOW))
        }


        // TODO: How to make this work for Android TV
        val mainActivityIntent = Intent.makeMainActivity(ComponentName(context, MainActivity::class.java))
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val contentIntent = PendingIntent.getActivity(context, 0, mainActivityIntent, 0)

        val toggleTunnelIntent = Intent.makeMainActivity(ComponentName(context, TunnelToggleActivity::class.java))
        toggleTunnelIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or Intent.FLAG_ACTIVITY_NO_ANIMATION)
        val closeIntent = PendingIntent.getActivity(context, 0, toggleTunnelIntent, 0);

        return Notification.Builder(context, foregroundNotificationChannelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentText(context.getText(R.string.run_in_foreground_notification_text))
                .setContentIntent(contentIntent)
                .setActions(Notification.Action.Builder(
                        Icon.createWithResource(context, android.R.drawable.ic_menu_close_clear_cancel),
                        context.getText(R.string.run_in_foreground_notification_action_close),
                        closeIntent).build())
                .build()
    }
}