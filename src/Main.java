import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static final String AUDIO_MEDIUM = "AUDIO_QUALITY_MEDIUM";
    public static final String AUDIO_LOW = "AUDIO_QUALITY_LOW";

    public static Thread thread;

    public static URLDataReceiver urlDataReceiver;
    public static Downloader downloader;
    public static Decrypter decrypter;
    public static Streams streams;
    public static Merger merger;

    public static String youtubeLink;
    public static String youtubeUrlSource;
    public static String videoQuality;
    public static String videoFileName;
    public static String videoUrl;

    public static JFrame frame;
    public static JPanel panel;
    public static JLabel urlLabel;
    public static JLabel qualityLabel;
    public static JTextField urlLinkField;
    public static JTextField videoQualityField;
    public static JTextArea textArea;
    public static JButton downloadButton;
    public static JScrollPane scroll;

    public static ArrayList<VideoFormat> adaptiveFormatMap = new ArrayList<>();
    public static ArrayList<AudioFormat> audioMap = new ArrayList<>();


    public static void main(String[] args) throws IOException {

        urlDataReceiver = new URLDataReceiver();
        downloader = new Downloader();

        frame = new JFrame("Downloader");
        panel = new JPanel();

        urlLabel = new JLabel("Enter URL");
        qualityLabel = new JLabel("Enter Quality");
        urlLinkField = new JTextField(25); // accepts upto 10 characters
        videoQualityField = new JTextField(8);
        textArea = new JTextArea();
        downloadButton = new JButton("Download");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        scroll = new JScrollPane(textArea);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        panel.add(urlLabel);
        panel.add(urlLinkField);
        panel.add(qualityLabel);
        panel.add(videoQualityField);
        panel.add(downloadButton);

        frame.getContentPane().add(scroll);
        frame.getContentPane().add(BorderLayout.SOUTH, panel);
        frame.setVisible(true);
        //youtubeLink = args[0];
        //videoQuality = args[1];
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                thread =new Thread(new Runnable() {
                    @Override
                    public void run() {
                        youtubeLink = urlLinkField.getText();
                        videoQuality = videoQualityField.getText();
                        youtubeUrlSource = urlDataReceiver.getUrlData(youtubeLink);
                        decrypter = new Decrypter(youtubeUrlSource);
                        decrypter.findFunctionTypes(urlDataReceiver.getUrlData(decrypter.getBaseJs()));

                        streams = new Streams(youtubeUrlSource,decrypter);
                        streams.parseJson();
                        adaptiveFormatMap = streams.getAdaptiveVideoFormat();
                        audioMap = streams.getAudioFormat();
                        videoFileName = adaptiveFormatMap.get(0).videoName;

                        for(int i=0; i<adaptiveFormatMap.size(); i++){
                            if(adaptiveFormatMap.get(i).mimeType.contains("avc") && adaptiveFormatMap.get(i).quality.equals(videoQuality)){
                                videoUrl = adaptiveFormatMap.get(i).url;
                            }
                        }

                        // audio 0
                        // video 2
                        // only avc and mp4a codec muxing
                        try {
                            textArea.append("[INFO] DOWNLOAD STARTED FOR "+videoFileName);
                            System.out.println("[INFO] DOWNLOAD STARTED FOR "+videoFileName);

                            downloader.downloadFile(videoUrl, "video.mp4");
                            downloader.downloadFile(audioMap.get(0).url, "audio.mp4");
                        }catch (IOException e){
                            //System.out.println("[ERROR] HTTP REQUEST FAILED. TRY AGAIN");
                            textArea.append("[ERROR] INTERNET CONNECTION PROBLEM!!\n");
                            e.printStackTrace();
                        }
                        textArea.append("[INFO] VIDEO DOWNLOAD FINISHED \n");
                        System.out.println("[LOG] VIDEO DOWNLOAD FINISHED ");
                        System.out.println("[LOG] MERGE STARTED");
                        textArea.append("[INFO] MERGE STARTED\n");
                        merger = new Merger(videoFileName,"video.mp4","audio.mp4");
                        System.out.println("[LOG] MERGE FINISHED");
                        textArea.append("[INFO] MERGE FINISHED\n");
                    }
                });
                thread.start();
            }
        });



    }
}
