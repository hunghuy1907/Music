package com.example.hung.music;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MusicService extends Service {
    // id of notification
    private static final int NOTIFY_ID = 10;
    private MediaPlayer mMediaPlayer;
    private String mNameSong;
    private String mArtist;
    private int mPositionSong;
    private ArrayList<Song> mSongs;
    private boolean mIsShuffle = false;
    private Random mRandom;

    @Override
    public void onCreate() {
        super.onCreate();
        mPositionSong = 0;
        mRandom = new Random();
        mMediaPlayer = new MediaPlayer();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (intent == null) {
            stopSelf();
            return new BoundService();
        }
        return new BoundService();
    }

    public void playSong() {
        mMediaPlayer.reset();
        Song song = mSongs.get(mPositionSong);
        mArtist = song.getmArtistSong();
        mNameSong = song.getmNameSong();

        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(getApplicationContext(),
                    Uri.parse(mSongs.get(mPositionSong).getmPathSong()));
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    startForeGround();
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
                    playNext();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void startForeGround() {
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.play)
                .setOngoing(true)
                .setContentTitle(mNameSong)
                .setContentText(mArtist);
        Notification notification = builder.build();
        startForeground(NOTIFY_ID, notification);
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

    public void setSong(int songIndex) {
        mPositionSong = songIndex;
    }

    public void pauseAndResume() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        } else {
            mMediaPlayer.start();
        }
    }

    public void playPrev() {
        mPositionSong--;
        if (mPositionSong < 0) {
            mPositionSong = mSongs.size() - 1;
        }
        playSong();
    }

    public void playNext() {
        if (mIsShuffle) {
            int newSong = mPositionSong;
            while (newSong == mPositionSong) {
                newSong = mRandom.nextInt(mSongs.size());
            }
            mPositionSong = newSong;
        } else {
            mPositionSong++;
            if (mPositionSong >= mSongs.size())
                mPositionSong = 0;
        }
        playSong();
    }

    public void setmSongs(ArrayList<Song> mSongs) {
        this.mSongs = mSongs;
    }
}
