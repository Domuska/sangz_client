package tomi.piipposoft.sangz_client.Song;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import tomi.piipposoft.sangz_client.JsonGenerator;
import tomi.piipposoft.sangz_client.Playlist.PlaylistActivity;
import tomi.piipposoft.sangz_client.R;
import tomi.piipposoft.sangz_client.Songs.SongListActivity;
import tomi.piipposoft.sangz_client.Utils;
import tomi.piipposoft.sangz_client.WebInterface;

public class SongDetailsActivity extends AppCompatActivity
        implements WebInterface.IConsumeData,
        WebInterface.IRemovedData,
        WebInterface.IDataChanged,
        EditSongDialog.NoticeDialogListener{

    public static String EXTRAS_URL = "url_extra";

    private final String TAG = "SongDetailsActivity";
    DrawerLayout drawerLayout;
    Toolbar toolbar;

    private WebInterface.IConsumeData thisActivity;
    private String url;
    String thisSongurl;
    private String vote_url;
    private TextView songName;
    private Button upvoteButton, downvoteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        thisActivity = this;

        songName = (TextView) findViewById(R.id.songDetailsSongName);
        upvoteButton = (Button) findViewById(R.id.song_details_upvote_button);
        downvoteButton = (Button) findViewById(R.id.song_details_downvote_button);


        SharedPreferences preferences = this.getSharedPreferences(
                getResources().getString(R.string.sharedPreferences),
                Context.MODE_PRIVATE
        );


        url = preferences.getString(
                getResources().getString(R.string.sharedPreferencesUrlKey),
                ""
        );

        thisSongurl = url + getIntent().getExtras().getString(SongDetailsActivity.EXTRAS_URL);
        Log.d(TAG, "URL: " + thisSongurl);
        initializeDrawer();

        final String user_id = preferences.getString(
                getResources().getString(R.string.sharedPreferencesUserIDKey),
                ""
        );

        upvoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] params = new String[2];
                params[0] = vote_url;
                params[1] = JsonGenerator.generateUpvoteBody(user_id);
                new Utils.PostVoteTask().setCallingActivity(SongDetailsActivity.this).execute(params);
            }
        });

        downvoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] params = new String[2];
                params[0] = vote_url;
                params[1] = JsonGenerator.generateDownvoteBody(user_id);
                new Utils.PostVoteTask().setCallingActivity(SongDetailsActivity.this).execute(params);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        new Utils.GetDataTask().setCallingActivity(thisActivity).execute(this.thisSongurl);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_song_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.toolbar_delete_song){
            sendDeleteSong();
        }
        else if(item.getItemId() == R.id.toolbar_modify_song){
            generateEditSongDetailsFragment();
        }

        return true;
    }

    @Override
    public void consumeData(String JSONString) {
        Log.d(TAG, JSONString);

        try {
            JSONObject object = new JSONObject(JSONString);
            String songNameString = object.getString("songname");
            songName.setText(songNameString);

            upvoteButton.setVisibility(View.VISIBLE);
            downvoteButton.setVisibility(View.VISIBLE);
            vote_url = url +  object.getJSONArray("links").getJSONObject(0).getString("votes");
            Log.d(TAG, "vote_url got from server: " + vote_url);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notifyServerDataChanged() {
        new Utils.GetDataTask().setCallingActivity(thisActivity).execute(this.thisSongurl);
    }

    // called when OK is clicked in edit song dialog
    @Override
    public void onDialogPositiveClick(String dialogText) {
        Log.d(TAG, "got text from dialog: " + dialogText);

        String[] params = new String[2];

        params[0] = SongDetailsActivity.this.thisSongurl;
        params[1] = generatePostBody(dialogText);
        Log.d(TAG, "PUT body: " + params[1]);
        new Utils.EditSongTask().setCallingActivity(SongDetailsActivity.this).execute(params);
    }

    private void initializeDrawer(){

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {

                    if(item.isChecked())
                        item.setChecked(false);
                    else
                        item.setChecked(true);

                    drawerLayout.closeDrawers();

                    if(item.getItemId() == R.id.drawer_playlist){
                        Intent i = new Intent(SongDetailsActivity.this, PlaylistActivity.class);
                        startActivity(i);
                        return true;

                    }
                    else if(item.getItemId() == R.id.drawer_songlist){
                        Intent i = new Intent(SongDetailsActivity.this, SongListActivity.class);
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

    private String generatePostBody(String songName){

        return "{" +
            "\"songname\": \"" + songName +"\"," +
            "\"medialocation\": \"youtube\"" +
            "}";
    }

    private void sendDeleteSong(){

        String[] params = {SongDetailsActivity.this.url};
        Log.d(TAG, "sending DELETE to URL: " + params[0]);
        new Utils.DeleteSongTask().setCallingActivity(SongDetailsActivity.this).execute(params);

    }


    private void generateEditSongDetailsFragment(){

        DialogFragment dialog = new EditSongDialog();
        dialog.show(SongDetailsActivity.this.getSupportFragmentManager(), "EditSongDetailsFragment");

    }

    @Override
    public void songDeleted() {
        SongDetailsActivity.this.finish();
    }
}
