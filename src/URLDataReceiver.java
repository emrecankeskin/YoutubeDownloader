import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class URLDataReceiver {

    BufferedReader reader;
    StringBuilder output = new StringBuilder();

    public String getUrlData(String urlLink) {
        try{

            output.delete(0,output.length());
            URL url = new URL(urlLink);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159");
            urlConnection.setUseCaches(false);
            urlConnection.setDoInput(true);
            urlConnection.setConnectTimeout(Integer.MAX_VALUE);

            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while((line = reader.readLine()) != null){
                output.append(line);
            }
            return output.toString();

        }catch (IOException e){
            return null;
        }
    }
}
