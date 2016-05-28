package tomi.piipposoft.sangz_client;

/**
 * Created by Domu on 21-May-16.
 *
 * Interface used between activities and HTTP methods
 * in Utils.java
 */
public class WebInterface {

    public interface IConsumeData {
        void consumeData(String JSONString);
    }

    public interface IDataChanged{
        void notifyServerDataChanged();
    }

    public interface IRemovedData{
        void songDeleted();
    }
}
