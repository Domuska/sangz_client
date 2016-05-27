package tomi.piipposoft.sangz_client.Songs;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

import tomi.piipposoft.sangz_client.R;

/**
 * Created by Domu on 28-May-16.
 */
public class SongListRecyclerAdapter extends RecyclerView.Adapter<SongListRecyclerAdapter.ViewHolder>{

    private ArrayList<SingleSong> songList;
    private final String TAG = "SongListRecyclerAdapter";

    public SongListRecyclerAdapter(ArrayList<SingleSong> songParams)
    {
        songList = songParams;
    }
//    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView songName;
        public TextView artistName;
        public TextView albumname;

//        public IViewHolderClicks clickListener;

//        public ViewHolder(View rowView, IViewHolderClicks listener){
        public ViewHolder(View rowView){

            super(rowView);
            songName = (TextView) rowView.findViewById(R.id.songList_songName);
            artistName = (TextView) rowView.findViewById(R.id.songList_artistName);
            albumname = (TextView) rowView.findViewById(R.id.songList_albumName);
//            clickListener = listener;
        }

//        @Override
//        public void onClick(View v) {
//            //handle button clicks in here
//        }

//        public interface IViewHolderClicks {
//
//            void onSongNameClicker(TextView textView);
//        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View rowWiew = inflater.inflate(R.layout.songlist_recycler_row, parent, false);
        return new ViewHolder(rowWiew);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {


        holder.songName.setText(songList.get(holder.getAdapterPosition()).getSongName());
        holder.songName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "songname clicked: " + holder.songName.getText());
            }
        });
    }



    @Override
    public int getItemCount() {
        return songList.size();
    }
}
