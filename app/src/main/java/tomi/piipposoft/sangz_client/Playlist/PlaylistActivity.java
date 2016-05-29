package tomi.piipposoft.sangz_client.Playlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import tomi.piipposoft.sangz_client.R;
import tomi.piipposoft.sangz_client.Songs.SongListActivity;
import tomi.piipposoft.sangz_client.Utils;
import tomi.piipposoft.sangz_client.WebInterface;

public class PlaylistActivity extends AppCompatActivity implements
        WebInterface.IConsumeData,
        WebInterface.IDataChanged{

    private final String TAG = "MainActivity";
    TextView textView;
    private RecyclerView recyclerView;
    private WebInterface.IConsumeData thisActivity;

    private String server_URL;
    private String serverResponse;

    private DrawerLayout drawerLayout;

    // todo should go to sharedprefs
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");
    public static final MediaType MEDIA_TYPE_COLLECTION_JSON = MediaType.parse("application/vnd.collection+json");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        thisActivity = this;

        textView = (TextView) findViewById(R.id.textView);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        SharedPreferences prefs = this.getSharedPreferences(
                getResources().getString(R.string.sharedPreferences),
                Context.MODE_PRIVATE
        );

        server_URL = prefs.getString(
                getResources().getString(R.string.sharedPreferencesUrlKey),
                ""
        );

        server_URL += "/sangz/api/playlist/";
        sendGETPlaylist();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_modify_song);
//        if (fab != null) {
//
//            fab.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    new Utils.GetDataTask().setCallingActivity(thisActivity).execute(server_URL);
//                }
//            });
//        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

        if (navigationView != null) {

            navigationView.setCheckedItem(R.id.drawer_playlist);

            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {

                    drawerLayout.closeDrawers();

                    if(item.getItemId() == R.id.drawer_playlist){
                        Log.d(TAG, "playlist clicked");
                        // already here!
                        return true;

                    }
                    else if(item.getItemId() == R.id.drawer_songlist){
                        Intent i = new Intent(PlaylistActivity.this, SongListActivity.class);
                        startActivity(i);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_playlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            sendGETPlaylist();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //todo this is a very bad way of doing this, we should handle JSON in here
    // and make it into a list and use this list to initialize the adapter
    public void consumeData(String JSONstring){

        serverResponse = JSONstring;

        try {
            JSONObject jsonObject = new JSONObject(serverResponse);
            JSONObject collection = jsonObject.getJSONObject("collection");
            JSONArray items = collection.getJSONArray("items");


            for(int i = 0; i < items.length(); i++) {

                JSONObject aSong = items.getJSONObject(i);
                String songHref = aSong.getString("href");
                JSONArray songData = aSong.getJSONArray("data");
                JSONObject songName = songData.getJSONObject(0);
            }

            PlaylistRecyclerAdapter adapter = new PlaylistRecyclerAdapter(serverResponse, this);
            recyclerView.setAdapter(adapter);


        } catch (JSONException e) {

            e.printStackTrace();
        }


    }

    @Override
    public void notifyServerDataChanged() {
        sendGETPlaylist();
    }

    private void sendGETPlaylist(){
        new Utils.GetDataTask().setCallingActivity(thisActivity).execute(server_URL);
    }
}
