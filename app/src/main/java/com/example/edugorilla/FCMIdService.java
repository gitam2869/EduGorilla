package com.example.edugorilla;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FCMIdService extends FirebaseMessagingService
{

    private static final String TAG = "FCMService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage)
    {
        super.onMessageReceived(remoteMessage);

        Log.d("msg", "onMessageReceived: " + remoteMessage.getFrom());

        String message = remoteMessage.getNotification().getBody();
        String title = remoteMessage.getNotification().getTitle();
        Log.d(TAG, "onMessageReceived: "+title);
        Log.d(TAG, "onMessageReceived: "+message);

        Intent intent = new Intent(this, MainActivity.class);

        intent.putExtra("fromNotification","yes");
        intent.putExtra("title", remoteMessage.getNotification().getTitle());
        intent.putExtra("message", remoteMessage.getNotification().getBody());

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String channelId = "Default";
        NotificationCompat.Builder builder = new  NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody()).setAutoCancel(true).setContentIntent(pendingIntent);;
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
            assert manager != null;
            manager.createNotificationChannel(channel);
        }
        manager.notify(0, builder.build());
    }

    @Override
    public void onNewToken(@NonNull String s)
    {
//        SharedPreferenceManager.getInstance(getApplicationContext())
//                .userFCMToken(s);

        Log.d(TAG, "onNewToken: "+s);
    }
}
