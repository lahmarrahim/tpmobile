package com.example.monderniertp;

import static androidx.core.app.NotificationCompat.PRIORITY_MAX;
import static androidx.core.app.NotificationCompat.PRIORITY_MIN;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.monderniertp.MusicFiles;
import com.example.monderniertp.MainActivity;
import com.example.monderniertp.R;

import java.util.ArrayList;

public class MusicService extends Service {

    private MyReceiver playpause;
    private MyReceiver nextMusic;
    private MyReceiver prevMusic;
    private MyReceiver playNextMusic;
    private MyReceiver playPrevMusic;
    static private Notification notification;
    static private NotificationCompat.Builder notificationBuilder;
    private MediaPlayer mediaPlayer;
    String titre;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            destroyNotification();
            createNotification();
        }
    };
    BroadcastReceiver broadcastReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            destroyNotification();
            createNotification();
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        playpause = new MyReceiver();
        nextMusic = new MyReceiver();
        prevMusic = new MyReceiver();
        playNextMusic = new MyReceiver();
        playPrevMusic = new MyReceiver();
        registerReceiver(playpause , new IntentFilter("PlayPause"));
        registerReceiver(nextMusic , new IntentFilter("Next"));
        registerReceiver(prevMusic , new IntentFilter("Prev"));
        registerReceiver(playNextMusic , new IntentFilter("pNextMusic"));
        registerReceiver(playPrevMusic , new IntentFilter("pPrevMusic"));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver , new IntentFilter("pNextMusic"));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver1 , new IntentFilter("pPrevMusic"));
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        titre = intent.getStringExtra("titre");
        return createNotification();
    }

    public int createNotification(){
        Intent notificationIntent = new Intent(this , MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, notificationIntent , 0);

        PendingIntent pPPendingIntent = PendingIntent.getBroadcast(this , 0 , new Intent("PlayPause") , PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pPPendingIntent1 = PendingIntent.getBroadcast(this , 0 , new Intent("Next") , PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pPPendingIntent2 = PendingIntent.getBroadcast(this , 0 , new Intent("Prev") , PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(
                    "ChannelId",
                    "ForegroundService",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setLightColor(Color.BLUE);
            channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(channel);

            notificationBuilder = new NotificationCompat.Builder(
                    this, "ChannelId");
            notification = notificationBuilder.setOngoing(true)
                    .setContentTitle("Lecture en cours")
                    .setContentText(titre)
                    .setSmallIcon(R.drawable.icon)
                    .addAction(R.drawable.prev , "Prev" , pPPendingIntent2)
                    .addAction(R.drawable.pause , "Play/Pause" , pPPendingIntent)
                    .addAction(R.drawable.next , "Next" , pPPendingIntent1)
                    .setContentIntent(pendingIntent)
                    .setPriority(PRIORITY_MAX)
                    .build();

            startForeground(110, notification);
            return START_STICKY;
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(playpause);
        unregisterReceiver(nextMusic);
        unregisterReceiver(prevMusic);
        destroyNotification();
        super.onDestroy();
    }

    public void destroyNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(true);
        }
    }

    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }
    }

}
