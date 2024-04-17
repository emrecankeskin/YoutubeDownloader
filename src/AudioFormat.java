public class AudioFormat {

    public final String audioQuality;
    public final String url;
    public final String mimeType;

    public AudioFormat(String audioQuality, String url,String mimeType){
        this.audioQuality = audioQuality;
        this.url = url;
        this.mimeType = mimeType;
    }

    public String getUrl() {
        return url;
    }


    public String getAudioQuality() {
        return audioQuality;
    }
    public String getMimeType(){
        return mimeType;
    }

}
