package tomi.piipposoft.sangz_client.Playlist;

/**
 * Created by Domu on 21-May-16.
 *
 * Interface used between activities and HTTP methods
 * in Utils.java
 */
public class WebInterface {

    public interface CallerActivity {

        void consumeData(String JSONString);
    }
}
