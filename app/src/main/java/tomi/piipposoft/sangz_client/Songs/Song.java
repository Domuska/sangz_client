package tomi.piipposoft.sangz_client.Songs;

/**
 * Created by Domu on 28-May-16.
 */
public class Song {

    private String songName;
    private String artistName;
    private String albumName;
    private String ID;
    private String url;

    public Song(String song, String artist, String album, String url, String ID){
        this.songName = song;
        this.artistName = artist;
        this.albumName = album;
        this.url = url;
        this.ID = ID;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getUrl() {
        return url;
    }

    public String getID() {
        return ID;
    }

}
