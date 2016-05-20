package tomi.piipposoft.sangz_client;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    TextView textView;
    private RecyclerView recyclerView;
    private AppCompatActivity thisActivity;
    private String dataSet;

    private RecyclerAdapter recyclerViewAdapter;

    //todo set this to shared preferences - prolly a good idea to ask user the URL too at some point and use that
    public static final String URL = "http://192.168.1.95:5000";
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = MainActivity.URL + "/sangz/api/playlist/";
                    new DownloadWebpageTask().execute(url);
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


    private class DownloadWebpageTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {

            try {

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(urls[0])
                        .build();

                Response response = client.newCall(request).execute();
                return response.body().string();

            }
            catch(IOException e){
//                Snackbar.make(thisActivity, "Something went wrong!", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "Got data: " + s);

            ArrayList<String> dataset = new ArrayList<>();
            //todo: lets not really handle json here, move this to more reasonable place
            // http://stackoverflow.com/questions/9605913/how-to-parse-json-in-android

            if (s != null) {

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONObject collection = jsonObject.getJSONObject("collection");
                    JSONArray items = collection.getJSONArray("items");


                    for(int i = 0; i < items.length(); i++) {

                        JSONObject aSong = items.getJSONObject(i);
                        String songHref = aSong.getString("href");
                        JSONArray songData = aSong.getJSONArray("data");
                        JSONObject songName = songData.getJSONObject(0);
//                    String songNameString = songName.getString("value");

                        //                dataset.add(jsonObject.getString())
                        dataset.add(songName.getString("value"));
                    }


                } catch (JSONException e) {

                    e.printStackTrace();
                }


                RecyclerAdapter adapter = new RecyclerAdapter(s);
                recyclerView.setAdapter(adapter);
            }

            else{
                thisActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(thisActivity, "something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.d(TAG, "ERROR, no data!");
            }
        }
    }
}
