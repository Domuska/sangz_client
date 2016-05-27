package tomi.piipposoft.sangz_client.Songs;

/**
 * Created by Domu on 28-May-16.
 */
public class SingleSong {

    private String songName;
    private String artistName;
    private String albumName;



    private String url;

    public SingleSong(String song, String artist, String album, String url){
        this.songName = song;
        this.artistName = artist;
        this.albumName = album;
        this.url = url;
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

}
