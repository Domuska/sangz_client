package tomi.piipposoft.sangz_client;

/**
 * Created by Domu on 29-May-16.
 */
public class JsonGenerator {

    //todo change this to use JSONObjects

    /**
     * Conveniency method for generating the body that is needed to send an up vote to the server
     * @param user_id currently logged user's ID
     * @return string containing the body that is used to send an up vote
     */
    public static String generateUpvoteBody(String user_id){

        return "{" +
                "\"type\": \"upvote\"," +
                "\"voter_id\": \"" + user_id + "\"" +
                "}";
    }

    /**
     * Conveniency method for generating the body that is needed to send an down vote to the server
     * @param user_id currently logged user's ID
     * @return string containing the body that is used to send an down vote
     */
    public static String generateDownvoteBody(String user_id){

        return "{" +
                "\"type\": \"downvote\"," +
                "\"voter_id\": \"" + user_id + "\"" +
                "}";
    }
}
