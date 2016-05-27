package tomi.piipposoft.sangz_client.Song;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import tomi.piipposoft.sangz_client.R;
import tomi.piipposoft.sangz_client.Utils;
import tomi.piipposoft.sangz_client.WebInterface;

public class SongDetailsActivity extends AppCompatActivity
        implements WebInterface.INewData,
        WebInterface.IRemovedData,
        EditSongDialog.NoticeDialogListener{

    public static String EXTRAS_URL = "url_extra";

    private final String TAG = "SongDetailsActivity";

    private WebInterface.INewData thisActivity;
    private String url;
    private TextView songName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        ImageButton deleteSongButton = (ImageButton) toolbar.findViewById(R.id.toolbar_delete_song);
        deleteSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDeleteSong();
            }
        });

        ImageButton modifySongButton = (ImageButton) toolbar.findViewById(R.id.toolbar_modify_song);
        modifySongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateEditSongDetailsFragment();
            }
        });

        setSupportActionBar(toolbar);

        thisActivity = this;

        songName = (TextView) findViewById(R.id.songDetailsSongName);


        SharedPreferences preferences = this.getSharedPreferences(
                getResources().getString(R.string.sharedPreferences),
                Context.MODE_PRIVATE
        );


        url = preferences.getString(
                getResources().getString(R.string.sharedPreferencesUrlKey),
                ""
        );

        url += getIntent().getExtras().getString(SongDetailsActivity.EXTRAS_URL);
        Log.d(TAG, "URL: " + url);

        final String serverURL = url;




//        if (modifySongFab != null) {
//            modifySongFab.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
////                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                            .setAction("Action", null).show();
//
//                    generateEditSongDetailsFragment();
////                    String[] params = new String[2];
////
////                    params[0] = SongDetailsActivity.this.url;
////                    params[1] = generateEditSongDetailsFragment();
////                    Log.d(TAG, "PUT body: " + params[1]);
////                    new Utils.EditSongTask().setCallingActivity(thisActivity).execute(params);
//
//                    //put this button to expand into delete/edit?
//                }
//            });
//        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        new Utils.DownloadPlaylistTask().setCallingActivity(thisActivity).execute(this.url);

    }

    @Override
    public void consumeData(String JSONString) {
        Log.d(TAG, JSONString);

        try {
            JSONObject object = new JSONObject(JSONString);
            String songNameString = object.getString("songname");
            songName.setText(songNameString);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notifyDataChanged() {
        new Utils.DownloadPlaylistTask().setCallingActivity(thisActivity).execute(this.url);
    }

    // called when OK is clicked in edit song dialog
    @Override
    public void onDialogPositiveClick(String dialogText) {
        Log.d(TAG, "got text from dialog: " + dialogText);

        String[] params = new String[2];

        params[0] = SongDetailsActivity.this.url;
        params[1] = generatePostBody(dialogText);
        Log.d(TAG, "PUT body: " + params[1]);
        new Utils.EditSongTask().setCallingActivity(SongDetailsActivity.this).execute(params);
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
