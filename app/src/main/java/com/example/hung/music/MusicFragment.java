package com.example.hung.music;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;
import static com.example.hung.appmusic.MusicService.*;
import static com.example.hung.appmusic.R.drawable.pause;

public class MusicFragment extends Fragment implements MusicAdapter.OnClickItemMusic, View.OnClickListener {
    public static List<Song> songs;
    public static int positionOfSong;
    private RecyclerView mRcvMusic;
    private MusicAdapter mMusicAdapter;
    private Intent mIntent;
    private MusicService mMusicService;
    private boolean isBound;
    private boolean isPause;
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
        showNameSongs();
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
                isBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isBound = false;
            }
        };
    }

    private void showNameSongs() {
        songs = new ArrayList<>();
        getNameAndPathResource(new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath()));

        mMusicAdapter = new MusicAdapter(songs, getActivity());
        mMusicAdapter.setmOnClickItemMusic(this);
        mRcvMusic.setAdapter(mMusicAdapter);
    }

    private List<File> getNameAndPathResource(File root) {
        List<File> fileList = new ArrayList<>();
        File[] files = root.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                fileList.addAll(getNameAndPathResource(files[i]));
            } else {
                if (files[i].getName().endsWith(".mp3")) {
                    String name = files[i].getName().toString().substring(0,
                            files[i].getName().lastIndexOf("."));
                    int index = name.indexOf("_-");
                    if (index > 0) {
                        name = name.substring(0, name.lastIndexOf("_-"));
                    }
                    String path = files[i].getPath().toString();
                    Song song = new Song(name, path);
                    songs.add(song);
                    fileList.add(files[i]);
                }
            }
        }
        return fileList;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void clickItem(int position) {
        mTxtNameSong.setText(songs.get(position).getmNameSong());
        positionOfSong = position;
        mIntent = new Intent(getActivity(), MusicService.class);
        getActivity().bindService(mIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_previous:
                positionOfSong--;
                mMusicService.otherSong();
                mTxtNameSong.setText(songs.get(positionOfSong).getmNameSong());
                mBtnPlay.setBackgroundResource(R.drawable.play);
                isPause = false;
                break;

            case R.id.btn_play:
                if (!isPause) {
                    mMusicService.pauseSong();
                    mBtnPlay.setBackgroundResource(R.drawable.pause);
                    isPause = true;

                } else {
                    mMusicService.resumeSong();
                    isPause = false;
                    mBtnPlay.setBackgroundResource(R.drawable.play);
                }
                break;

            case R.id.btn_next:
                positionOfSong++;
                mMusicService.otherSong();
                mTxtNameSong.setText(songs.get(positionOfSong).getmNameSong());
                mBtnPlay.setBackgroundResource(R.drawable.play);
                isPause = false;
                break;

            default:
                break;
        }
    }
}
