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
import okhttp3.Response;
import tomi.piipposoft.sangz_client.Playlist.WebInterface;

/**
 * Created by Domu on 21-May-16.
 */
public class Utils {

    public static class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        private String TAG = "DownloadWebPageTask";

        private WebInterface.CallerActivity caller;

        public DownloadWebpageTask setCallingActivity(WebInterface.CallerActivity callingActivity){

            caller = callingActivity;
            return this;
        }

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
                        dataset.add(songName.getString("value"));
                    }


                } catch (JSONException e) {

                    e.printStackTrace();
                }

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
}
