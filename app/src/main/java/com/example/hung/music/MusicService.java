package com.example.hung.music;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import java.io.IOException;

public class MusicService extends Service {
    private static final String STOP = "stop";
    public MediaPlayer mMediaPlayer;
    private MusicReceiver mMusicReceiver;
    private int mLength;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (intent == null) {
            stopSelf();
            return new BoundService();
        }
        startForeGroundService();
        playSong(MusicFragment.positionOfSong);
        return new BoundService();
    }
    
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void startForeGroundService() {
        Intent intent = new Intent(this, MusicService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        intent.setAction(STOP);

        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_music)
                .setContentTitle("Name's song")
                .setContentText(intent.getStringExtra(Constance.NAME_SONG))
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", pendingIntent)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
        startForeground(Constance.NOTIFICATION_ID, notification);
        mMusicReceiver = new MusicReceiver();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(STOP);
        registerReceiver(mMusicReceiver, intentFilter);
    }

    private void playSong(int position) {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(MusicFragment.songs.get(position).getmPathSong());
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pauseSong() {
        mMediaPlayer.pause();
        mLength = mMediaPlayer.getCurrentPosition();
    }

    public void resumeSong() {
        mMediaPlayer.start();
        mMediaPlayer.seekTo(mLength);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void otherSong() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
        playSong(MusicFragment.positionOfSong);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mMediaPlayer.stop();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class BoundService extends Binder{
        public MusicService getService(){
            return MusicService.this;
        }
    }

    public class MusicReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null)
                return;
            switch (intent.getAction()) {
                case STOP:
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                    stopForeground(true);
                    stopSelf();
                    break;

                default:
                    break;
            }
        }
    }
}
