public class VideoFormat {

    //create videoformat object for hashmap<String: itag, format object>
    public final String url;
    public final String videoName;
    public final String quality;
    public final String mimeType;

    public VideoFormat(String quality,String url, String videoName,String mimeType){
        this.quality = quality;
        this.url = url;
        this.videoName = videoName;
        this.mimeType = mimeType;
    }



    public String getUrl() {
        return url;
    }

    public String getVideoName() {
        return videoName;
    }

    public String getQuality(){
        return quality;
    }
    public String getMimeType(){
        return mimeType;
    }
}
