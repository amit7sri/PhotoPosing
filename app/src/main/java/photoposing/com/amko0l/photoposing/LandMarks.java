package photoposing.com.amko0l.photoposing;

/**
 * Created by amko0l on 4/23/2017.
 */

public class LandMarks {
    String url;
    String latitude;
    String longitute;
    int likecount;

    public LandMarks(){

    }

    public LandMarks(String url, String latitude, String longitute){
        this.url = url;
        this.latitude = latitude;
        this.longitute = longitute;
        this.likecount = 0;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitute() {
        return longitute;
    }

    public void setLongitute(String longitute) {
        this.longitute = longitute;
    }
}
