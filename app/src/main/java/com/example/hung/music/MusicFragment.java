package com.example.hung.music;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import static android.content.Context.BIND_AUTO_CREATE;

public class MusicFragment extends Fragment implements MusicAdapter.OnClickItemMusic, View.OnClickListener {
    private ArrayList<Song> mSongs;
    private RecyclerView mRcvMusic;
    private MusicAdapter mMusicAdapter;
    private Intent mIntent;
    private MusicService mMusicService;
    private boolean isBound;
    private ServiceConnection serviceConnection;
    private TextView mTxtNameSong;
    private Button mBtnPlay;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recyclev_view_music, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeComponents();
        showNamemSongs();
    }

    private void initializeComponents() {
        mTxtNameSong = getActivity().findViewById(R.id.txt_name);
        mRcvMusic = getActivity().findViewById(R.id.rcv_music);
        mRcvMusic.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRcvMusic.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));

        mBtnPlay = getActivity().findViewById(R.id.btn_play);
        mBtnPlay.setOnClickListener(this);
        getActivity().findViewById(R.id.btn_previous).setOnClickListener(this);
        getActivity().findViewById(R.id.btn_next).setOnClickListener(this);

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MusicService.BoundService boundService = (MusicService.BoundService) service;
                mMusicService = boundService.getService();
                mMusicService.setmSongs(mSongs);
                isBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isBound = false;
            }
        };

        mIntent = new Intent(getActivity(), MusicService.class);
        getActivity().bindService(mIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    private void showNamemSongs() {
        mSongs = new ArrayList<>();
        getSongList();
        mMusicAdapter = new MusicAdapter(mSongs, getActivity());
        mMusicAdapter.setmOnClickItemMusic(this);
        mRcvMusic.setAdapter(mMusicAdapter);
    }

    private void getSongList() {
        ContentResolver resolver = getActivity().getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = resolver.query(musicUri, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            int nameColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            do {
                long id = musicCursor.getLong(idColumn);
                String name = musicCursor.getString(nameColumn);
                String artist = musicCursor.getString(artistColumn);
                String path = ContentUris.withAppendedId(
                        android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id).toString();
                mSongs.add(new Song(name, path, artist));
            }
            while (musicCursor.moveToNext());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void clickItem(int position) {
        mTxtNameSong.setText(mSongs.get(position).getmNameSong());
        mMusicService.setSong(position);
        mMusicService.playSong();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_previous:
                mMusicService.playPrev();
                break;

            case R.id.btn_play:
                mMusicService.pauseAndResume();
                break;

            case R.id.btn_next:
                mMusicService.playNext();
                break;

            default:
                break;
        }
    }
}
