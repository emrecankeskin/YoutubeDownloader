import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Streams {

    public String urlSource;

    public Decrypter decrypter;

    //You can access the video items with these objects
    public ArrayList<VideoFormat> adaptiveFormatMap = new ArrayList<>();
    public ArrayList<AudioFormat> audioMap = new ArrayList<>();


    public JSONObject ytPlayerResponse;
    public JSONObject streamingData;
    public JSONArray formats;
    public JSONArray adaptiveFormats;

    Matcher matcher;



    //public final Pattern patYouTubePageLink = Pattern.compile("(http|https)://(www\\.|m.|)youtube\\.com/watch\\?v=(.+?)( |\\z|&)");
    //public final Pattern patYouTubeShortLink = Pattern.compile("(http|https)://(www\\.|)youtu.be/(.+?)( |\\z|&)");


    //player response videol jsonlarını döndürüyor
    //patbaseJsSource encode eden fonksiyonları döndürüyor
    private Pattern patBaseJsSource = Pattern.compile("function\\(\\w+\\)\\{[\\w=.(\")]+;([\\w=.,(\");]*);return \\w+\\.join[(\")]*\\}\\;");
    public final Pattern patPlayerResponse = Pattern.compile("var ytInitialPlayerResponse\\s*=\\s*(\\{.+?\\})\\s*;");

    public Streams(String urlSource,Decrypter decrypter ) throws JSONException {

        this.urlSource = urlSource;
        this.decrypter = decrypter;
        //parseJson();
    }




    // setting json to our jsonobject's and array's

    //hashmap for adaptiveformat's list
    public ArrayList<VideoFormat>  getAdaptiveVideoFormat() throws JSONException {
        HashMap<String, String> linkData = new HashMap<>();
        String url;
        String quality;
        String videoName;
        String mimeType;

        for (int i = 0; i < adaptiveFormats.length(); i++) {

            if (adaptiveFormats.getJSONObject(i).has("url")) {
                url = adaptiveFormats.getJSONObject(i).getString("url");
            } else {

                String signature = "";
                String[] signatureWithUrl = decrypter.unicodeDecode(adaptiveFormats.getJSONObject(i).getString("signatureCipher")).split("&");

                //Map<String, String> linkData = new HashMap<>();
                for (String j : signatureWithUrl) {
                    String[] k = j.split("=");
                    linkData.put(k[0], k[1]);
                }
                signature = decrypter.decode(decrypter.urlDecode(linkData.get("s")));
                url = decrypter.urlDecode(decrypter.urlDecode(linkData.get("url"))) + '&' + linkData.get("sp") + '=' + signature;

            }
            try {
                quality = (String) adaptiveFormats.getJSONObject(i).get("qualityLabel");
            } catch (JSONException e) {
                continue;
            }
            mimeType = (String) adaptiveFormats.getJSONObject(i).get("mimeType");
            videoName = (String)(ytPlayerResponse.getJSONObject("microformat").getJSONObject("playerMicroformatRenderer").getJSONObject("title").get("simpleText"));
            adaptiveFormatMap.add(new VideoFormat(quality,url,videoName,mimeType));

        }
        return adaptiveFormatMap;
    }



    public ArrayList<AudioFormat>  getAudioFormat() throws JSONException {

        HashMap<String, String> linkData = new HashMap<>();
        String audioQuality;
        String url;
        String signature;
        String mimeType;

        for (int i = 0; i < adaptiveFormats.length(); i++) {

            //for only parsing audioQuality from json
            if (adaptiveFormats.getJSONObject(i).has("audioQuality")) {
                audioQuality = adaptiveFormats.getJSONObject(i).getString("audioQuality");
            } else {
                continue;
            }


            //sometimes url is not placed in json so i need to decode signatureCipher
            if (adaptiveFormats.getJSONObject(i).has("url")) {

                url = adaptiveFormats.getJSONObject(i).getString("url");

            } else {

                /*
                 * signatureWithUrl contains s sig and url for decrypter
                 *
                 * I am casting to s=[SIGNATURE] sig=[SIG?] url=[URL] with map linkData object
                 *
                 * decoding signature with decoding also it has to be urlDecode's return string for
                 *
                 * after that url concate for putting audio map
                 * */
                String[] signatureWithUrl = decrypter.unicodeDecode(adaptiveFormats.getJSONObject(i).getString("signatureCipher")).split("&");

                //Map<String, String> linkData = new HashMap<>();
                for (String j : signatureWithUrl) {
                    String[] k = j.split("=");
                    linkData.put(k[0], k[1]);
                }
                signature = decrypter.decode(decrypter.urlDecode(linkData.get("s")));
                url = decrypter.urlDecode(decrypter.urlDecode(linkData.get("url"))) + '&' + linkData.get("sp") + '=' + signature;
            }
            mimeType= (String) adaptiveFormats.getJSONObject(i).get("mimeType");
            audioMap.add(new AudioFormat(audioQuality,url,mimeType));

        }
        return audioMap;
    }


    /**
     * Decoding signatureCipher's signature with some algorithm
     * Disclaimer youtube sometimes changes its algorithm but pattern is very recognizable
     *
     * */


    public void parseJson() throws JSONException {
        matcher = patPlayerResponse.matcher(this.urlSource);
        if (matcher.find()) {
            ytPlayerResponse = new JSONObject(matcher.group(1));
            streamingData = ytPlayerResponse.getJSONObject("streamingData");
            formats = streamingData.getJSONArray("formats");
            adaptiveFormats = streamingData.getJSONArray("adaptiveFormats");
        }
    }

    public JSONArray getFormats(){
        return formats;
    }

    public JSONArray getAdaptiveFormats(){
        return adaptiveFormats;
    }



}
