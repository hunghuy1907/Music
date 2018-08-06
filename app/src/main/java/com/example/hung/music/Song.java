package com.example.hung.music;

public class Song {
    private String mNameSong;
    private String mPathSong;
    private String mArtistSong;

    public Song(String mNameSong, String mPathSong, String mArtistSong) {
        this.mNameSong = mNameSong;
        this.mPathSong = mPathSong;
        this.mArtistSong = mArtistSong;
    }

    public String getmArtistSong() {
        return mArtistSong;
    }

    public String getmNameSong() {
        return mNameSong;
    }

    public String getmPathSong() {
        return mPathSong;
    }
}
