package com.example.monderniertp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private static final int REQUEST_CODE = 1;
    ArrayList<MusicFiles> musicFiles;

    ImageView playpause, next, prev, addToFav;
    TextView titre;
    Button mesFavoris;

    MediaPlayer mediaPlayer;
    int position;

    MusicService musicService;

    MusicService.MyReceiver nextMusic;
    MusicService.MyReceiver prevMusic;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            playPauseMeth();
        }
    };
    BroadcastReceiver broadcastReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            playNextMeth();
        }
    };
    BroadcastReceiver broadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            playPrevMeth();
        }
    };

    ArrayList<MusicFiles> favoris;

    SensorManager sensorManager;
    Sensor sensor;
    int cpt = 1;

    @Override
    protected void onStart() {
        registerReceiver(nextMusic , new IntentFilter("NextMusic"));
        registerReceiver(prevMusic , new IntentFilter("PrevMusic"));
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(broadcastReceiver , new IntentFilter("PlayPause"));
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(broadcastReceiver1 , new IntentFilter("Next"));
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(broadcastReceiver2 , new IntentFilter("Prev"));
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permission();


        musicFiles = getAllAudio(this);

        playpause = findViewById(R.id.playpause);
        next = findViewById(R.id.next);
        prev = findViewById(R.id.prev);
        addToFav = findViewById(R.id.addToFav);
        titre = findViewById(R.id.titre);
        mesFavoris = findViewById(R.id.mesFavoris);

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(musicFiles.get(position).getPath());
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

        titre.setText(musicFiles.get(position).getTitle());

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        Intent i = new Intent(this, MusicService.class) ;
        i.putExtra("titre" , musicFiles.get(position).getTitle());
        startService(i);

        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor , SensorManager.SENSOR_DELAY_NORMAL);


        playpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPauseMeth();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playNextMeth();

            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPrevMeth();

            }
        });

        mesFavoris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent;
                intent = new Intent(getApplicationContext(), FavsActivity.class);
                intent.putExtra("favoris",favoris);
                startActivity(intent);

            }
        });



    }

    private void playPrevMeth() {
        if(mediaPlayer.isPlaying()){
            playpause.setImageResource(R.drawable.pause);
            mediaPlayer.stop();
            position = ((position - 1) < 0 ? ( musicFiles.size() -1 ) : (position-1) );
            Uri uri = Uri.parse(musicFiles.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            titre.setText(musicFiles.get(position).getTitle());
            mediaPlayer.start();
            Intent i = new Intent(this, MusicService.class) ;
            i.putExtra("titre" , musicFiles.get(position).getTitle());
            startService(i);
        }else{
            playpause.setImageResource(R.drawable.play);
            mediaPlayer.stop();
            position = ((position - 1) < 0 ? ( musicFiles.size() -1 ) : (position-1) );
            Uri uri = Uri.parse(musicFiles.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            titre.setText(musicFiles.get(position).getTitle());
            Intent i = new Intent(this, MusicService.class) ;
            i.putExtra("titre" , musicFiles.get(position).getTitle());
            startService(i);
        }
    }

    private void playNextMeth() {
        if(mediaPlayer.isPlaying()){
            playpause.setImageResource(R.drawable.pause);
            mediaPlayer.stop();
            position = ((position + 1) % musicFiles.size()) ;
            Uri uri = Uri.parse(musicFiles.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            titre.setText(musicFiles.get(position).getTitle());
            mediaPlayer.start();
            Intent i = new Intent(this, MusicService.class) ;
            i.putExtra("titre" , musicFiles.get(position).getTitle());
            startService(i);
        }else{
            playpause.setImageResource(R.drawable.play);
            mediaPlayer.stop();
            position = ((position + 1) % musicFiles.size()) ;
            Uri uri = Uri.parse(musicFiles.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            titre.setText(musicFiles.get(position).getTitle());
            Intent i = new Intent(this, MusicService.class) ;
            i.putExtra("titre" , musicFiles.get(position).getTitle());
            startService(i);
        }
    }

    private void playPauseMeth() {
        if(mediaPlayer.isPlaying()){
            playpause.setImageResource(R.drawable.play);
            mediaPlayer.pause();
            Intent i = new Intent(this, MusicService.class) ;
            i.putExtra("titre" , musicFiles.get(position).getTitle());
            startService(i);
        }else{
            playpause.setImageResource(R.drawable.pause);
            mediaPlayer.start();
            Intent i = new Intent(this, MusicService.class) ;
            i.putExtra("titre" , musicFiles.get(position).getTitle());
            startService(i);
        }
    }

    private void permission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
        else {
            Toast.makeText(this, "Permission Granted !!", Toast.LENGTH_SHORT).show();
            musicFiles = getAllAudio(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted !!", Toast.LENGTH_SHORT).show();
                musicFiles = getAllAudio(this);
            }
            else{
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }
    }

    public static ArrayList<MusicFiles> getAllAudio(Context context){

        ArrayList<MusicFiles> audioList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE
        };

        Cursor cursor = context.getContentResolver().query(uri, projection,null,null,null);
        if(cursor!= null){
            while (cursor.moveToNext()){
                String path = cursor.getString(0);
                String title = cursor.getString(1);

                MusicFiles musicFiles = new MusicFiles(path, title);
                Log.e("Path: " + path , " Title: " + title);
                audioList.add(musicFiles);
            }
            cursor.close();
        }
        return audioList;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.values[0] + sensorEvent.values[1] + sensorEvent.values[2]  >= 30){
            cpt++;
            if (cpt > 2){
                playPauseMeth();
                cpt = 1;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}


}