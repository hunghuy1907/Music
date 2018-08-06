package com.example.hung.music;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder>{
    private ArrayList<Song> mSongs;
    private LayoutInflater mInflater;
    private OnClickItemMusic mOnClickItemMusic;

    public MusicAdapter(ArrayList<Song> songs, Context context) {
        this.mSongs = songs;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_music, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Song song = mSongs.get(position);
        holder.mTxtNameSong.setText(song.getmNameSong().toString());
        holder.mTxtArtist.setText(song.getmArtistSong().toString());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickItemMusic.clickItem(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView mTxtNameSong;
        private TextView mTxtArtist;

        public ViewHolder(View itemView) {
            super(itemView);

            mTxtNameSong = itemView.findViewById(R.id.txt_name_song);
            mTxtArtist = itemView.findViewById(R.id.txt_artist);
        }
    }

    public void setmOnClickItemMusic(OnClickItemMusic mOnClickItemMusic) {
        this.mOnClickItemMusic = mOnClickItemMusic;
    }

    public interface OnClickItemMusic {
        void clickItem(int position);
    }
}
