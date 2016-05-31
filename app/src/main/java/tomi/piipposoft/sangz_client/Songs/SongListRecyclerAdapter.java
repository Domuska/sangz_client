package tomi.piipposoft.sangz_client.Songs;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import tomi.piipposoft.sangz_client.R;
import tomi.piipposoft.sangz_client.Song.SongDetailsActivity;

/**
 * Created by Domu on 28-May-16.
 */
public class SongListRecyclerAdapter extends RecyclerView.Adapter<SongListRecyclerAdapter.ViewHolder>{

    private ArrayList<Song> songList;
    private final String TAG = "SongListRecyclerAdapter";
    private Context myContext;

    public SongListRecyclerAdapter(ArrayList<Song> songParams, Context context)
    {
        songList = songParams;
        myContext = context;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView songName;
        public TextView artistName;
        public TextView albumname;

        public ViewHolder(View rowView){

            super(rowView);
            songName = (TextView) rowView.findViewById(R.id.songList_songName);
            artistName = (TextView) rowView.findViewById(R.id.songList_artistName);
            albumname = (TextView) rowView.findViewById(R.id.songList_albumName);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View rowWiew = inflater.inflate(R.layout.songlist_recycler_row, parent, false);
        return new ViewHolder(rowWiew);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

//        final Context context = my

        holder.songName.setText(songList.get(holder.getAdapterPosition()).getSongName());
        holder.songName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "songname clicked: " + holder.songName.getText());
                Intent i = new Intent(SongListRecyclerAdapter.this.myContext, SongDetailsActivity.class);
                i.putExtra( SongDetailsActivity.EXTRAS_URL, songList.get(holder.getAdapterPosition()).getUrl());
                myContext.startActivity(i);
            }
        });
    }





    @Override
    public int getItemCount() {
        return songList.size();
    }
}
