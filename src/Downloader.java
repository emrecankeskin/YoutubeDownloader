import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.file.Paths;


public class Downloader {
    private String fileName;
    private final String FILE_PATH = Paths.get("").toAbsolutePath()+"/";
    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.98 Safari/537.36";
    private int fileLength;
    private final int BUFFER_SIZE = 8*8*1024;


    //need to fix file path
    public void downloadFile(String urlName,String fileName) throws IOException {
        URL url = new URL(urlName);
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("User-Agent",USER_AGENT);
        urlConnection.connect();

        FileOutputStream fileOutputStream = new FileOutputStream(FILE_PATH+fileName);
        BufferedInputStream bis = new BufferedInputStream(urlConnection.getInputStream());
        BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

        byte[] bytes = new byte[BUFFER_SIZE];
        int count;

        while((count = bis.read(bytes,0,BUFFER_SIZE)) != -1){

            bos.write(bytes,0,count);

        }
        bos.close();
        bis.close();

    }



    public int getFileLength(){
        return fileLength;
    }

    public String getFileName(){
        return this.fileName;
    }
}
