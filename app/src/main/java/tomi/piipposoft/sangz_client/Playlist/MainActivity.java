package tomi.piipposoft.sangz_client.Playlist;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import tomi.piipposoft.sangz_client.R;
import tomi.piipposoft.sangz_client.Utils;
import tomi.piipposoft.sangz_client.WebInterface;

public class MainActivity extends AppCompatActivity implements WebInterface.INewData {

    private final String TAG = "MainActivity";
    TextView textView;
    private RecyclerView recyclerView;
    private WebInterface.INewData thisActivity;

    private String server_URL;
    private String serverResponse;

    //todo prolly a good idea to ask user the URL too at some point and use that
    private String URL = "http://192.168.1.95:5000";
    public static final String USER_ID = "1";

    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        prefs.edit().putString(
                getResources().getString(R.string.sharedPreferencesUrlKey),
                this.URL)
                .apply();

        server_URL = prefs.getString(
                getResources().getString(R.string.sharedPreferencesUrlKey),
                ""
        );

        server_URL += "/sangz/api/playlist/";

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_modify_song);
        if (fab != null) {

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Utils.DownloadPlaylistTask().setCallingActivity(thisActivity).execute(server_URL);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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

            RecyclerAdapter adapter = new RecyclerAdapter(serverResponse, this);
            recyclerView.setAdapter(adapter);


        } catch (JSONException e) {

            e.printStackTrace();
        }


    }

    @Override
    public void notifyDataChanged() {
        new Utils.DownloadPlaylistTask().setCallingActivity(thisActivity).execute(server_URL);
    }
}
