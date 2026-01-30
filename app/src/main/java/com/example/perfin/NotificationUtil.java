package com.example.perfin;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationUtil {

    private static final String CHANNEL_ID = "finance_alerts";

    // 🔔 Call ONCE (MainActivity)
    public static void init(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Finance Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Budget & savings alerts");

            NotificationManager manager =
                    context.getSystemService(NotificationManager.class);

            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    // 🚨 Budget exceeded
    public static void showBudgetExceeded(Context context) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_warning)
                        .setContentTitle("Budget Exceeded")
                        .setContentText("You have crossed your monthly budget.")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        NotificationManagerCompat.from(context)
                .notify(1001, builder.build());
    }

    // 🚨 Savings affected
    public static void showSavingsAlert(Context context) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_warning)
                        .setContentTitle("Savings Alert")
                        .setContentText("Your expenses are affecting your savings!")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        NotificationManagerCompat.from(context)
                .notify(1002, builder.build());
    }
}
