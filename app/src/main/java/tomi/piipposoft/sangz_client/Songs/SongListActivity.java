package tomi.piipposoft.sangz_client.Songs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import tomi.piipposoft.sangz_client.Playlist.PlaylistActivity;
import tomi.piipposoft.sangz_client.R;
import tomi.piipposoft.sangz_client.Utils;
import tomi.piipposoft.sangz_client.WebInterface;

public class SongListActivity extends AppCompatActivity implements
        WebInterface.INewData{

    private final String TAG = "SongListActivity";

    private String server_URL;
    private ArrayList<SingleSong> songsList;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        songsList = new ArrayList<>();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new SongListRecyclerAdapter(songsList));

        SharedPreferences prefs = this.getSharedPreferences(
                getResources().getString(R.string.sharedPreferences),
                Context.MODE_PRIVATE
        );

        server_URL = prefs.getString(
                getResources().getString(R.string.sharedPreferencesUrlKey),
                ""
        );

        String thisResourceURL = server_URL + "/sangz/api/songs/";

        new Utils.DownloadPlaylistTask().setCallingActivity(this).execute(thisResourceURL);

        Toolbar bottomToolBar = (Toolbar) findViewById(R.id.toolbar2);
        if (bottomToolBar != null) {
            bottomToolBar.inflateMenu(R.menu.menu_bottom_menu);

            bottomToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    if (item.getItemId() == R.id.bottom_bar_playlist) {
                        Intent i = new Intent(SongListActivity.this, PlaylistActivity.class);
                        startActivity(i);
                    } else if (item.getItemId() == R.id.bottom_bar_songs_list) {
                        Intent i = new Intent(SongListActivity.this, SongListActivity.class);
                        startActivity(i);
                    }

                    return true;
                }
            });
        }
    }

    @Override
    public void consumeData(String serverResponse) {
        Log.d(TAG, serverResponse);

        try{
            JSONObject collection= new JSONObject(serverResponse).getJSONObject("collection");
            JSONArray items = collection.getJSONArray("items");

            Log.d(TAG, "" + items.length());

            for(int i = 0; i < items.length(); i++){
                JSONObject oneSong = items.getJSONObject(i);
                JSONArray data = oneSong.getJSONArray("data");

                String songUrl = this.server_URL + oneSong.getString("href");
                Log.d(TAG, "url:" + songUrl);
                String songName = "";
                String artistName = "";
                String albumName = "";

                for(int j = 0; j < data.length(); j++){
                    JSONObject entry = data.getJSONObject(j);
                    String string = entry.getString("name");
//                    Log.d(TAG, "entry:" + string + " value: " + entry.getString("value"));

                    switch(string){
                        case "songname":
                            songName = entry.getString("value");
                            break;
                        case "album":
                            albumName = entry.getString("value");
                            break;
                        case "artist":
                            artistName = entry.getString("value");
                            break;
                    }
                }

                Log.d(TAG, "found values:" + songName + " " + artistName + " " + albumName + " " + songUrl);

                songsList.add(new SingleSong(songName, artistName, albumName, songUrl));
//                recyclerView.getAdapter().notifyDataSetChanged();
                int itemCount = recyclerView.getAdapter().getItemCount();
                recyclerView.getAdapter().notifyItemChanged(itemCount + i);

            }

        }
        catch (JSONException e){
            e.printStackTrace();
        }

    }

    @Override
    public void notifyDataChanged() {

    }

}
