package tomi.piipposoft.sangz_client;

/**
 * Created by Domu on 21-May-16.
 *
 * Interface used between activities and HTTP methods
 * in Utils.java
 */
public class WebInterface {

    public interface INewData {

        void consumeData(String JSONString);
        void notifyDataChanged();
    }

    public interface IRemovedData{

        void songDeleted();
    }
}
