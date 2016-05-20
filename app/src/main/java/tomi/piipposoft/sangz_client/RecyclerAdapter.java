package tomi.piipposoft.sangz_client;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by Domu on 18-May-16.
 *
 * https://developer.android.com/training/material/lists-cards.html
 *
 * https://guides.codepath.com/android/using-the-recyclerview
 *
 * item clicks:
 * http://stackoverflow.com/questions/24885223/why-doesnt-recyclerview-have-onitemclicklistener-and-how-recyclerview-is-dif
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<String> myData;
    private ArrayList<JSONArray> songDataArrays = new ArrayList<>();
    private ArrayList<String> songURLs = new ArrayList<>();
    private ArrayList<String> songVoteURLs = new ArrayList<>();
    private final String TAG = "RecyclerAdapter";



    //    public RecyclerAdapter(ArrayList<String> data) {
//        myData = data;
//    }
    public RecyclerAdapter(String data) {



        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONObject collection = jsonObject.getJSONObject("collection");
            JSONArray items = collection.getJSONArray("items");


            for (int i = 0; i < items.length(); i++) {

                JSONObject aSong = items.getJSONObject(i);
                String songHref = aSong.getString("href");
                String songVoteHref = aSong.getString("href_vote");
                JSONArray songData = aSong.getJSONArray("data");
                JSONObject songName = songData.getJSONObject(0);
//                    String songNameString = songName.getString("value");

                //                dataset.add(jsonObject.getString())
//                dataset.add(songName.getString("value"));

                songDataArrays.add(songData);
                songURLs.add(songHref);
                songVoteURLs.add(songVoteHref);
            }


        } catch (JSONException e) {

            e.printStackTrace();
        }
    }


    /**
     * Viewholder for doing viewholder stuff
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView songName;
        public TextView voteCount;
        public TextView artistName;
        public Button rowUpvoteButton;
        public Button rowDownvoteButton;
        public IViewHolderClicks clickListener;


        public ViewHolder(View rowView, IViewHolderClicks listener) {
            super(rowView);
            clickListener = listener;
            songName = (TextView) rowView.findViewById(R.id.recycler_songName);
            voteCount = (TextView) rowView.findViewById(R.id.recycler_voteCount);
            artistName = (TextView) rowView.findViewById(R.id.recycler_artistName);
            rowUpvoteButton = (Button) rowView.findViewById(R.id.recycler_upvotebutton);
            rowDownvoteButton = (Button) rowView.findViewById(R.id.recycler_downvotebutton);

            rowDownvoteButton.setOnClickListener(this);
            rowUpvoteButton.setOnClickListener(this);
            songName.setOnClickListener(this);
            rowView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v instanceof Button) {
                //todo replace this string with one from shared preferences or something
                if (((Button) v).getText().toString().equals("Upvote")) {
                    clickListener.onUpvoteButtonClicked((Button) v);
                } else {
                    clickListener.onDownvoteButtonClicked((Button) v);
                }
            } else if (v instanceof TextView) {
                clickListener.onSongNameClicker((TextView) v);
            }

        }

        //interface that is called when an item is clicked in the recyclerview
        public interface IViewHolderClicks {

            void onUpvoteButtonClicked(Button button);

            void onDownvoteButtonClicked(Button button);

            void onSongNameClicker(TextView textView);
        }
    }

    //inflate the xml, create viewholder and return it
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

//        View rowView = inflater.inflate(R.layout.recycler_row, parent, false);
        View rowView = inflater.inflate(R.layout.recycler_row, parent, false);

        ViewHolder viewHolder = new ViewHolder(rowView, new ViewHolder.IViewHolderClicks() {
            @Override
            public void onUpvoteButtonClicked(Button button) {
                //here we should send an request to the server to add an up vote to this song
                Log.d(TAG, "upvotebutton clicked");

            }

            @Override
            public void onDownvoteButtonClicked(Button button) {
                //here we should send an request to the server to add downvote
                Log.d(TAG, "downvotebutton clicked");
            }

            @Override
            public void onSongNameClicker(TextView textView) {
                //here we should start a new activity to display details of a song
                Log.d(TAG, "song name clicked");
            }
        });

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {


        try {
            JSONObject songNameObject = songDataArrays.get(position).getJSONObject(0);
//            dataset.add(songName.getString("value"));
            TextView textView = holder.songName;
            textView.setText(songNameObject.getString("value"));
            textView = holder.voteCount;
            JSONObject voteCountObject = songDataArrays.get(position).getJSONObject(2);
            textView.setText(voteCountObject.getString("value"));
            textView = holder.artistName;
            JSONObject artistName = songDataArrays.get(position).getJSONObject(1);
            textView.setText(artistName.getString("value"));

            final String[] params = new String[2];
            params[0] = MainActivity.URL +  songVoteURLs.get(holder.getAdapterPosition());
            Log.d(TAG, "url used: " + params);

            holder.rowUpvoteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d(TAG, "upvote button clicked on position " + holder.getAdapterPosition());
                    params[1] = generateUpvoteBody();
                    new PostVoteTask().execute(params);

                }
            });

            holder.rowDownvoteButton.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Log.d(TAG, "downvote button clicked on position" + holder.getAdapterPosition());
                    params[1] = generateDownvoteBody();
                    new PostVoteTask().execute(params);

                }
            });


        }
        catch(JSONException e ){
            Log.d(TAG, "JSonException in recycleradapter");
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return songDataArrays.size();
    }

    private String generateUpvoteBody(){

        return "{" +
                "\"type\": \"upvote\"," +
                "\"voter_id\": \"" + MainActivity.USER_ID + "\"" +
                "}";
    }

    private String generateDownvoteBody(){

        return "{" +
                "\"type\": \"downvote\"," +
                "\"voter_id\": \"" + MainActivity.USER_ID + "\"" +
                "}";
    }

    //todo move this to more sensible place
    /**
     * Async task for sending a vote to the server
     * params for .execute should include an array of strings
     * that has URL of the server as the first element
     * and the body of the request as the second element
     */
    private class PostVoteTask extends AsyncTask<String, Void, String>{

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
}

