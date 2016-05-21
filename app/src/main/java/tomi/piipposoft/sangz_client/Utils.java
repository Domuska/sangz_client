package tomi.piipposoft.sangz_client;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import tomi.piipposoft.sangz_client.Playlist.MainActivity;

/**
 * Created by Domu on 21-May-16.
 */
public class Utils {

    /**
     * Async task for making a GET request to the server,
     * Params should include the URL for the server where the
     * request is sent.
     *
     * Will call consumeData from an interface that MUST BE SET
     * in setCallingActivity. The consumeData will be supplied the JSON
     * response that is gotten from server.
     */
    public static class DownloadPlaylistTask extends AsyncTask<String, Void, String> {

        private String TAG = "DownloadWebPageTask";

        private WebInterface.CallerActivity caller;

        public DownloadPlaylistTask setCallingActivity(WebInterface.CallerActivity callingActivity){

            caller = callingActivity;
            return this;
        }

        @Override
        protected String doInBackground(String... urls) {

            if (caller != null) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(urls[0])
                            .build();

                    Response response = client.newCall(request).execute();
                    return response.body().string();

                }
                catch(IOException e){
                    e.printStackTrace();
                    return null;
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d(TAG, "Got data: " + s);
            // http://stackoverflow.com/questions/9605913/how-to-parse-json-in-android

            if (s != null) {

                caller.consumeData(s);
            }

            else{
                //todo do some magic to show error in UI in caller activity
//                thisActivity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(thisActivity, "something went wrong!", Toast.LENGTH_SHORT).show();
//                    }
//                });
                Log.d(TAG, "ERROR, no data!");
            }
        }
    }

    /**
     * Async task for sending a POST to the server (for now only for votes)
     * params for .execute should include an array of strings
     * that has URL of the server as the first element
     * and the body of the request as the second element
     */
    public static class PostVoteTask extends AsyncTask<String, Void, String>{

        final String TAG = "PostVoteTask";

        @Override
        protected String doInBackground(String... params) {

            try {

                OkHttpClient client = new OkHttpClient();

                Log.d(TAG, "MEDIA_TYPE_JSON: " + params[1]);
                Log.d(TAG, "Media type: " + MainActivity.MEDIA_TYPE_JSON);

                RequestBody body = RequestBody.create(MainActivity.MEDIA_TYPE_JSON, params[1]);

                Log.d(TAG,body.contentType().toString());

                Request request = new Request.Builder()
                        .url(params[0])
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();
                return response.body().string();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d(TAG, "Response: " + s);

        }
    }

    public static class DownloadSongDetailsTask extends AsyncTask<String, Void, String> {

        private WebInterface.CallerActivity caller;

        public DownloadSongDetailsTask setCallingActivity(WebInterface.CallerActivity callingActivity){

            caller = callingActivity;
            return this;
        }

        @Override
        protected String doInBackground(String... params) {
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}
