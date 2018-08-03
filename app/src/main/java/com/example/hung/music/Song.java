package com.example.hung.music;

public class Song {
    private String mNameSong;
    private String mPathSong;

    public Song(String mNameSong, String mPathSong) {
        this.mNameSong = mNameSong;
        this.mPathSong = mPathSong;
    }

    public Song(String mNameSong) {
        this.mNameSong = mNameSong;
    }

    public String getmNameSong() {
        return mNameSong;
    }

    public void setmNameSong(String mNameSong) {
        this.mNameSong = mNameSong;
    }

    public String getmPathSong() {
        return mPathSong;
    }
}
