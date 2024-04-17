import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.io.File;



/*
* Still not working properly .
*
* Merging audio and video can be done with ffmpeg wrapping.
*
*
* */
public class Merger {
    public Merger(String fileName, String videoFile, String audioFile) {
        Movie video;
        Movie audio;
        File vidFile;
        File audFile;

        try {
            video = MovieCreator.build(videoFile);
            audio = MovieCreator.build(audioFile);
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            return;
        }

        Movie movie = new Movie();
        try {

            movie.addTrack(new AppendTrack(video.getTracks().get(0)));
            movie.addTrack(new AppendTrack(audio.getTracks().get(0)));
            Container out = new DefaultMp4Builder().build(movie);
            FileChannel fc = new FileOutputStream(fileName+".mp4").getChannel();
            out.writeContainer(fc);
            fc.close();
            vidFile = new File(videoFile);
            audFile = new File(audioFile);
            vidFile.delete();
            audFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

}