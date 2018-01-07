package pranshusinha.com.dhun;

/**
 * Created by PranshuSinha on 13/02/16.
 */
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

public class SongManager {
    // SDCard Path
    final String MEDIA_PATH = new String("/storage/emulated/0/Music/");
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

    // Constructor
    public SongManager(){

    }

    /**
     * Function to read all mp3 files from sdcard
     * and store the details in ArrayList
     * */
    public ArrayList<HashMap<String, String>> getPlayList(){

        File home = new File(MEDIA_PATH);
        System.out.println(home.listFiles());

        if (home.listFiles(new FileExtensionFilter()).length > 0) {
            for (File file : home.listFiles(new FileExtensionFilter())) {
                HashMap<String, String> song = new HashMap<String, String>();
                song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
                song.put("songPath", file.getPath());

                // Adding each song to SongList
                songsList.add(song);
            }
        }else{HashMap<String, String> song = new HashMap<String, String>();song.put("songTitle","pranshu");song.put("songPath","/storage/emulated/0/Music/Nucleya - Bass.mp3");songsList.add(song);}
        // return songs list array
        return songsList;
    }

    /**
     * Class to filter files which are having .mp3 extension
     * */
    class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.endsWith(".mp3") || name.endsWith(".MP3"));
        }
    }
}

