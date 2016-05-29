package tomi.piipposoft.sangz_client.Songs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
        WebInterface.IConsumeData,
        WebInterface.IDataChanged,
        AddSongDialog.NoticeDialogListener{

    private final String TAG = "SongListActivity";

    private String thisResourceURL;
    private String server_URL;
    private ArrayList<Song> songsList;
    private RecyclerView recyclerView;
    private SharedPreferences prefs;

    DrawerLayout drawerLayout;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        songsList = new ArrayList<>();


        prefs = this.getSharedPreferences(
                getResources().getString(R.string.sharedPreferences),
                Context.MODE_PRIVATE
        );

        server_URL = prefs.getString(
                getResources().getString(R.string.sharedPreferencesUrlKey),
                ""
        );

        thisResourceURL = server_URL + "/sangz/api/songs/";

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    generateAddSongFragment();
//                    String[] params = {thisResourceURL, generateAddSongPostBody()};
//                    new Utils.AddSongTask().setCallingActivity(SongListActivity.this).execute(params);
                }
            });
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new SongListRecyclerAdapter(songsList));



        new Utils.GetDataTask().setCallingActivity(this).execute(thisResourceURL);

        initializeDrawer();
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
                String songId = "";

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
                        case "ID":
                            songId = entry.getString("value");
                            break;
                    }
                }

                Log.d(TAG, "found values:" + songName + " " + artistName + " " + albumName + " " + songUrl);


                songsList.add(new Song(songName, artistName, albumName, songUrl, songId));
                int itemCount = recyclerView.getAdapter().getItemCount();
//                recyclerView.getAdapter().notifyItemChanged(itemCount + i);
                recyclerView.getAdapter().notifyItemChanged(i);


//                if (i >= songsList.size()) {
//                    songsList.add(new Song(songName, artistName, albumName, songUrl, songId));
//                    int itemCount = recyclerView.getAdapter().getItemCount();
//                    recyclerView.getAdapter().notifyItemChanged(itemCount + i);
//                }


            }

        }
        catch (JSONException e){
            e.printStackTrace();
        }

    }

    @Override
    public void notifyServerDataChanged() {
        new Utils.GetDataTask().setCallingActivity(this).execute(thisResourceURL);
    }

    @Override
    public void onDialogPositiveClick(ArrayList<String> textInputs) {
        Log.d(TAG, "got strings from dialogFragment: ");
        for(int i = 0; i < textInputs.size(); i++){
            Log.d(TAG, i + " " + textInputs.get(i));
        }

        String[] params = {thisResourceURL, generateAddSongPostBody(textInputs.get(0), textInputs.get(1))};

        //set songsList empty since new data should be arriving soon (a bad idea but hey, who's gonna sue me at this point)
        songsList = new ArrayList<>();
        new Utils.AddSongTask().setCallingActivity(SongListActivity.this).execute(params);
    }

    private void generateAddSongFragment(){
        DialogFragment dialog = new AddSongDialog();
        dialog.show(this.getSupportFragmentManager(), "AddSongDialog");
    }

    private String generateAddSongPostBody(String songName, String mediaLocation){


        try {
            JSONObject body = new JSONObject();

            JSONArray data = new JSONArray();
            JSONObject songNameObject = new JSONObject();
            songNameObject.put("name", "song_name");
            songNameObject.put("value", songName);
            data.put(0, songNameObject);

            JSONObject userIdObject = new JSONObject();
            userIdObject.put("name", "user_id");

            String user_id = prefs.getString(
                    getResources().getString(R.string.sharedPreferencesUserIDKey),
                    ""
            );

            userIdObject.put("value", user_id);
            data.put(1, userIdObject);

            JSONObject mediaLocationObject = new JSONObject();
            mediaLocationObject.put("name", "media_location");
            mediaLocationObject.put("value", mediaLocation);
            data.put(2, mediaLocationObject);

            JSONObject dataObject = new JSONObject();
            dataObject.put("data", data);

            JSONObject templateObject = new JSONObject();
            templateObject.put("template", dataObject);

            Log.d(TAG, templateObject.toString());

            return templateObject.toString();
        }
        catch(JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    private void initializeDrawer(){

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

        if (navigationView != null) {

            navigationView.setCheckedItem(R.id.drawer_songlist);

            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {

                    drawerLayout.closeDrawers();

                    if(item.getItemId() == R.id.drawer_playlist){
                        Log.d(TAG, "playlist clicked");
                        Intent i = new Intent(SongListActivity.this, PlaylistActivity.class);
                        startActivity(i);
                        return true;

                    }
                    else if(item.getItemId() == R.id.drawer_songlist){
                        // already here!
                        return true;
                    }


                    return false;
                }
            });

        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

    }

}
