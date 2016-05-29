package tomi.piipposoft.sangz_client;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import tomi.piipposoft.sangz_client.Playlist.PlaylistActivity;

/**
 * Created by Domu on 21-May-16.
 */
public class Utils {

    private static final int HTTP_NO_CONTENT = 204;
    private static final int HTTP_CREATED = 201;

    // TODO references to the activity interface should maybe be replaced by WeakReferences?

    /**
     * Async task for making a generic GET request to the server,
     * Params should include the URL for the server where the
     * request is sent.
     *
     * Will call consumeData from an interface that MUST BE SET
     * in setCallingActivity. The consumeData will be supplied the JSON
     * response that is received from server.
     */
    public static class GetDataTask extends AsyncTask<String, Void, String> {

        private String TAG = "DownloadWebPageTask";

        private WebInterface.IConsumeData caller;

        public GetDataTask setCallingActivity(WebInterface.IConsumeData callingActivity) {

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

                } catch (IOException e) {
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
            } else {
                //todo do some magic to show error in UI in caller activity
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
    public static class PostVoteTask extends AsyncTask<String, Void, String> {

        final String TAG = "PostVoteTask";

        private WebInterface.IDataChanged caller;

        public PostVoteTask setCallingActivity(WebInterface.IDataChanged callingActivity) {

            caller = callingActivity;
            return this;
        }

        @Override
        protected String doInBackground(String... params) {

            try {

                OkHttpClient client = new OkHttpClient();

                Log.d(TAG, "MEDIA_TYPE_JSON: " + params[1]);
                Log.d(TAG, "Media type: " + PlaylistActivity.MEDIA_TYPE_JSON);

                RequestBody body = RequestBody.create(PlaylistActivity.MEDIA_TYPE_JSON, params[1]);

                Log.d(TAG, body.contentType().toString());

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
            caller.notifyServerDataChanged();
        }
    }

    /**
     * Async task for sending PUT messages
     */
    public static class EditSongTask extends AsyncTask<String, Void, String> {

        private final String TAG = "EditSongTask";

        private WebInterface.IDataChanged caller;

        public EditSongTask setCallingActivity(WebInterface.IDataChanged callingActivity) {

            caller = callingActivity;
            return this;
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(PlaylistActivity.MEDIA_TYPE_JSON, params[1]);

                Request request = new Request.Builder()
                        .url(params[0])
                        .put(body)
                        .build();

                Response response = client.newCall(request).execute();
                return response.body().string();

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "response: " + s);
            caller.notifyServerDataChanged();
        }
    }

    /**
     * Async task for sending DELETE messages
     */
    public static class DeleteSongTask extends AsyncTask<String, Void, Void> {

        private final String TAG = "DeleteSongTask";
        private WebInterface.IRemovedData caller;

        public DeleteSongTask setCallingActivity(WebInterface.IRemovedData callingActivity) {

            caller = callingActivity;
            return this;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(TAG, "DELETE call finishing");
            caller.songDeleted();
        }

        @Override
        protected Void doInBackground(String... params) {


            try {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(params[0])
                        .delete()
                        .build();


                client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     * async task for POST when adding a new song,
     * uses collection+json as mime type
     */
    public static class AddSongTask extends AsyncTask<String, Void, Integer> {

        private WebInterface.IDataChanged caller;
        private final String TAG = "AddSongTask";

        public AddSongTask setCallingActivity(WebInterface.IDataChanged caller) {
            this.caller = caller;
            return this;
        }

        @Override
        protected Integer doInBackground(String... params) {

            try {
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(PlaylistActivity.MEDIA_TYPE_COLLECTION_JSON, params[1]);
                Log.d(TAG, "header used:" + PlaylistActivity.MEDIA_TYPE_COLLECTION_JSON + " body: " + params[1]);

                Request request = new Request.Builder()
                        .url(params[0])
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();
                Log.d(TAG, response.message());
                return response.code();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            if (integer == HTTP_CREATED)
                caller.notifyServerDataChanged();
            else {
                Log.d(TAG, "something went wrong, got header " + integer);
            }

        }
    }
}