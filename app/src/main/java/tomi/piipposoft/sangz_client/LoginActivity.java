package tomi.piipposoft.sangz_client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import tomi.piipposoft.sangz_client.Playlist.PlaylistActivity;

public class LoginActivity extends AppCompatActivity implements WebInterface.IConsumeData{


    private final String TAG = "LoginActivity";
    private final String DEFAULT_SHARED_PREF = "noValue";
    private final String DEFAULT_URL = "http://192.168.1.95:5000";

    private String user_resource_URL = "/sangz/api/users/";
    private String URL;
    private String userName;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TextInputEditText userNameEditText = (TextInputEditText) findViewById(R.id.login_enter_name_field);
        final TextInputEditText ipEditText =(TextInputEditText) findViewById(R.id.login_enter_ip_field);
        Button loginButton = (Button) findViewById(R.id.login_sign_in);

        prefs = this.getSharedPreferences(
                getResources().getString(R.string.sharedPreferences),
                Context.MODE_PRIVATE
        );



        URL = prefs.getString(
                getResources().getString(R.string.sharedPreferencesUrlKey),
                DEFAULT_SHARED_PREF
        );

        if(URL.equals(DEFAULT_SHARED_PREF)) {

            prefs.edit().putString(
                    getResources().getString(R.string.sharedPreferencesUrlKey),
                    this.DEFAULT_URL)
                    .apply();
        }



        if (loginButton != null) {
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        if (ipEditText != null && !(ipEditText.getText().toString().isEmpty())) {


                            java.net.URL url = new URL(ipEditText.getText().toString());

                            prefs.edit().putString(
                                    getResources().getString(R.string.sharedPreferencesUrlKey),
                                    ipEditText.getText().toString()
                            )
                                    .apply();
                        }

                        String server_URL = prefs.getString(
                                getResources().getString(R.string.sharedPreferencesUrlKey),
                                ""
                        );
                        Log.d(TAG, "server_URL:" + server_URL);
                        userName = userNameEditText.getText().toString();
                        new Utils.GetDataTask().setCallingActivity(LoginActivity.this).execute(server_URL + user_resource_URL);
                    }
                    catch (MalformedURLException e){
                        e.printStackTrace();
                        Snackbar snackbar = Snackbar.make(
                                v,
                                LoginActivity.this.getResources().getString(R.string.login_snackbar_text),
                                Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
            });
        }


    }

    @Override
    public void consumeData(String JSONString) {
        Log.d(TAG, "Got data: " + JSONString);

        try {
            JSONObject collection = new JSONObject(JSONString).getJSONObject("collection");
            JSONArray items = collection.getJSONArray("items");

            for(int i = 0; i < items.length();i++){

                JSONObject object = items.getJSONObject(i);
                JSONArray data = object.getJSONArray("data");

                //todo figure way to more reliably get user name, here we rely on server always returning name object as 2nd object in the array
                String name = data.getJSONObject(1).getString("value");

                if (name.equals(this.userName)){

                    String userID = data.getJSONObject(0).getString("value");
                    Log.d(TAG, "username matched on server, putting to prefs ID:" + userID);

                    this.prefs = this.getSharedPreferences(
                            getResources().getString(R.string.sharedPreferences),
                            Context.MODE_PRIVATE
                    );

                    prefs.edit().putString(
                            getResources().getString(R.string.sharedPreferencesUserIDKey),
                            userID)
                            .apply();
                }
            }

            Intent i = new Intent(LoginActivity.this, PlaylistActivity.class);
            startActivity(i);
        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }
}
